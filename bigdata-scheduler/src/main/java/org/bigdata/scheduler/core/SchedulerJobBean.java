package org.bigdata.scheduler.core;

import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class SchedulerJobBean extends QuartzJobBean implements ScheduleJob {
}
