package com.ia.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import com.ia.util.DateUtil;

public class JobStatistics {

    class JobStat {
        String jobType;

        int attempts; // since midnight

        int failures; // since midnight

        int errorCount[] = new int[NUM_ERROR_TYPES]; // since midnight

        int totalAttempts; // since tomcat start

        int totalFailures; // since tomcat start

        Date lastPageError; // last page error time or null

        int minExecTime; // in seconds (for all runs since tomcat start)

        int maxExecTime; // in seconds (for all runs since tomcat start)

        int avgExecTime; // in seconds (for all runs since tomcat start)

        // Map of job execution id to error type, for failed jobs. Purged at midnight.
        HashMap<Integer, Integer> jobErrors = new HashMap<Integer, Integer>();

        JobStat(final String jobType) {
            this.jobType = jobType;
        }

        void clearPrevDayStats() {
            for (int i = 0; i < NUM_ERROR_TYPES; ++i) {
                errorCount[i] = 0;
            }
            attempts = 0;
            failures = 0;
            jobErrors.clear();
        }

        void jobExecuted(final int jobExecutionId, final int timeInSecs) {
            final Integer val = jobErrors.get(jobExecutionId);
            if (val != null) {
                // Either jobFailed() or the same jobExecuted() was called before
                if (val.intValue() == JOB_SUCCESS) {
                    return; // jobExecuted() was called
                }
                failures++;
                totalFailures++;
            }
            else {
                jobErrors.put(jobExecutionId, JOB_SUCCESS);
            }
            attempts++;
            if (timeInSecs > 0) {
                if (timeInSecs < minExecTime || minExecTime == 0) {
                    minExecTime = timeInSecs;
                }
                if (timeInSecs > maxExecTime || maxExecTime == 0) {
                    maxExecTime = timeInSecs;
                }
                avgExecTime = (totalAttempts * avgExecTime + timeInSecs) / (totalAttempts + 1);
            }
            totalAttempts++;
        }

        void jobFailed(final int jobExecutionId, final int errorType) {
            final Integer val = jobErrors.get(jobExecutionId);
            if (val == null) {
                // jobExecuted() was not called before (Ex. preprocess failure)
                jobErrors.put(jobExecutionId, errorType);
                errorCount[errorType]++;
                if (errorType == ERROR_PAGE) {
                    lastPageError = new Date();
                }
                // Do not change attempt or failure counts
                return;
            }
            if (val.intValue() != JOB_SUCCESS) {
                return; // jobFailed() was already called
            }
            jobErrors.put(jobExecutionId, errorType);
            errorCount[errorType]++;
            if (errorType == ERROR_PAGE) {
                lastPageError = new Date();
            }
            failures++;
            totalFailures++;
        }

        @Override
        public String toString() {
            final String[] items = toStringArray(false);
            final StringBuilder sb = new StringBuilder("[JobStat] ");
            sb.append(items[0]).append(" attempts=").append(items[1]).append(" failures=")
                    .append(items[2]).append(" (").append(items[3]).append("%)")
                    .append(" importance=").append(items[4]);
            if (items[5].length() > 0) {
                sb.append(' ').append(items[5]);
            }
            sb.append(" totalAttempts=").append(items[6]).append(" totalFailures=").append(items[7])
                    .append(" (").append(items[8]).append("%)");
            if (items[9].length() > 0) {
                sb.append(" lastRefreshed=").append(items[9]);
            }
            if (items[10].length() > 0) {
                sb.append(" lastPageError=").append(items[10]);
            }
            if (items[11].length() > 0) {
                sb.append(" lastBlocked=").append(items[11]);
            }
            sb.append(" blockStatus=").append(items[12]).append(" minExecTime=").append(items[13])
                    .append(" maxExecTime=").append(items[14]).append(" avgExecTime=")
                    .append(items[15]);
            return sb.toString();
        }

