package org.bigdata.server.consumer;

import org.bigdata.server.bean.entity.TaskStatusEvent;

/**
 * 事件消费器接口，所有事件监听器需实现此接口
 */
public interface EventConsumer {
    void handle(TaskStatusEvent event);
}
