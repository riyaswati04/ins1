package com.ia.scheduler;

import static com.ia.common.Handlers.logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import com.ia.util.DateUtil;

public class IAJobStates {
    /**
     * A bean to capture all parameters of a Quartz Job in order to view it through Admin or log.
     */
    class IAJobState {
        static final String NA = "~";

        int jobExecutionId; // valid (non-zero) only if job is currently running

        int jobUID;

        String jobName;

        String jobType;

        String jobState;

        String runningTime; // in mm:ss format (valid only if job is currently running)

        int userId;

        String updateSource;

        String jobDesc;

        String jobHandler;

        String jobGroup;

        String triggerType; // Simple or Cron

        String triggerGroup;

        String triggerDetails;

        @Override
        public String toString() {
            final String str = "jobUID=" + jobUID + ", jobName=" + jobName + ", jobType=" + jobType
                    + ", jobState=" + jobState + ", userId=" + userId + ", updateSource="
                    + updateSource + ", jobDesc=" + jobDesc + ", jobHandler=" + jobHandler
                    + ", jobGroup=" + jobGroup + ", triggerType=" + triggerType + ", triggerGroup="
                    + triggerGroup + ", triggerDetails=" + triggerDetails;
            return jobExecutionId == 0 ? str
                    : str + ", jobExecutionId=" + jobExecutionId + ", runningTime=" + runningTime;
        }

        public String[] toStringArrayDetails() {
            return new String[] {jobUID != 0 ? String.valueOf(jobUID) : NA,
                    jobDesc != null ? jobDesc : NA, jobHandler != null ? jobHandler : NA,
                    jobGroup != null ? jobGroup : NA, triggerType != null ? triggerType : NA,
                    triggerGroup != null ? triggerGroup : NA,
                    triggerDetails != null ? triggerDetails : NA};
        }

        public String[] toStringArraySummary() {
            return new String[] {jobExecutionId != 0 ? String.valueOf(jobExecutionId) : NA,

                    jobUID != 0 ? String.valueOf(jobUID) : NA,

                    jobName != null ? jobName : NA,

                    jobType != null ? jobType : NA,

                    jobState != null ? "RUNNING".equals(jobState) && runningTime == null
                            ? jobState + " (REMOTE)" : jobState : NA,

                    runningTime != null ? runningTime : NA,

                    userId != 0 ? String.valueOf(userId) : NA,

                    updateSource != null ? updateSource : NA};

        }
    }

    private String jobCounts;

    private final int tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();

    private final HashMap<Integer, IAJobState> jobUIDtoStateMap =
            new HashMap<Integer, IAJobState>();

    private final HashMap<Integer, IAJobState> jobExecutionIdtoStateMap =
            new HashMap<Integer, IAJobState>();

    private final HashMap<String, IAJobState> cronJobTypetoStateMap =
            new HashMap<String, IAJobState>();

    private final ArrayList<IAJobState> states = new ArrayList<IAJobState>();

    public IAJobStates() {}

    // Get counts, mark remote jobs, etc.
    public void collectStats() {
        final HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for (final IAJobState state : states) {
            String jobState = state.jobState;
            if ("RUNNING".equals(jobState) && state.runningTime == null) {
                state.jobState = jobState = "RUNNING (REMOTE)";
            }
            else if ("QUEUED".equals(jobState)) {
                state.jobState = jobState = "LINK-QUEUE";
            }
            if (jobState != null) {
                final Integer count = counts.get(jobState);
                counts.put(jobState, count == null ? 1 : count.intValue() + 1);
            }
        }
        final StringBuilder sb = new StringBuilder();
        for (final String jobState : new TreeSet<String>(counts.keySet())) {
            sb.append(' ').append(jobState).append("=").append(counts.get(jobState));
        }
        jobCounts = sb.length() == 0 ? " None" : sb.toString();
    }

    public void dump() {
        for (final IAJobState state : states) {
            logger.info("States:" + state);
        }
        logger.info("Active Jobs:" + jobCounts);
    }

    // Called for all jobs in about-to-run queue
    public void jobIsInAboutToRunQueue(final int jobExecutionId) {
        final IAJobState state = jobExecutionIdtoStateMap.get(jobExecutionId);
        if (state != null) {
            state.jobState = "RUN-QUEUE";
        }
    }

    // Called for all jobs in pre-scheduled queue
    public void jobIsInPreScheduledQueue(final int jobUID) {
        final IAJobState state = jobUIDtoStateMap.get(jobUID);
        if (state != null) {
            state.jobState = "PRE-QUEUE";
        }
    }

    public void populateAutoFetchJobState(final int jobExecutionId, final int jobUID,
            final String jobType, final int userId, final String updateSource, final String jobDesc,
            final String jobState) {
        IAJobState state = jobUIDtoStateMap.get(jobUID);
        if (state != null) {
            return; // Already added
        }
        state = new IAJobState();
        state.jobExecutionId = jobExecutionId;
        state.jobUID = jobUID;
        state.jobType = jobType;
        state.userId = userId;
        state.updateSource = updateSource;
        state.jobDesc = jobDesc;
        state.jobState = jobState;
        states.add(state);
        jobUIDtoStateMap.put(jobUID, state);
        if (jobExecutionId != 0) {
            jobExecutionIdtoStateMap.put(jobExecutionId, state);
        }
    }

