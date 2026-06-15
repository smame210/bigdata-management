package org.bigdata.server.bean.dto.task;

import lombok.Data;
import org.bigdata.server.bean.PageQueryDTO;

import java.time.LocalDateTime;

/**
 * 任务查询条件DTO
 */
@Data
public class TaskQueryDTO extends PageQueryDTO {
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 引擎类型
     */
    private String engineType;
    
    /**
     * 任务状态
     */
    private Integer taskStatus;
    
    /**
     * 任务运行模式
     */
    private String taskMode;
    
    /**
     * 集群ID
     */
    private Integer clusterId;
    
    /**
     * 创建开始时间
     */
    private LocalDateTime createTimeStart;
    
    /**
     * 创建结束时间
     */
    private LocalDateTime createTimeEnd;
}