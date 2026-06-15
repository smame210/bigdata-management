package org.bigdata.server.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.entity.TaskStatusEvent;
import org.bigdata.server.mapper.TaskStatusEventMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCleanupJob {

    private final TaskStatusEventMapper eventMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanup() {
        int deleted = eventMapper.delete(Wrappers.<TaskStatusEvent>lambdaQuery()
                .eq(TaskStatusEvent::getEventStatus, "DONE")
                .lt(TaskStatusEvent::getCreatedAt, LocalDateTime.now().minusDays(7)));
        if (deleted > 0) {
            log.info("Cleaned up {} expired status events", deleted);
        }
    }
}