        String[] toStringArray(final boolean forHTML) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < NUM_ERROR_TYPES; ++i) {
                final int count = errorCount[i];
                if (count == 0) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(labels[i]).append('=').append(count);
            }
            String failureTypes = sb.toString();
            final IAJobInfo jobInfo = KJobScheduler.getJobInfo(jobType);
            final int blockStatus = jobInfo != null ? jobInfo.getBlockStatus() : 0;
            final String blockStatusMessage =
                    jobInfo != null ? jobInfo.getBlockStatusMessage() : "";
            final boolean jobBlocked = blockStatus > IAJobInfo.JOB_NOT_BLOCKED ? true : false;
            final int percentFailure = attempts == 0 ? 0 : (int) (100 * failures / attempts);
            final int totalPercentFailure =
                    totalAttempts == 0 ? 0 : (int) (100 * totalFailures / totalAttempts);
            final boolean unfixedPageError = lastPageError != null && jobInfo != null
                    && lastPageError.after(jobInfo.getLastRefreshed());
            final String importance =
                    jobBlocked || unfixedPageError || percentFailure >= 80 && attempts >= 5
                            || totalPercentFailure >= 80 && totalAttempts >= 5 ? IMPORTANCE_HIGH
                                    : percentFailure >= 40 ? IMPORTANCE_MEDIUM
                                            : percentFailure > 0 ? IMPORTANCE_LOW : IMPORTANCE_NONE;
            final Date lastBlocked = jobInfo == null ? null : jobInfo.getLastBlocked();
            String jobName = jobType;
            if (forHTML) {
                if (jobName.length() > 25) {
                    jobName = jobName.substring(0, 25) + "..";
                }
                if (jobBlocked) {
                    jobName =
                            "<span style=\"text-decoration: line-through;\">" + jobName + "</span>";
                }
                if (unfixedPageError) {
                    failureTypes += "<br/><b>PAGE Error NOT FIXED</b>";
                }
            }
            return new String[] {jobName, String.valueOf(attempts), String.valueOf(failures),
                    String.valueOf(percentFailure), importance, failureTypes,
                    String.valueOf(totalAttempts), String.valueOf(totalFailures),
                    String.valueOf(totalPercentFailure),
                    jobInfo == null ? ""
                            : DateUtil.TIMESTAMP_FORMAT.format(jobInfo.getLastRefreshed()),
                    lastPageError == null ? "" : DateUtil.TIMESTAMP_FORMAT.format(lastPageError),
                    lastBlocked == null ? "" : DateUtil.TIMESTAMP_FORMAT.format(lastBlocked),
                    blockStatusMessage, String.valueOf(minExecTime), String.valueOf(maxExecTime),
                    String.valueOf(avgExecTime)};
        }
    }

    private static final int JOB_SUCCESS = -1;

    // Error types
    public static final int NATIVE_JOB_EXCEPTION = 0;

    public static final int PREPROCESS_FAILED = 1;

    public static final int ERROR_HTTP = 2;

    public static final int ERROR_IO = 3;

    public static final int ERROR_PAGE = 4;

    public static final int ERROR_SITE = 5;

    public static final int ERROR_NOACC = 6;

    public static final int ERROR_PASSWORD = 7;

    public static final int ERROR_CHPASSWORD = 8;

    public static final int ERROR_USERACT = 9;

    public static final int ERROR_INCOMPLETE_FILE = 10;

    public static final int ERROR_LOAD_FAILED = 11;

    public static final int ERROR_USERACT_2FA = 12;

    private static final int NUM_ERROR_TYPES = 13;

    private static final String labels[] = new String[] {"NativeJobException", "PreprocessFailed",
            "HTTP", "IO", "Page", "Site/Timeout", "NoAcc", "Password", "ChPassword", "UserAct",
            "IncompleteFile", "LoadFailure", "UserAct2FA"};

    private static final String IMPORTANCE_HIGH = "HIGH";

    private static final String IMPORTANCE_MEDIUM = "MEDIUM";

    private static final String IMPORTANCE_LOW = "LOW";

    private static final String IMPORTANCE_NONE = "NONE";

    private static final HashMap<String, Integer> IMPORTANCE_ORDER = new HashMap<String, Integer>();

    static {
        IMPORTANCE_ORDER.put(IMPORTANCE_HIGH, 1);
        IMPORTANCE_ORDER.put(IMPORTANCE_MEDIUM, 2);
        IMPORTANCE_ORDER.put(IMPORTANCE_LOW, 3);
        IMPORTANCE_ORDER.put(IMPORTANCE_NONE, 4);
    }

    private static JobStatistics instance;

    public static JobStatistics getJobStatistics() {
        if (instance == null) {
            instance = new JobStatistics();
        }
        return instance;
    }

    // Map of JobType to JobStat
    private final HashMap<String, JobStat> jobStatMap = new HashMap<String, JobStat>();

    private JobStatistics() {}

    // Called at Midnight
    // Clear daily stats
    public void clearStats() {
        synchronized (jobStatMap) {
            for (final JobStat s : jobStatMap.values()) {
                s.clearPrevDayStats();
            }
        }
    }

    public String getStat(final String jobType) {
        if (jobType == null) {
            return "";
        }
        final JobStat s = jobStatMap.get(jobType);
        if (s == null) {
            return "";
        }
        return s.toString();
    }

    public ArrayList<String> getStats() {
        final ArrayList<String> stats = new ArrayList<String>();
        synchronized (jobStatMap) {
            for (final String jobType : new TreeSet<String>(jobStatMap.keySet())) {
                stats.add(jobStatMap.get(jobType).toString());
            }
        }
        return stats;
    }

    // Called from NativeJob, on completion of a job.
    // Guaranteed to be called only once per job instance, as soon as job completes.
    // Not called if job is not executed (for example - preprocess failure, dropping)
    public void jobExecuted(final String jobType, final int jobExecutionId, final int timeInSecs) {
        if (jobType == null) {
            return;
        }
        synchronized (jobStatMap) {
            JobStat s = jobStatMap.get(jobType);
            if (s == null) {
                s = new JobStat(jobType);
                jobStatMap.put(jobType, s);
            }
            s.jobExecuted(jobExecutionId, timeInSecs);
        }
    }

    // Usually called during postprocessing.
    // May be called during preprocessing, or several times for the same jobExecutionId.
    void jobFailed(final String jobType, final int jobExecutionId, final int errorType) {
        if (jobType == null) {
            return;
        }
        synchronized (jobStatMap) {
            JobStat s = jobStatMap.get(jobType);
            if (s == null) {
                s = new JobStat(jobType);
                jobStatMap.put(jobType, s);
            }
            s.jobFailed(jobExecutionId, errorType);
        }
    }

    // Called at tomcat start
    void registerJob(final String jobType) {
        if (jobType == null) {
            return;
        }
        synchronized (jobStatMap) {
            if (!jobStatMap.containsKey(jobType)) {
                jobStatMap.put(jobType, new JobStat(jobType));
            }
        }
    }
}
