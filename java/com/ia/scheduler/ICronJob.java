package com.ia.scheduler;

import org.quartz.JobDataMap;

/**
 * Common methods of all IA Cron Jobs
 */
public interface ICronJob extends IKJob {

    public static final String PARAM_NEXT_FIRE_TIME = "NEXT_FIRE_TIME";

    public int runCronJob(JobDataMap dataMap);
}
