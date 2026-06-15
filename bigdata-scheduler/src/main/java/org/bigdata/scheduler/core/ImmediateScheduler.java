package org.bigdata.scheduler.core;

import lombok.extern.slf4j.Slf4j;
import org.bigdata.scheduler.bean.SchedulerCreateParam;
import org.bigdata.scheduler.enums.ScheduledType;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImmediateScheduler extends AbstractSchedulerProvider {

    public ImmediateScheduler(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public ScheduledType getScheduleType() {
        return ScheduledType.IMMEDIATE;
    }

    @Override
    public void createScheduler(SchedulerCreateParam params) {
        JobBuilder jobBuilder = JobBuilder.newJob(params.getJobClass())
                .withIdentity(params.getName(), DEFAULT_GROUP);
        if (params.getData() != null && !params.getData().isEmpty()) {
            jobBuilder.usingJobData(new JobDataMap(params.getData()));
        }

        JobDetail jobDetail = jobBuilder.build();

        TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(params.getName(), DEFAULT_GROUP)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withRepeatCount(0));

        if (params.getStartTime() != null) {
            triggerBuilder.startAt(params.getStartTime());
        } else {
            triggerBuilder.startNow();
        }

        SimpleTrigger trigger = triggerBuilder.build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("immediate schedule error!", e);
            throw new RuntimeException("Failed to schedule immediate/retry job: " + params.getName(), e);
        }
    }

    @Override
    public void rescheduleJob(SchedulerCreateParam params) {
        // do nothing
    }
}
