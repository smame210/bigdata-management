package org.bigdata.scheduler.core;

import lombok.extern.slf4j.Slf4j;
import org.bigdata.scheduler.bean.SchedulerCreateParam;
import org.bigdata.scheduler.enums.ScheduledType;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CronScheduler extends AbstractSchedulerProvider {

    public CronScheduler(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public ScheduledType getScheduleType() {
        return ScheduledType.CRON;
    }

    @Override
    public void createScheduler(SchedulerCreateParam params) {
        if (params.getCron() == null) {
            throw new IllegalArgumentException("cron express is null!");
        }
        JobBuilder jobBuilder = JobBuilder.newJob(params.getJobClass())
                .withIdentity(params.getName(), DEFAULT_GROUP);
        if (params.getData() != null && !params.getData().isEmpty()) {
            jobBuilder.usingJobData(new JobDataMap(params.getData()));
        }

        JobDetail jobDetail = jobBuilder.build();
        //创建触发器，指定任务执行时间
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(params.getName(), DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(params.getCron()));
        if (params.getStartTime() != null) {
            triggerBuilder.startAt(params.getStartTime());
        }
        if (params.getEndTime() != null) {
            triggerBuilder.endAt(params.getEndTime());
        }
        CronTrigger trigger = triggerBuilder.build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("cron schedule error!", e);
        }
    }

    @Override
    public void rescheduleJob(SchedulerCreateParam params) {
        TriggerKey triggerKey = TriggerKey.triggerKey(params.getName(), DEFAULT_GROUP);
        try {
            // 获取旧的触发器
            CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            TriggerBuilder<CronTrigger> triggerBuilder = oldTrigger.getTriggerBuilder()
                    .withIdentity(params.getName(), DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(params.getCron()));
            if (params.getStartTime() != null) {
                triggerBuilder.startAt(params.getStartTime());
            }
            if (params.getEndTime() != null) {
                triggerBuilder.endAt(params.getEndTime());
            }
            Trigger newTrigger = triggerBuilder.build();
            scheduler.rescheduleJob(triggerKey, newTrigger);
        } catch (SchedulerException e) {
            log.error("cron rescheduler error!", e);
        }
    }
}
