package org.bigdata.server.bean.dto.cluster;

import lombok.Data;

/**
 * 集群查询条件DTO
 */
@Data
public class ClusterQueryDTO {
    /**
     * 集群名称
     */
    private String clusterName;
    
    /**
     * 集群类型
     */
    private String clusterType;
    
    /**
     * 状态
     */
    private Integer clusterStatus;
}