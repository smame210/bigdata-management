package org.bigdata.server.service;

import org.bigdata.server.bean.vo.DashboardStatisticsVO;
import org.bigdata.server.bean.vo.StatusDistributionVO;
import org.bigdata.server.bean.vo.TaskTrendVO;

import java.util.List;

/**
 * 首页仪表盘服务
 */
public interface IDashboardService {

    /**
     * 获取首页统计概览
     */
    DashboardStatisticsVO getStatistics();

    /**
     * 获取近N天任务趋势
     */
    List<TaskTrendVO> getTaskTrend(int days);

    /**
     * 获取任务状态分布
     */
    List<StatusDistributionVO> getStatusDistribution();
}
