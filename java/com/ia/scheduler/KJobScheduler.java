package com.ia.scheduler;

import static com.ia.common.IAProperties.autofetchTimeoutLimit;
import static com.ia.common.IAProperties.perlCommand;
import static com.ia.common.IAProperties.productionSystem;
import static com.ia.common.IAProperties.pythonCommand;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.scheduler.NativeJobHandler.DATA_DIR_NAME;
import static com.ia.scheduler.NativeJobHandler.JOB_NAME;
import static com.ia.scheduler.NativeJobHandler.SCRIPT_NAME;
import static com.ia.scheduler.NativeJobHandler.SCRIPT_PARAMS;
import static com.ia.scheduler.NativeJobHandler.TOKEN_NAME;
import static com.ia.util.DateUtil.MYSQL_TIMESTAMP_FORMAT;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.jooq.DSLContext;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

import com.google.inject.Inject;
import com.ia.log.Logger;
import com.ia.scheduler.enums.JOB_EXECUTOR;

public final class KJobScheduler {

    private static Scheduler sched;

    private static Scheduler cronSched;

    private static HashMap<Long, Process> processMap; // startTime=>Process

    private static HashMap<String, IAJobInfo> jobInfos;

    private static Timer watchdog;

    private static int counter = 0;

    private static Logger logger = getLogger(KJobScheduler.class);

    // Job Groups
    public static String SYSTEM_JOB_GROUP = "ia";

    public static IAJobInfo getJobInfo(final String jobType) {
        return jobInfos.get(jobType);
    }

    private final DSLContext dsl;

    private final JobFactory jobFactory;

    @Inject
    public KJobScheduler(final JobFactory jobFactory, final DSLContext dsl) {
        super();
        this.jobFactory = jobFactory;
        this.dsl = dsl;
    }

    public IAJobStates getSchedulerState() {
        final IAJobStates states = new IAJobStates();
        // jobParametersService shuts down before KJobScheduler.
        // Therefore it will be null during KJobScheduler#shutdown.

        getSchedulerStateForAScheduler(states, sched);

        getSchedulerStateForAScheduler(states, cronSched);

        // RunningNativeJobs.populateJobRunningTime(states);

        states.collectStats();
        return states;
    }

    private void getSchedulerStateForAScheduler(final IAJobStates states,
            final Scheduler scheduler) {
        try {
            final String[] jobGroupNames = scheduler.getJobGroupNames();
            for (final String jobGroupName : jobGroupNames) {
                final String[] jobNames = scheduler.getJobNames(jobGroupName);
                for (final String jobName : jobNames) {
                    final JobDetail jobDetail = scheduler.getJobDetail(jobName, jobGroupName);
                    states.populateJobDetail(jobName, jobGroupName,
                            jobDetail.getJobClass().getName());
                }
            }
            final String[] triggerGroupNames = scheduler.getTriggerGroupNames();
            for (final String triggerGroupName : triggerGroupNames) {
                final String[] triggerNames = scheduler.getTriggerNames(triggerGroupName);
                for (final String triggerName : triggerNames) {
                    final Trigger trigger = scheduler.getTrigger(triggerName, triggerGroupName);
                    if (trigger.getClass().getName().equals("org.quartz.SimpleTrigger")) {
                        final SimpleTrigger t = (SimpleTrigger) trigger;
                        final String startTimeStr = t.getStartTime() != null
                                ? MYSQL_TIMESTAMP_FORMAT.format(t.getStartTime()) : "";
                        final String nextFireTimeStr = t.getNextFireTime() != null
                                ? MYSQL_TIMESTAMP_FORMAT.format(t.getNextFireTime()) : "";
                        String triggerDetails =
                                isEmpty(nextFireTimeStr) ? "startTime=" + startTimeStr + ", " : "";
                        triggerDetails += "repeatCount=" + t.getRepeatCount() + ", repeatInterval="
                                + t.getRepeatInterval();
                        if (!isEmpty(nextFireTimeStr)) {
                            triggerDetails += ", nextFireTime=" + nextFireTimeStr;
                        }

                        states.populateJobTrigger("Simple", triggerName, triggerGroupName,
                                triggerDetails);
                    }
                    else if (trigger.getClass().getName().equals("org.quartz.CronTrigger")) {
                        final CronTrigger t = (CronTrigger) trigger;
                        final String triggerDetails = "startTime="
                                + (t.getStartTime() != null
                                        ? MYSQL_TIMESTAMP_FORMAT.format(t.getStartTime()) : "")
                                + ", cronExpr=" + t.getCronExpression() + ", nextFireTime="
                                + (t.getNextFireTime() != null
                                        ? MYSQL_TIMESTAMP_FORMAT.format(t.getNextFireTime()) : "");

                        states.populateJobTrigger("Cron", triggerName, triggerGroupName,
                                triggerDetails);
                    }
                }
            }
        }
        catch (final SchedulerException e) {
            logger.warn("Unable to get scheduler state", e);
        }
    }

