package org.bigdata.server.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 集群信息实体
 */
@Data
@TableName("cluster_info")
public class ClusterInfo {
    /**
     * 集群ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 集群名称
     */
    private String clusterName;
    
    /**
     * 集群类型
     */
    private String clusterType;
    
    /**
     * 状态（0-停用，1-启用）
     */
    private Integer clusterStatus;

    /**
     * 健康状态（0-未知，1-健康，2-降级，3-异常）
     */
    private Integer healthStatus;

    /**
     * 最近健康检查时间
     */
    private LocalDateTime healthCheckedAt;
    
    /**
     * 描述
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
