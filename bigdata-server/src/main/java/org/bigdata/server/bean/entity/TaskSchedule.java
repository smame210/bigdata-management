package org.bigdata.server.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_schedule")
public class TaskSchedule {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 关联的任务ID
     */
    private Integer taskId;

    /**
     * 任务名称
     */
    @TableField(exist = false)
    private String taskName;

    /**
     * 引擎类型
     */
    @TableField(exist = false)
    private String engineType;

    /**
     * 集群名称
     */
    @TableField(exist = false)
    private String clusterName;

    /**
     * 调度名称
     */
    private String scheduleName;

    /**
     * 任务实例名称
     */
    private String scheduleInstanceName;
    
    /**
     * 调度状态：0-禁用 1-启用
     */
    private Integer scheduleStatus;
    
    /**
     * 调度频率：ONCE-一次性, MINUTELY-每分钟, HOURLY-每小时, DAILY-每天, WEEKLY-每周, MONTHLY-每月, CUSTOM-自定义cron表达式
     */
    private String scheduleFrequency;
    
    /**
     * Cron表达式（当frequency为CUSTOM时使用）
     */
    private String cronExpression;
    
    /**
     * 下次运行时间
     */
    private LocalDateTime nextRunTime;
    
    /**
     * 最后运行时间
     */
    private LocalDateTime lastRunTime;
    
    /**
     * 调度生效开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 调度生效结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes;
    
    /**
     * 重试间隔（秒）
     */
    private Integer retryInterval;
    
    /**
     * 任务超时时间（秒）
     */
    private Integer timeoutSeconds;
    
    /**
     * 依赖的任务IDs（逗号分隔的任务ID列表）
     */
    private String dependencyTaskIds;
    
    /**
     * 任务参数（JSON格式）
     */
    private String params;
    
    /**
     * 记录创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 记录更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDeleted;
}