    /**
     * Kill all jobs which are running for more than autofetchTimeoutLimit minutes.
     */
    private void killLongRunningJobs() {
        final long now = System.currentTimeMillis();
        final long tolerance = autofetchTimeoutLimit * 60 * 1000L;
        final ArrayList<Long> startTimes = new ArrayList<Long>();
        synchronized (processMap) {
            for (final long startTime : processMap.keySet()) {
                // logger.debug("now = " + now + ", startTime = " + startTime + ", tolerance = " +
                // tolerance);
                if (now - startTime < tolerance) {
                    continue;
                }
                final Process p = processMap.get(startTime);
                // logger.debug("To destroy process " + p + ", started at " + new Date(startTime));
                try {
                    if (p != null) {
                        logger.warn(
                                "Destroying process " + p + ", started at " + new Date(startTime));
                        p.destroy();
                    }
                }
                catch (final Exception e) {
                    logger.error("Exception in process destroy", e);
                    logger.warn("Destroying process " + p + " again, started at "
                            + new Date(startTime));
                    try {
                        p.destroy();
                    }
                    catch (final Exception ee) { /* ignore */
                    }
                }
                finally {
                    startTimes.add(startTime);
                }
            }
            if (startTimes.size() == 0) {
                return;
            }
            for (final long startTime : startTimes) {
                processMap.remove(startTime);
            }
        }
    }

    public void processEnded(final long startTime) {
        synchronized (processMap) {
            processMap.remove(startTime);
        }
    }

    public long processStarted(final Process p) {
        final long startTime = System.currentTimeMillis();
        synchronized (processMap) {
            processMap.put(startTime, p);
        }
        return startTime;
    }

    /**
     * This method is called once at startup and later from Admin. Subsequent calls will NOT replace
     * existing jobs.
     */
    public String refreshJobs() {
        final String jobNames = "";
        // read database table k_jobs and create job metadata (KJobInfo)
        return "";
    }

    /**
     * The main method which schedules an auto-fetch job.
     */
    public void scheduleJob(final String scriptName, final String tempDir, final Object params,
            final String token, final Class<? extends NativeJob> handlerClass) {
        final Calendar startTime = Calendar.getInstance();
        final String jobName = "job#" + ++counter;

        final SimpleTrigger t = new SimpleTrigger(jobName, scriptName, startTime.getTime());
        final JobDetail jobDetail = new JobDetail(jobName, scriptName, handlerClass);
        // set parameters
        final JobDataMap dataMap = jobDetail.getJobDataMap();
        dataMap.put(DATA_DIR_NAME, tempDir);
        dataMap.put(SCRIPT_PARAMS, params);
        dataMap.put(TOKEN_NAME, token);
        dataMap.put(SCRIPT_NAME, scriptName);
        dataMap.put(JOB_NAME, jobName);

        try {
            sched.scheduleJob(jobDetail, t);
        }
        catch (final Exception e) {
            logger.error("Unable to schedule " + jobName, e);
        }
    }

    /**
     * Schedule a system cron job. The semantics of nextFireTime is as follows. If nextFireTime is
     * 1, then it is ignored and the cron job is freshly scheduled. For all other values, the
     * current time is checked, the job is fired once if the nextFireTime has passed, and scheduled
     * for future firing as per the cron expression.
     */
    private void scheduleSystemCronJob(final IAJobInfo ji, final long nextFireTime) {
        final String cronExp = ji.getCronExp();
        final String jobType = ji.getJobType();
        String jobGroup = SYSTEM_JOB_GROUP;
        Trigger t;
        try {
            logger.info("Scheduling cron job " + jobType + " : " + cronExp);
            JobDetail jobDetail = new JobDetail(jobType, jobGroup, ji.getHandler());
            JobDataMap dataMap = jobDetail.getJobDataMap();
            dataMap.put(NativeJob.SCRIPT_NAME, ji.getScript());
            t = new CronTrigger(jobType, jobGroup, jobType, jobGroup, cronExp);
            t.setPriority(10); // In Quartz, all jobs will have the same priority
            cronSched.scheduleJob(jobDetail, t);

            // If scheduled fire time has passed, fire it once
            final Date curTime = new Date();
            final Date schFireTime = new Date(nextFireTime);
            if (nextFireTime != 1 && curTime.after(schFireTime)) {
                // Group name is changed so that there is no clash between
                // this job+trigger with the previous job+trigger.
                jobGroup += '_';
                jobDetail = new JobDetail(jobType, jobGroup, ji.getHandler());
                dataMap = jobDetail.getJobDataMap();
                dataMap.put(SCRIPT_NAME, ji.getScript());
                final Date cronNextFireTime = t.getNextFireTime();
                dataMap.putAsString("NEXT_FIRE_TIME",
                        cronNextFireTime == null ? 0L : cronNextFireTime.getTime());

                final long ts = TriggerUtils.getNextGivenMinuteDate(null, 2).getTime();
                t = new SimpleTrigger(jobType, jobGroup, jobType, jobGroup, new Date(ts), null, 0,
                        0);

                /* In Quartz, all jobs will have the same priority */
                t.setPriority(10);
                final Date ft = cronSched.scheduleJob(jobDetail, t);
                logger.info("Scheduling misfired system job " + jobType + " to run at " + ft);
            }
        }
        catch (final ParseException e) {
            logger.error(
                    "Unable to parse cron expression \"" + cronExp + "\" [jobType=" + jobType + "]",
                    e);
        }
        catch (final SchedulerException e) {
            logger.error("Unable to schedule job [jobType=" + jobType + "]", e);
        }
    }

