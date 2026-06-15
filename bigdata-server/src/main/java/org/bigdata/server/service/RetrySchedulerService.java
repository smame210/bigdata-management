package org.bigdata.server.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.scheduler.bean.SchedulerCreateParam;
import org.bigdata.scheduler.core.AbstractSchedulerProvider;
import org.bigdata.scheduler.core.SchedulerProviderRegistry;
import org.bigdata.scheduler.enums.ScheduledType;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.bean.entity.TaskSchedule;
import org.bigdata.server.job.RetryExecuteJob;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.mapper.TaskScheduleMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 任务失败重试编排服务。
 * 由 TaskStatusSyncJob 在检测到 FAILED 实例时调用，负责判断重试条件并创建 Quartz 延迟重试任务。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetrySchedulerService {

    private final TaskScheduleMapper taskScheduleMapper;

    private final TaskInstanceMapper taskInstanceMapper;

    private final SchedulerProviderRegistry schedulerProviderRegistry;

    /**
     * 当任务实例失败时，判断是否需要重试，如需则创建 Quartz 延迟重试任务。
     *
     * @param instance 已更新为 FAILED 状态的任务实例
     */
    public void scheduleRetryIfNeeded(TaskInstance instance) {
        // 1. 只有调度触发的任务才重试
        if (instance.getScheduleId() == null) {
            return;
        }

        // 2. 只对 FAILED 重试
        if (!Objects.equals(JobStatusEnum.FAILED.getCode(), instance.getTaskStatus())) {
            return;
        }

        // 3. 查调度配置
        TaskSchedule schedule = taskScheduleMapper.selectById(instance.getScheduleId());
        if (schedule == null || schedule.getMaxRetryTimes() == null || schedule.getMaxRetryTimes() <= 0) {
            return;
        }

        // 4. 是否已超重试上限
        int currentRetryCount = instance.getRetryCount() != null ? instance.getRetryCount() : 0;
        if (currentRetryCount >= schedule.getMaxRetryTimes()) {
            log.info("Task instance {} has reached max retry times ({})", instance.getId(), schedule.getMaxRetryTimes());
            return;
        }

        // 5. 原子锁定：仅在未安排过重试时设置 retry_scheduled
        int locked = taskInstanceMapper.update(null,
                Wrappers.<TaskInstance>lambdaUpdate()
                        .set(TaskInstance::getRetryScheduled, true)
                        .eq(TaskInstance::getId, instance.getId())
                        .and(w -> w.isNull(TaskInstance::getRetryScheduled)
                                .or().eq(TaskInstance::getRetryScheduled, false)));
        if (locked == 0) {
            log.info("Task instance {} retry already scheduled by another process", instance.getId());
            return;
        }

        // 6. 计算重试时间
        int retryInterval = schedule.getRetryInterval() != null ? schedule.getRetryInterval() : 0;
        Date retryTime = DateUtil.offsetSecond(new Date(), retryInterval);

        // 7. 构建重试链路信息
        Integer rootInstanceId = instance.getRootInstanceId() != null ? instance.getRootInstanceId() : instance.getId();

        // 8. 创建 Quartz 一次性延迟任务
        String jobName = "retry-" + instance.getId() + "-" + (currentRetryCount + 1);
        try {
            AbstractSchedulerProvider provider = schedulerProviderRegistry.getProvider(ScheduledType.IMMEDIATE);
            provider.createScheduler(SchedulerCreateParam.builder()
                    .name(jobName)
                    .startTime(retryTime)
                    .jobClass(RetryExecuteJob.class)
                    .data(Map.of(
                            "taskId", instance.getTaskId(),
                            "scheduleId", instance.getScheduleId(),
                            "parentInstanceId", instance.getId(),
                            "rootInstanceId", rootInstanceId,
                            "retryCount", currentRetryCount
                    ))
                    .build());
        } catch (Exception e) {
            // Quartz 创建失败 → 回滚 retry_scheduled，下次扫描可重试
            taskInstanceMapper.update(null,
                    Wrappers.<TaskInstance>lambdaUpdate()
                            .set(TaskInstance::getRetryScheduled, false)
                            .eq(TaskInstance::getId, instance.getId()));
            log.error("Failed to create retry Quartz job for instance {}, rolling back retry_scheduled", instance.getId(), e);
            return;
        }

        log.info("Retry scheduled for task instance {}: attempt {}, at {}",
                instance.getId(), currentRetryCount + 1, retryTime);
    }
}
