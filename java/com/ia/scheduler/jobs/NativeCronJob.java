package com.ia.scheduler.jobs;

import static com.ia.common.Handlers.kJobScheduler;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ia.crypto.CryptHandler;
import com.ia.scheduler.ICronJob;
import com.ia.scheduler.JobStatistics;
import com.ia.scheduler.KJobScheduler;
import com.ia.scheduler.NativeJob;
import com.ia.scheduler.Timeouts;

public class NativeCronJob extends NativeJob implements ICronJob {

    protected int scriptTimeOut = Timeouts.CRON;

    public NativeCronJob(final CryptHandler cryptHandler, final KJobScheduler kJobScheduler) {
        super(cryptHandler, kJobScheduler);
    }

    @Override
    protected final int abortPreProcess() {
        jobFailed(JobStatistics.PREPROCESS_FAILED);
        return super.abortPreProcess();
    }

    @Override
    public int compareTo(final NativeJob o) {
        return 0;
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final JobDetail jobDetail = context.getJobDetail();
        final String jobGroup = jobDetail.getGroup();
        final JobDataMap dataMap = jobDetail.getJobDataMap();
        int exitCode = 1;

        // For system jobs (cron jobs), jobType is derived from jobName
        jobType = context.getJobDetail().getName();
        // scriptDebug = kJobScheduler.getScriptDebugFlag(jobType);
        final Date nextFireTime = jobGroup.equals(KJobScheduler.SYSTEM_JOB_GROUP)
                ? context.getTrigger().getNextFireTime()
                : new Date(Long.parseLong(dataMap.getString(PARAM_NEXT_FIRE_TIME)));
        // CRON jobs can neither be postponed nor dropped
        exitCode = runNativeCommand(dataMap);
        context.setResult(new Integer(exitCode));
        // Save nextFireTime (used on Tomcat restart)
        kJobScheduler.storeNextFireTime(jobType, nextFireTime);

    }

    protected void logNoDataError(String message) {
        // It's likely that the job failed even before outputting #ERROR
        // In such cases log the last line
        jobFailed(JobStatistics.ERROR_INCOMPLETE_FILE);
        message = "Job Execution failed: " + (message == null ? "Incomplete output file" : message);
        logError(message);
        // sendAlertMail(message, jobType, tempDir.getName());
    }

    // Run directly once; do not change next fire time
    // For invocation by Admin only
    // Returns exit code
    @Override
    public int runCronJob(final JobDataMap dataMap) {
        // scriptDebug = kJobScheduler.getScriptDebugFlag(jobType);
        // CRON jobs can neither be postponed nor dropped
        // TODO: Check if timeout of 20 mins is sufficient
        return runNativeCommand(dataMap);
    }

    @Override
    public void setJobType(final String jobType) {

    }
}
