package org.bigdata.server.service;

import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.server.bean.entity.TaskInstance;

/**
 * 告警触发服务
 */
public interface IAlertTriggerService {

    /**
     * 任务状态变化时触发告警检查
     *
     * @param taskInstance 任务实例
     * @param newStatus    新状态
     */
    void onTaskStatusChange(TaskInstance taskInstance, JobStatusEnum newStatus);

    /**
     * 任务超时时触发告警
     *
     * @param taskInstance 任务实例
     */
    void onTaskTimeout(TaskInstance taskInstance);
}
