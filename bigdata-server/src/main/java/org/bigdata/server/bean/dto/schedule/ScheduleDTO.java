package org.bigdata.server.bean.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 关联的任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Integer taskId;
    
    /**
     * 调度名称
     */
    @NotBlank(message = "调度名称不能为空")
    private String scheduleName;
    
    /**
     * 调度状态：0-禁用 1-启用
     */
    private Integer scheduleStatus = 1;
    
    /**
     * 调度频率：ONCE-一次性, MINUTELY-每分钟, HOURLY-每小时, DAILY-每天, WEEKLY-每周, MONTHLY-每月, CUSTOM-自定义cron表达式
     */
    @NotBlank(message = "调度频率不能为空")
    private String scheduleFrequency;
    
    /**
     * Cron表达式（当frequency为CUSTOM时使用）
     */
    private String cronExpression;
    
    /**
     * 调度生效开始时间
     */
    @NotNull(message = "开始时间不可为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 调度生效结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 最大重试次数
     */
    @Min(value = 0, message = "最大重试次数不能为负数")
    private Integer maxRetryTimes = 0;

    /**
     * 重试间隔（秒）
     */
    @Min(value = 0, message = "重试间隔不能为负数")
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
     * 任务参数（JSON格式）
     */
    private String params;
}