    // Examples:
    // populateJobDetail("job#98299:6", "HDFCCombo", "com.ia.scheduler.jobs.CommonItypeUpdater")
    // populateJobDetail("UnsubscribeJob", "ia",
    // "com.ia.scheduler.jobs.UnsubscriptionUpdater")
    public void populateJobDetail(final String jobName, final String jobGroup, String jobHandler) {
        final int colonIndex = jobName.indexOf(':');
        final int dotIndex = jobHandler.lastIndexOf('.');
        if (dotIndex != -1) {
            jobHandler = jobHandler.substring(dotIndex + 1);
        }
        final String prefix1 = "job#";
        final int idIndex = jobName.startsWith(prefix1) ? prefix1.length() : 0;
        if (colonIndex != -1 && idIndex > 0) {
            try {
                final int jobUID = Integer.parseInt(jobName.substring(idIndex, colonIndex));
                IAJobState state = jobUIDtoStateMap.get(jobUID);
                if (state == null) {
                    // Should not happen
                    state = new IAJobState();
                    states.add(state);
                    jobUIDtoStateMap.put(jobUID, state);
                }
                state.jobName = jobName;
                state.jobGroup = jobGroup;
                state.jobHandler = jobHandler;
                return;
            }
            catch (final Exception e) {
                logger.error("Exception in parsing jobUID from jobName " + jobName + ", error="
                        + e.toString());
            }
        }
        // Must be a Quartz Cron job
        final IAJobState state = new IAJobState();
        states.add(state);
        state.jobName = jobName;
        state.jobType = jobName;
        state.jobGroup = jobGroup;
        state.jobHandler = jobHandler;
        cronJobTypetoStateMap.put(state.jobType, state);
    }

    // Examples:
    // populateJobTrigger("Simple", "job#98299:6", "HDFCCombo",
    // "startTime=2013-01-02
    // 13:18:09,endTime=null,repeatCount=0,repeatInterval=0,nextFireTime=2013-01-02 13:18:09");
    // populateJobTrigger("Cron", "UnsubscribeJob", "ia",
    // "startTime=2012-12-14 18:58:07,endTime=null,cronExpression=0 00 23 ? *
    // *,nextFireTime=2013-01-02 23:00:00");
    // triggerName should be same as jobName
    public void populateJobTrigger(final String triggerType, final String triggerName,
            final String triggerGroup, final String triggerDetails) {
        final int colonIndex = triggerName.indexOf(':');
        final String prefix1 = "job#";
        final int idIndex = triggerName.startsWith(prefix1) ? prefix1.length() : 0;
        if (colonIndex != -1 && idIndex > 0) {
            try {
                final int jobUID = Integer.parseInt(triggerName.substring(idIndex, colonIndex));
                IAJobState state = jobUIDtoStateMap.get(jobUID);
                if (state == null) {
                    // Should not happen
                    state = new IAJobState();
                    state.jobName = triggerName;
                    state.jobType = triggerName;
                    states.add(state);
                    jobUIDtoStateMap.put(jobUID, state);
                }
                state.triggerType = triggerType;
                state.triggerGroup = triggerGroup;
                state.triggerDetails = triggerDetails;
                return;
            }
            catch (final Exception e) {
                logger.error("Exception in parsing jobUID from triggerName " + triggerName
                        + ", error=" + e.toString());
            }
        }
        // Must be a Quartz Cron job
        IAJobState state = cronJobTypetoStateMap.get(triggerName);
        if (state == null) {
            // Should not happen
            state = new IAJobState();
            state.jobName = triggerName;
            state.jobType = triggerName;
            states.add(state);
            if ("Cron".equals(triggerType)) {
                cronJobTypetoStateMap.put(state.jobType, state);
            }
        }
        state.triggerType = triggerType;
        state.triggerGroup = triggerGroup;
        state.triggerDetails = triggerDetails;
    }

    // Called for all currently running local Perl jobs
    public void populatePerlJobRunningTime(final int jobExecutionId, final String jobType,
            final long runningTime) {
        IAJobState state = jobExecutionIdtoStateMap.get(jobExecutionId);
        if (state == null) {
            // Not an Auto-update job - could be a Cron job or a non-Quartz job
            state = cronJobTypetoStateMap.get(jobType);
            if (state == null) {
                // Quartz has no clue about the job
                state = new IAJobState();
                state.jobExecutionId = jobExecutionId;
                state.jobType = jobType;
                states.add(state);
            }
            state.jobExecutionId = jobExecutionId;
        }
        // There are some chances of more than one instances of a Cron job running - can be ignored
        // for
        // now
        if (state.jobState == null) {
            state.jobState = "RUNNING"; // non-auto-update jobs
        }
        if ("RUNNING".equals(state.jobState)) {
            state.runningTime = DateUtil.TIME_FORMAT.format(new Date(runningTime - tzOffset));
        }
    }

}
