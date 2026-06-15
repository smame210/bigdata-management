package org.bigdata.server.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.bean.entity.TaskStatusEvent;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.service.IAlertTriggerService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventConsumer implements EventConsumer {

    private final IAlertTriggerService alertTriggerService;
    private final TaskInstanceMapper taskInstanceMapper;

    @Override
    public void handle(TaskStatusEvent event) {
        if ("status-changed".equals(event.getEventType())) {
            TaskInstance instance = taskInstanceMapper.selectById(event.getInstanceId());
            if (instance == null) {
                log.warn("TaskInstance not found for event, instanceId: {}", event.getInstanceId());
                return;
            }
            alertTriggerService.onTaskStatusChange(instance,
                    JobStatusEnum.getByCode(event.getNewStatus()));
        } else if ("timeout".equals(event.getEventType())) {
            TaskInstance instance = taskInstanceMapper.selectById(event.getInstanceId());
            if (instance == null) {
                log.warn("TaskInstance not found for timeout event, instanceId: {}", event.getInstanceId());
                return;
            }
            alertTriggerService.onTaskTimeout(instance);
        }
    }
}
