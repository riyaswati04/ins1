package com.ia.scheduler;

import org.quartz.Job;

/**
 * Common methods of all IA Jobs
 */
public interface IKJob extends Job {
    public void setJobType(String jobType);
}
