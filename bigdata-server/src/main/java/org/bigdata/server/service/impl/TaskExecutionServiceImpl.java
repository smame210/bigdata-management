package org.bigdata.server.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.api.job.JobHandlerIdentifier;
import org.bigdata.api.job.JobHandlerInvoker;
import org.bigdata.server.bean.dto.task.FlinkTaskConfigDTO;
import org.bigdata.server.bean.entity.ClusterInfo;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.bean.entity.TaskSchedule;
import org.bigdata.server.convert.JobParamConverter;
import org.bigdata.server.convert.JobParamConverterInvoker;
import org.bigdata.server.enums.ClusterStatusEnum;
import org.bigdata.server.exception.BizException;
import org.bigdata.server.mapper.ClusterInfoMapper;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.mapper.TaskStatusEventMapper;
import org.bigdata.server.service.ITaskExecutionService;
import org.bigdata.server.publish.TaskEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecutionServiceImpl implements ITaskExecutionService {

    private final TaskInfoMapper taskInfoMapper;

    private final ClusterInfoMapper clusterInfoMapper;

    private final TaskInstanceMapper taskInstanceMapper;

    private final TaskEventPublisher taskEventPublisher;

    private final TransactionTemplate transactionTemplate;

    @Override
    public TaskInstance submitTask(Integer taskId) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        if (taskInfo == null) {
            throw new BizException("任务不存在");
        }
        return doSubmit(taskInfo, null);
    }

    @Override
    public TaskInstance submitTask(Integer taskId, TaskSchedule taskSchedule) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        if (taskInfo == null) {
            throw new BizException("任务不存在");
        }
        return doSubmit(taskInfo, taskSchedule);
    }

    private TaskInstance doSubmit(TaskInfo taskInfo, TaskSchedule taskSchedule) {
        FlinkTaskConfigDTO flinkTaskConfigDTO = JSON.parseObject(taskInfo.getMetadata(), FlinkTaskConfigDTO.class);
        ClusterInfo clusterInfo = clusterInfoMapper.selectById(taskInfo.getClusterId());
        if (clusterInfo == null) {
            throw new BizException("集群不存在");
        }
        if (!ClusterStatusEnum.ENABLE.getCode().equals(clusterInfo.getClusterStatus())) {
            throw new BizException("集群已停用，无法执行任务");
        }
        validateRequiredFiles(flinkTaskConfigDTO, clusterInfo);

        TaskInstance taskInstance = buildTaskInstance(taskInfo, clusterInfo, taskSchedule, flinkTaskConfigDTO);

        try {
            JobHandlerIdentifier identifier = new JobHandlerIdentifier(
                    clusterInfo.getClusterType(), taskInfo.getEngineType(), flinkTaskConfigDTO.getTaskType());
            JobParamConverter converter = JobParamConverterInvoker.convert(identifier);
            String jobId = JobHandlerInvoker.JobSubmit(identifier, converter.convert(taskInstance));

            // 远程提交成功，在事务内写入运行中实例
            TaskInstance finalInstance = taskInstance;
            transactionTemplate.executeWithoutResult(status -> {
                finalInstance.setTaskInstanceId(jobId);
                finalInstance.setTaskStatus(JobStatusEnum.RUNNING.getCode());
                taskInstanceMapper.insert(finalInstance);
            });
            log.info("task submit success, task instance id: {}", jobId);
            return taskInstance;
        } catch (Exception e) {
            log.error("task submit failed, task id: {}", taskInfo.getId(), e);

            // 远程提交失败，在事务内写入失败实例和事件
            taskInstance.setTaskStatus(JobStatusEnum.FAILED.getCode());
            transactionTemplate.executeWithoutResult(status -> {
                taskInstanceMapper.insert(taskInstance);
                taskEventPublisher.publishStatusChanged(
                        taskInstance.getId(), taskInfo.getId(),
                        null, JobStatusEnum.FAILED.getCode(), "submit");
            });

            throw new BizException("任务提交失败: " + e.getMessage());
        }
    }

    private void validateRequiredFiles(FlinkTaskConfigDTO flinkTaskConfigDTO, ClusterInfo clusterInfo) {
        validateFileIfLocal("任务JAR文件", flinkTaskConfigDTO.getJarPath());
        validateFileIfLocal("Flink配置文件(flink-conf.yaml)", flinkTaskConfigDTO.getFlinkConfPath());
        validateFileIfLocal("日志配置文件", flinkTaskConfigDTO.getLogConfPath());

    }

    private void validateFileIfLocal(String fileLabel, String filePath) {
        if (filePath == null || filePath.isBlank() || filePath.contains("://")) {
            return;
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new BizException(fileLabel + "不存在或不可读，请重新上传后重试");
        }
    }

    private TaskInstance buildTaskInstance(TaskInfo taskInfo, ClusterInfo clusterInfo, TaskSchedule taskSchedule, FlinkTaskConfigDTO flinkTaskConfigDTO) {
        TaskInstance.TaskInstanceBuilder builder = TaskInstance.builder()
                .taskId(taskInfo.getId())
                .taskName(taskInfo.getTaskName())
                .taskType(flinkTaskConfigDTO.getTaskType())
                .taskMode(taskInfo.getTaskMode())
                .taskStatus(JobStatusEnum.CREATED.getCode())
                .taskMetadata(taskInfo.getMetadata())
                .clusterId(clusterInfo.getId())
                .clusterType(clusterInfo.getClusterType())
                .clusterMetadata(clusterInfo.getMetadata());

        if (taskSchedule != null) {
            builder.scheduleId(taskSchedule.getId())
                    .scheduleName(taskSchedule.getScheduleName())
                    .scheduleInstanceName(taskSchedule.getScheduleInstanceName());
        }

        return builder.build();
    }
}