    public void shutdown() {
        watchdog.cancel();
        watchdog = null;
        logger.info("---------- Dumping scheduler state ----------");
        getSchedulerState().dump();
        logger.info("------- Shutting down Scheduler ----------------------");
        try {
            sched.shutdown();
            cronSched.shutdown();
        }
        catch (final SchedulerException e) {
            logger.warn("Unable to shutdown Quartz scheduler", e);
        }
        sched = null;
        cronSched = null;
        jobInfos = null;
    }

    public void start() {
        // start() is called by the servlet at startup or after a shutdown.
        processMap = new HashMap<Long, Process>();

        // start the watchdog
        watchdog = new Timer("JobServer-Watchdog", true); // daemon process
        watchdog.schedule(new TimerTask() {
            @Override
            public void run() {
                killLongRunningJobs();
            }
        }, 0L, 2 * 60 * 1000L); // every 2 minutes

        final SchedulerFactory sf = new StdSchedulerFactory();
        try {
            sched = sf.getScheduler();
            sched.setJobFactory(jobFactory);

            final SchedulerFactory cronSchedulerFactory =
                    new StdSchedulerFactory("quartz.cron.properties");
            /* Cron scheduler */
            cronSched = cronSchedulerFactory.getScheduler();
            cronSched.setJobFactory(jobFactory);
        }
        catch (final SchedulerException e) {
            logger.error("Unable to get Quartz scheduler", e);
            return;
        }
        catch (final Exception e) {
            logger.error("Unable to get quartz scheduler", e);
        }

        try {
            NativeJobHandler.init(pythonCommand, perlCommand);
            NativeJob.init(pythonCommand, perlCommand);
        }
        catch (final IOException e) {
            logger.error("Exception in setting up script execution environment", e);
            return;
        }

        try {
            validateExecutorsPaths();
        }
        catch (final IOException e) {
            logger.error("Exception in setting up script execution environment", e);
            return;
        }

        // Create jobInfos
        jobInfos = new HashMap<String, IAJobInfo>();
        refreshJobs();

        logger.info("------- Starting Scheduler ----------------------");
        try {
            sched.start();
            cronSched.start();
        }
        catch (final SchedulerException e) {
            logger.error("Unable to start Quartz scheduler", e);
        }
    }

    public void startCronJobs() {
        // Look through the cached jobInfos and schedule cronJobs if required

        logger.info("Starting cron jobs");

        for (final String jobType : jobInfos.keySet()) {
            final IAJobInfo ji = jobInfos.get(jobType);
            if ("cron".equals(ji.getScheduleType())) {
                final long nextFireTime = ji.getNextFireTime();
                // Do not fire system jobs if
                // (i) the job is blocked, or
                // (ii) the job has expired, or
                // (iii) developer system (non-production) - Allow MidnightJobs
                if (!ji.isBlocked() && nextFireTime != 0 && productionSystem) {
                    scheduleSystemCronJob(ji, nextFireTime);
                }
                else {
                    logger.warn("Job " + jobType + " is not scheduled with next fire time="
                            + nextFireTime
                            + ". Either it is inactive, expired or a cron running in development environment.");
                }
            }
        }
    }

    public void storeNextFireTime(final String jobType, final Date nextFireTime) {}

    public void validateExecutorsPaths() throws IOException {

        final JOB_EXECUTOR[] jobExecutors = JOB_EXECUTOR.values();

        for (final JOB_EXECUTOR jobExecutor : jobExecutors) {
            /* Skip executor NONE, they dont have any path */
            if (jobExecutor == JOB_EXECUTOR.NONE) {
                continue;
            }

            final String path = jobExecutor.getExecutionPath();

            logger.info("Validating path [" + path + "] for executor " + jobExecutor);

            if (isEmpty(path)) {
                logger.error("Path for executor " + jobExecutor + " is empty, skip validation.");
                continue;
            }

            final File f = new File(path);
            if (!f.exists()) {
                throw new IOException("Command for executor=" + jobExecutor + ", path=" + path
                        + " is not found!");
            }

            if (!f.isFile()) {
                throw new IOException("Command for executor=" + jobExecutor + ", path=" + path
                        + " is not a file!");
            }
        }
    }
}
