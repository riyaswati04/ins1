package com.ia.scheduler.jobs;

import static com.ia.common.Handlers.kJobScheduler;
import static com.ia.common.Handlers.logger;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ia.scheduler.ICronJob;
import com.ia.scheduler.JobStatistics;
import com.ia.scheduler.KJobScheduler;

public abstract class NonNativeCronJob implements ICronJob {

    private static int jobExecutionIdCounter = 1000000;

    protected String jobType;

    private int jobExecutionId = 0;

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final JobDetail jobDetail = context.getJobDetail();
        final String jobGroup = jobDetail.getGroup();
        final JobDataMap dataMap = jobDetail.getJobDataMap();
        int exitCode = 1;

        // For system jobs (cron jobs), jobType is derived from jobName
        jobType = context.getJobDetail().getName();
        final Date nextFireTime = jobGroup.equals(KJobScheduler.SYSTEM_JOB_GROUP)
                ? context.getTrigger().getNextFireTime()
                : new Date(Long.parseLong(dataMap.getString(PARAM_NEXT_FIRE_TIME)));
        exitCode = runCronJob(dataMap);
        context.setResult(new Integer(exitCode));
        // Save nextFireTime (used on Tomcat restart)
        kJobScheduler.storeNextFireTime(jobType, nextFireTime);
    }

    protected int getJobExecutionId() {
        if (jobExecutionId == 0) {
            jobExecutionId = ++jobExecutionIdCounter;
        }
        return jobExecutionId;
    }

    protected String getLogMessage(final String message) {
        return message + " [jobType=" + jobType + "]";
    }

    public void logDebug(final String message) {
        logger.debug(getLogMessage(message));
    }

    public void logError(final String message) {
        logger.error(getLogMessage(message));
    }

    public void logError(final String message, final Exception exception) {
        logger.error(getLogMessage(message), exception);
    }

    public void logInfo(final String message) {
        logger.info(getLogMessage(message));
    }

    public void logWarn(final String message) {
        logger.warn(getLogMessage(message));
    }

    public void logWarn(final String message, final Exception exception) {
        logger.warn(getLogMessage(message), exception);
    }

    @SuppressWarnings("finally")
    @Override
    public int runCronJob(final JobDataMap dataMap) {
        int retCode = 1;
        final long startTime = System.currentTimeMillis();
        try {
            retCode = runNonNativeCommand(dataMap);
        }
        catch (final Exception e) {
            logError("Exception in NonNativeCronJob#runCronJob", e);

        }
        finally {
            JobStatistics.getJobStatistics().jobExecuted(jobType, getJobExecutionId(),
                    (int) ((System.currentTimeMillis() - startTime) / 1000));
            return retCode;
        }
    }

    protected abstract int runNonNativeCommand(JobDataMap dataMap);

    @Override
    public void setJobType(final String jobType) {
        this.jobType = jobType;
    }
}
