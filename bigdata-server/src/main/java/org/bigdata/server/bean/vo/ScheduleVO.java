package org.bigdata.server.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleVO {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 关联的任务ID
     */
    private Integer taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 引擎类型
     */
    private String engineType;

    /**
     * 集群名称
     */
    private String clusterName;

    /**
     * 调度名称
     */
    private String scheduleName;
    
    /**
     * 调度状态：0-禁用 1-启用
     */
    private Integer scheduleStatus = 1;
    
    /**
     * 调度频率：ONCE-一次性, MINUTELY-每分钟, HOURLY-每小时, DAILY-每天, WEEKLY-每周, MONTHLY-每月, CUSTOM-自定义cron表达式
     */
    private String scheduleFrequency;
    
    /**
     * Cron表达式（当frequency为CUSTOM时使用）
     */
    private String cronExpression;
    
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
    private Integer maxRetryTimes = 0;
    
    /**
     * 重试间隔（秒）
     */
    private Integer retryInterval = 0;
    
    /**
     * 任务超时时间（秒）
     */
    private Integer timeoutSeconds;
    
    /**
     * 依赖的任务IDs（逗号分隔的任务ID列表）
     */
    private String dependencyTaskIds;


    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}