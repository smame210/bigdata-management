package org.bigdata.server.bean.vo;

import lombok.Data;

/**
 * 首页统计概览数据
 */
@Data
public class DashboardStatisticsVO {
    private long totalTasks;
    private long runningInstances;
    private long failedInstances;
    private long totalClusters;
}
