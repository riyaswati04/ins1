package com.ia.scheduler;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;

public final class GuiceAwareJobFactory implements JobFactory {

    private final Injector injector;

    @Inject
    public GuiceAwareJobFactory(final Injector injector) {
        super();
        this.injector = injector;
    }

    @Override
    public Job newJob(final TriggerFiredBundle bundle) throws SchedulerException {

        final JobDetail jobDetail = bundle.getJobDetail();

        final Class<?> jobClass = jobDetail.getJobClass();

        try {
            return (Job) injector.getInstance(jobClass);

        }
        catch (final Exception e) {
            throw new SchedulerException(e);

        }
    }

}
