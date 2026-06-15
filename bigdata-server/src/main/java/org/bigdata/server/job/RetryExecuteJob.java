package org.bigdata.server.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.scheduler.core.SchedulerJobBean;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.bean.entity.TaskSchedule;
import org.bigdata.server.enums.ScheduleStatusEnum;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.mapper.TaskScheduleMapper;
import org.bigdata.server.service.ITaskExecutionService;
import org.bigdata.server.service.RetrySchedulerService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Quartz 延迟重试任务。
 * 由 RetrySchedulerService 创建，在设定的 retryInterval 时间后执行一次重新提交。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetryExecuteJob extends SchedulerJobBean {

    private final TaskScheduleMapper taskScheduleMapper;

    private final TaskInstanceMapper taskInstanceMapper;

    private final ITaskExecutionService taskExecutionService;

    private final RetrySchedulerService retrySchedulerService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        Integer taskId = (Integer) dataMap.get("taskId");
        Integer scheduleId = (Integer) dataMap.get("scheduleId");
        Integer parentInstanceId = (Integer) dataMap.get("parentInstanceId");
        Integer rootInstanceId = (Integer) dataMap.get("rootInstanceId");
        Integer retryCount = (Integer) dataMap.get("retryCount");

        // 1. 检查调度是否仍处于启用状态
        TaskSchedule schedule = taskScheduleMapper.selectById(scheduleId);
        if (schedule == null || !ScheduleStatusEnum.ENABLE.getCode().equals(schedule.getScheduleStatus())) {
            log.warn("Schedule {} is disabled or deleted, skipping retry for instance {}", scheduleId, parentInstanceId);
            return;
        }

        try {
            // 2. 重新提交任务
            TaskInstance newInstance = taskExecutionService.submitTask(taskId, schedule);

            // 3. 更新重试链路字段
            taskInstanceMapper.update(Wrappers.<TaskInstance>lambdaUpdate()
                    .set(TaskInstance::getRetryCount, retryCount + 1)
                    .set(TaskInstance::getRootInstanceId, rootInstanceId)
                    .set(TaskInstance::getParentInstanceId, parentInstanceId)
                    .eq(TaskInstance::getId, newInstance.getId()));

            log.info("Retry execute success: parentInstance={}, newInstance={}, attempt={}",
                    parentInstanceId, newInstance.getId(), retryCount + 1);
        } catch (Exception e) {
            log.error("Retry execute failed for parentInstance={}", parentInstanceId, e);

            // 4. 即使提交失败，doSubmit() 也会插入一条 FAILED 实例
            //    找到这条实例，补上重试链路信息，然后继续进入重试链
            try {
                List<TaskInstance> recentFailed = taskInstanceMapper.selectList(
                        Wrappers.<TaskInstance>lambdaQuery()
                                .eq(TaskInstance::getTaskId, taskId)
                                .eq(TaskInstance::getScheduleId, scheduleId)
                                .eq(TaskInstance::getTaskStatus, JobStatusEnum.FAILED.getCode())
                                .orderByDesc(TaskInstance::getId)
                                .last("LIMIT 1")
                );
                if (!recentFailed.isEmpty()) {
                    TaskInstance failedInstance = recentFailed.get(0);
                    // 补上重试链路信息
                    taskInstanceMapper.update(null,
                            Wrappers.<TaskInstance>lambdaUpdate()
                                    .set(TaskInstance::getRetryCount, retryCount + 1)
                                    .set(TaskInstance::getRootInstanceId, rootInstanceId)
                                    .set(TaskInstance::getParentInstanceId, parentInstanceId)
                                    .eq(TaskInstance::getId, failedInstance.getId())
                    );
                    failedInstance.setRetryCount(retryCount + 1);
                    failedInstance.setRootInstanceId(rootInstanceId);
                    failedInstance.setParentInstanceId(parentInstanceId);
                    // 尝试安排下一次重试
                    retrySchedulerService.scheduleRetryIfNeeded(failedInstance);
                }
            } catch (Exception ex) {
                log.error("Failed to continue retry chain after submit failure", ex);
            }
        }
    }
}
