package org.bigdata.scheduler.core;

import lombok.extern.slf4j.Slf4j;
import org.bigdata.scheduler.bean.SchedulerCreateParam;
import org.bigdata.scheduler.enums.ScheduledType;
import org.quartz.*;

@Slf4j
public abstract class AbstractSchedulerProvider {
    protected static final String DEFAULT_GROUP = "default_group";

    protected final Scheduler scheduler;

    public AbstractSchedulerProvider(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public abstract ScheduledType getScheduleType();

    public abstract void createScheduler(SchedulerCreateParam params);

    public abstract void rescheduleJob(SchedulerCreateParam params);

    public boolean deleteScheduler(String name) {
        boolean success = false;
        try {
            // 暂停触发器
            scheduler.pauseTrigger(new TriggerKey(name, DEFAULT_GROUP));
            // 移除触发器中的任务
            scheduler.unscheduleJob(new TriggerKey(name, DEFAULT_GROUP));
            // 删除任务
            scheduler.deleteJob(new JobKey(name, DEFAULT_GROUP));
            success = true;
        } catch (SchedulerException e) {
            log.error("job delete error!", e);
        }
        return success;
    }
}
