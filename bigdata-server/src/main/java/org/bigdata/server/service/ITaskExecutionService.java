package org.bigdata.server.service;

import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.bean.entity.TaskSchedule;

/**
 * 任务执行服务，封装任务提交的核心逻辑
 */
public interface ITaskExecutionService {

    /**
     * 手动提交任务（无调度上下文）
     *
     * @param taskId 任务ID
     * @return 创建的任务实例
     */
    TaskInstance submitTask(Integer taskId);

    /**
     * 通过调度提交任务
     *
     * @param taskId      任务ID
     * @param taskSchedule 调度信息
     * @return 创建的任务实例
     */
    TaskInstance submitTask(Integer taskId, TaskSchedule taskSchedule);
}
