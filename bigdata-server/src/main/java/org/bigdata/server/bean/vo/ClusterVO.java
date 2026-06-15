package org.bigdata.server.bean.vo;

import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 集群信息VO
 */
@Data
public class ClusterVO {
    /**
     * 集群ID
     */
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime healthCheckedAt;
    
    /**
     * 元数据
     */
    private String metadata;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
