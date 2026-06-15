package org.bigdata.server.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.entity.TaskStatusEvent;
import org.bigdata.server.mapper.TaskStatusEventMapper;
import org.bigdata.server.consumer.EventConsumer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDispatchJob {

    private final TaskStatusEventMapper eventMapper;
    private final List<EventConsumer> consumers;

    @Scheduled(fixedDelay = 5000)
    public void dispatch() {
        List<TaskStatusEvent> events = eventMapper.selectList(Wrappers
                .<TaskStatusEvent>lambdaQuery()
                .eq(TaskStatusEvent::getEventStatus, "NEW")
                .last("LIMIT 50"));

        if (events.isEmpty()) {
            return;
        }

        log.debug("Dispatching {} status events", events.size());

        for (TaskStatusEvent event : events) {
            int updated = eventMapper.update(null,
                    Wrappers.<TaskStatusEvent>lambdaUpdate()
                            .set(TaskStatusEvent::getEventStatus, "PROCESSING")
                            .eq(TaskStatusEvent::getId, event.getId())
                            .eq(TaskStatusEvent::getEventStatus, "NEW"));
            if (updated != 1) {
                continue;
            }

            try {
                for (EventConsumer consumer : consumers) {
                    consumer.handle(event);
                }
                int done = eventMapper.update(null,
                        Wrappers.<TaskStatusEvent>lambdaUpdate()
                                .set(TaskStatusEvent::getEventStatus, "DONE")
                                .set(TaskStatusEvent::getUpdatedAt, LocalDateTime.now())
                                .eq(TaskStatusEvent::getId, event.getId()));
                if (done != 1) {
                    log.error("Failed to mark event {} as DONE, row may have been deleted", event.getId());
                }
            } catch (Exception e) {
                log.error("Failed to process event id: {}", event.getId(), e);
                String errMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                int failed = eventMapper.update(null,
                        Wrappers.<TaskStatusEvent>lambdaUpdate()
                                .set(TaskStatusEvent::getEventStatus, "FAILED")
                                .set(TaskStatusEvent::getErrorMsg, errMsg)
                                .set(TaskStatusEvent::getUpdatedAt, LocalDateTime.now())
                                .eq(TaskStatusEvent::getId, event.getId()));
                if (failed != 1) {
                    log.error("Failed to mark event {} as FAILED, row may have been deleted", event.getId());
                }
            }
        }
    }
}
