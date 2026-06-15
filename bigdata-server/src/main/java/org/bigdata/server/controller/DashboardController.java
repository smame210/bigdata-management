package org.bigdata.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.vo.DashboardStatisticsVO;
import org.bigdata.server.bean.vo.StatusDistributionVO;
import org.bigdata.server.bean.vo.TaskTrendVO;
import org.bigdata.server.service.IDashboardService;
import org.bigdata.server.util.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页仪表盘
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    /**
     * 获取首页统计概览
     */
    @GetMapping("/statistics")
    public R<DashboardStatisticsVO> getStatistics() {
        return R.ok(dashboardService.getStatistics());
    }

    /**
     * 获取近N天任务趋势
     *
     * @param days 天数，默认7
     */
    @GetMapping("/task-trend")
    public R<List<TaskTrendVO>> getTaskTrend(@RequestParam(defaultValue = "7") int days) {
        return R.ok(dashboardService.getTaskTrend(days));
    }

    /**
     * 获取任务状态分布
     */
    @GetMapping("/status-distribution")
    public R<List<StatusDistributionVO>> getStatusDistribution() {
        return R.ok(dashboardService.getStatusDistribution());
    }
}
