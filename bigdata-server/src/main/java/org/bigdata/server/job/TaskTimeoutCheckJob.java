package org.bigdata.server.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.publish.TaskEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 超时检查 Job
 *
 * @deprecated 超时逻辑暂不启用。
 * 原有硬编码 24 小时的规则不够合理，后续应改为：
 * 1. 读取调度配置中的 timeoutSeconds
 * 2. 超时后需先确认远端任务是否真实结束
 * 3. 如需要则调用 cancel 终止远端任务，而不仅仅是本地改状态
 */
@Deprecated
@Slf4j
//@Component
@RequiredArgsConstructor
public class TaskTimeoutCheckJob {

    private final TaskInstanceMapper taskInstanceMapper;

    private final TaskEventPublisher taskEventPublisher;

    private final TransactionTemplate transactionTemplate;

    /**
     * 默认超时时间：24小时
     */
    private static final long DEFAULT_TIMEOUT_HOURS = 24;

    /**
     * 每5分钟检查一次超时任务
     */
    @Scheduled(fixedDelay = 300000)
    public void checkTimeout() {
        List<TaskInstance> runningInstances = taskInstanceMapper.selectList(
                Wrappers.<TaskInstance>lambdaQuery()
                        .eq(TaskInstance::getTaskStatus, JobStatusEnum.RUNNING.getCode())
        );

        if (runningInstances.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (TaskInstance instance : runningInstances) {
            try {
                checkSingleInstance(instance, now);
            } catch (Exception e) {
                log.error("Failed to check timeout for instance id: {}", instance.getId(), e);
            }
        }
    }

    private void checkSingleInstance(TaskInstance instance, LocalDateTime now) {
        LocalDateTime launchTime = instance.getLaunchTime();
        if (launchTime == null) {
            launchTime = instance.getCreateTime();
        }

        if (launchTime == null) {
            return;
        }

        Duration duration = Duration.between(launchTime, now);
        long hours = duration.toHours();

        if (hours >= DEFAULT_TIMEOUT_HOURS) {
            log.warn("Task instance {} has been running for {} hours, marking as timeout", instance.getId(), hours);

            transactionTemplate.executeWithoutResult(status -> {
                taskInstanceMapper.update(Wrappers.<TaskInstance>lambdaUpdate()
                        .set(TaskInstance::getTaskStatus, JobStatusEnum.KILLED.getCode())
                        .eq(TaskInstance::getId, instance.getId()));

                // 写入超时事件
                taskEventPublisher.publishTimeout(
                        instance.getId(), instance.getTaskId(),
                        instance.getTaskStatus(), "timeout_check");
            });
        }
    }
}
