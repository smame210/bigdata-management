package org.bigdata.server.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.api.job.JobHandlerIdentifier;
import org.bigdata.api.job.JobHandlerInvoker;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.convert.JobParamConverter;
import org.bigdata.server.convert.JobParamConverterInvoker;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.service.RetrySchedulerService;
import org.bigdata.server.publish.TaskEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusSyncJob {

    private final TaskInstanceMapper taskInstanceMapper;

    private final TaskInfoMapper taskInfoMapper;

    private final TaskEventPublisher taskEventPublisher;

    private final RetrySchedulerService retrySchedulerService;

    private final TransactionTemplate transactionTemplate;

    /**
     * 每30秒同步一次任务状态
     */
    @Scheduled(fixedDelay = 30000)
    public void syncTaskStatus() {
        log.debug("TaskStatusSyncJob tick");
        List<TaskInstance> runningInstances = taskInstanceMapper.selectList(
                Wrappers.<TaskInstance>lambdaQuery()
                        .eq(TaskInstance::getTaskStatus, JobStatusEnum.RUNNING.getCode())
        );

        if (runningInstances.isEmpty()) {
            return;
        }

        log.info("Syncing status for {} running task instances", runningInstances.size());

        for (TaskInstance instance : runningInstances) {
            try {
                syncSingleInstance(instance);
            } catch (Exception e) {
                log.error("Failed to sync status for instance id: {}", instance.getId(), e);
            }
        }
    }

    private void syncSingleInstance(TaskInstance instance) {
        TaskInfo taskInfo = taskInfoMapper.selectById(instance.getTaskId());
        if (taskInfo == null) {
            log.warn("TaskInfo not found for instance: {}", instance.getId());
            return;
        }

        JobHandlerIdentifier identifier = new JobHandlerIdentifier(
                instance.getClusterType(),
                taskInfo.getEngineType(),
                instance.getTaskType()
        );

        JobParamConverter converter = JobParamConverterInvoker.convert(identifier);
        JobStatusEnum currentStatus = JobHandlerInvoker.status(identifier, converter.convert(instance));

        if (currentStatus != null && currentStatus.getCode() != instance.getTaskStatus()) {
            transactionTemplate.executeWithoutResult(status -> {
                taskInstanceMapper.update(Wrappers.<TaskInstance>lambdaUpdate()
                        .set(TaskInstance::getTaskStatus, currentStatus.getCode())
                        .eq(TaskInstance::getId, instance.getId()));
                log.info("Task instance {} status updated from {} to {}",
                        instance.getId(), instance.getTaskStatus(), currentStatus.getCode());

                // 状态变化事件（所有状态变化均写入）
                taskEventPublisher.publishStatusChanged(
                        instance.getId(), taskInfo.getId(),
                        instance.getTaskStatus(), currentStatus.getCode(), "status_sync");
            });

            // 更新内存中的状态，供下游服务使用（事务外）
            instance.setTaskStatus(currentStatus.getCode());
            // 如果是 FAILED 且为调度触发，安排自动重试
            if (currentStatus == JobStatusEnum.FAILED) {
                retrySchedulerService.scheduleRetryIfNeeded(instance);
            }
        }
    }
}
