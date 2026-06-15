package org.bigdata.server.bean.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务信息视图对象
 */
@Data
public class TaskVO {
    /**
     * 主键ID
     */
    private Integer id;
    
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
     * 集群名称
     */
    private String clusterName;

    /**
     * 集群状态（0-停用，1-启用）
     */
    private Integer clusterStatus;

    /**
     * 元数据
     */
    private String config;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
