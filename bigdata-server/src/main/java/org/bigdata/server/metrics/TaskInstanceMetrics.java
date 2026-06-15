package org.bigdata.server.metrics;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.api.job.JobHandlerIdentifier;
import org.bigdata.api.job.JobHandlerInvoker;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.convert.JobParamConverter;
import org.bigdata.server.convert.JobParamConverterInvoker;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TaskInstanceMetrics {

    private final TaskInstanceMapper taskInstanceMapper;

    private final TaskInfoMapper taskInfoMapper;

    private final Set<Integer> jobStatusSet = ImmutableSet.of(
            JobStatusEnum.UNKNOWN.getCode(),
            JobStatusEnum.CREATED.getCode(),
            JobStatusEnum.ACCEPTED.getCode(),
            JobStatusEnum.RUNNING.getCode()
    );

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void schedule() {
        List<TaskInstance> taskInstances = taskInstanceMapper.selectList(Wrappers.<TaskInstance>lambdaQuery()
                .in(TaskInstance::getTaskStatus, jobStatusSet));
        List<CompletableFuture<Pair<Integer, Integer>>> futures = new ArrayList<>();
        for (TaskInstance taskInstance : taskInstances) {
            CompletableFuture<Pair<Integer, Integer>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    TaskInfo taskInfo = taskInfoMapper.selectById(taskInstance.getTaskId());
                    if (taskInfo == null) {
                        log.warn("TaskInfo not found for instance: {}", taskInstance.getId());
                        return null;
                    }
                    JobHandlerIdentifier identifier = new JobHandlerIdentifier(
                            taskInstance.getClusterType(), taskInfo.getEngineType(), taskInstance.getTaskType());
                    JobParamConverter converter = JobParamConverterInvoker.convert(identifier);
                    JobStatusEnum status = JobHandlerInvoker.status(identifier, converter.convert(taskInstance));
                    return Pair.of(taskInstance.getId(), status.getCode());
                } catch (Exception e) {
                    log.error("check task instance status id: [{}] error: ", taskInstance.getId(), e);
                }
                return null;
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<Pair<Integer, Integer>> future : futures) {
            Pair<Integer, Integer> pair = future.join();
            if (pair == null) {
                continue;
            }
            try {
                taskInstanceMapper.update(null, Wrappers.<TaskInstance>lambdaUpdate()
                        .eq(TaskInstance::getId, pair.getLeft())
                        .set(TaskInstance::getTaskStatus, pair.getRight()));
            } catch (Exception e) {
                log.error("update task instance status id: [{}] error: ", pair.getLeft(), e);
            }
        }
    }

}
