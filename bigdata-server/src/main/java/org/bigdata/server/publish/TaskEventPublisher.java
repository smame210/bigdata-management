package org.bigdata.server.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.server.bean.entity.TaskStatusEvent;
import org.bigdata.server.mapper.TaskStatusEventMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

    private final TaskStatusEventMapper eventMapper;

    public void publishStatusChanged(Integer instanceId, Integer taskId,
                                      Integer oldStatus, Integer newStatus, String source) {
        TaskStatusEvent event = new TaskStatusEvent()
                .setInstanceId(instanceId)
                .setTaskId(taskId)
                .setEventType("status-changed")
                .setOldStatus(oldStatus)
                .setNewStatus(newStatus)
                .setSource(source)
                .setEventStatus("NEW")
                .setCreatedAt(LocalDateTime.now());
        eventMapper.insert(event);
        log.debug("Published status-changed event: instanceId={}, taskId={}, newStatus={}, source={}",
                instanceId, taskId, newStatus, source);
    }

    public void publishTimeout(Integer instanceId, Integer taskId,
                                Integer oldStatus, String source) {
        TaskStatusEvent event = new TaskStatusEvent()
                .setInstanceId(instanceId)
                .setTaskId(taskId)
                .setEventType("timeout")
                .setOldStatus(oldStatus)
                .setNewStatus(JobStatusEnum.KILLED.getCode())
                .setSource(source)
                .setEventStatus("NEW")
                .setCreatedAt(LocalDateTime.now());
        eventMapper.insert(event);
    }
}
