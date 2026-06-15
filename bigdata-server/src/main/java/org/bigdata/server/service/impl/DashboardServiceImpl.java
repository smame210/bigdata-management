package org.bigdata.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.vo.DashboardStatisticsVO;
import org.bigdata.server.bean.vo.StatusDistributionVO;
import org.bigdata.server.bean.vo.TaskTrendVO;
import org.bigdata.server.mapper.ClusterInfoMapper;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.service.IDashboardService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页仪表盘服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final TaskInfoMapper taskInfoMapper;
    private final TaskInstanceMapper taskInstanceMapper;
    private final ClusterInfoMapper clusterInfoMapper;

    @Override
    public DashboardStatisticsVO getStatistics() {
        DashboardStatisticsVO vo = new DashboardStatisticsVO();
        vo.setTotalTasks(taskInfoMapper.selectCount(Wrappers.emptyWrapper()));
        vo.setRunningInstances(taskInstanceMapper.selectCount(
                Wrappers.lambdaQuery(org.bigdata.server.bean.entity.TaskInstance.class)
                        .eq(org.bigdata.server.bean.entity.TaskInstance::getTaskStatus, 2)
        ));
        vo.setFailedInstances(taskInstanceMapper.selectCount(
                Wrappers.lambdaQuery(org.bigdata.server.bean.entity.TaskInstance.class)
                        .eq(org.bigdata.server.bean.entity.TaskInstance::getTaskStatus, 4)
        ));
        vo.setTotalClusters(clusterInfoMapper.selectCount(Wrappers.emptyWrapper()));
        log.debug("首页统计数据: {}", vo);
        return vo;
    }

    @Override
    public List<TaskTrendVO> getTaskTrend(int days) {
        return taskInstanceMapper.queryTaskTrend(days);
    }

    @Override
    public List<StatusDistributionVO> getStatusDistribution() {
        return taskInstanceMapper.queryStatusDistribution();
    }
}
