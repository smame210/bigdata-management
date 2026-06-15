package org.bigdata.server.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务信息实体类
 */
@Data
@TableName("task_info")
public class TaskInfo {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 元数据
     */
    private String metadata;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDeleted;
}