package com.ia.scheduler;

import java.util.Date;
import java.util.HashSet;

import com.ia.scheduler.enums.JOB_EXECUTOR;
import com.ia.util.DateUtil;

public class IAJobInfo {

    public static final int JOB_TEMPORARILY_BLOCKED = 1;

    public static final int JOB_PERMANENTLY_BLOCKED = 2;

    public static final int JOB_NOT_BLOCKED = 0;

    public static final String JOB_TEMPORARILY_BLOCKED_MESSAGE = "Temporarily Blocked";

    public static final String JOB_PERMANENTLY_BLOCKED_MESSAGE = "Permanently Blocked";

    public static final String JOB_NOT_BLOCKED_MESSAGE = "Not Blocked";

    public static int getBlockStatusFromBlockStatusMessage(final String message) {
        if (JOB_TEMPORARILY_BLOCKED_MESSAGE.equals(message)) {
            return JOB_TEMPORARILY_BLOCKED;
        }
        else if (JOB_PERMANENTLY_BLOCKED_MESSAGE.equals(message)) {
            return JOB_PERMANENTLY_BLOCKED;
        }

        return JOB_NOT_BLOCKED;
    }

    private int blocked;

    private final HashSet<String> blockedItypes; // Set of itypes

    private String cronExp;

    private final String desc;

    private long downtimeStart, downtimeEnd; // Job downtime

    private Class<?> handler;

    private String handlerName;

    private JOB_EXECUTOR jobExecutor;

    private final String jobType;

    private Date lastBlocked;

    private Date lastRefreshed;

    private long nextFireTime;

    private String scheduleType = "onrequest";

    private String script;

    public IAJobInfo(final String jobType, final String desc, final JOB_EXECUTOR jobExecutor) {
        this.jobType = jobType;
        this.desc = desc;
        blocked = JOB_NOT_BLOCKED;
        blockedItypes = new HashSet<String>();
        this.jobExecutor = jobExecutor;
        lastRefreshed = new Date();
    }

    public HashSet<String> getBlockedItypes() {
        return blockedItypes;
    }

    public int getBlockStatus() {
        return blocked;
    }

    public String getBlockStatusMessage() {
        return blocked > JOB_NOT_BLOCKED
                ? blocked == JOB_TEMPORARILY_BLOCKED ? JOB_TEMPORARILY_BLOCKED_MESSAGE
                        : JOB_PERMANENTLY_BLOCKED_MESSAGE
                : blockedItypes.size() > 0 ? blockedItypes.toString() : JOB_NOT_BLOCKED_MESSAGE;
    }

    public String getCronExp() {
        return cronExp;
    }

    public String getDesc() {
        return desc;
    }

    public Class<?> getHandler() {
        return handler;
    }

    public JOB_EXECUTOR getJobExecutor() {
        return jobExecutor;
    }

    public String getJobType() {
        return jobType;
    }

    public Date getLastBlocked() {
        return lastBlocked;
    }

    public Date getLastRefreshed() {
        return lastRefreshed;
    }

    public String getName() {
        return handlerName;
    }

    public long getNextFireTime() {
        return nextFireTime;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public String getScript() {
        return script;
    }

    public boolean isBlocked() {
        return blocked > JOB_NOT_BLOCKED;
    }

    public boolean isBlocked(final String itype) {
        if (blocked > JOB_NOT_BLOCKED) {
            return true;
        }
        return itype == null ? blocked > JOB_NOT_BLOCKED : blockedItypes.contains(itype);
    }

    public boolean isBlockedPermanently() {
        return blocked == JOB_PERMANENTLY_BLOCKED;
    }

    public boolean isBlockedTemporarily() {
        return blocked == JOB_TEMPORARILY_BLOCKED;
    }

    public boolean isDownAt(final long aTime) {
        return downtimeStart > 0 && downtimeEnd > 0 && downtimeStart <= aTime
                && downtimeEnd >= aTime;
    }

    public boolean isOnRequestJob() {
        return "onrequest".equals(scheduleType);
    }

    public void setBlocked(final int jobBlockStatus) {
        blocked = jobBlockStatus;
        lastBlocked = new Date();
    }

    public void setBlocked(final String itype, final int jobBlockStatus) {
        if (itype == null) {
            setBlocked(jobBlockStatus);
        }
        else {
            blockedItypes.add(itype);
        }
        lastBlocked = new Date();
    }

    public void setCronExp(final String cronExp) {
        this.cronExp = cronExp;
    }

    public void setHandler(final Class<?> handler) {
        this.handler = handler;
    }

    public void setJobDowntime(final long startTimeMillis, final long endTimeMillis) {
        downtimeStart = startTimeMillis;
        downtimeEnd = endTimeMillis;
    }

    public void setJobExecutor(final JOB_EXECUTOR jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    public void setName(final String name) {
        handlerName = name;
    }

    public void setNextFireTime(final long nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public void setReleased() {
        blocked = JOB_NOT_BLOCKED;
        blockedItypes.clear();
        lastRefreshed = new Date();
    }

    public void setReleased(final String itype) {
        if (itype != null) {
            blockedItypes.remove(itype);
        }
        else {
            blocked = JOB_NOT_BLOCKED;
            blockedItypes.clear();
        }
        lastRefreshed = new Date();
    }

    public void setScheduleType(final String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public void setScript(final String script) {
        this.script = script;
    }

    public String[] toStringArray() {
        return new String[] {jobType, getBlockStatusMessage(),
                nextFireTime == 0 ? "NA" : DateUtil.TIMESTAMP_FORMAT.format(new Date(nextFireTime)),
                null, desc, script, handlerName, scheduleType, cronExp};
    }
}
