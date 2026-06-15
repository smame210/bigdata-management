package org.bigdata.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.cluster.AbstractClusterInfoDTO;
import org.bigdata.server.bean.dto.cluster.ClusterQueryDTO;
import org.bigdata.server.bean.entity.ClusterInfo;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.vo.ClusterVO;
import org.bigdata.server.enums.ClusterHealthStatusEnum;
import org.bigdata.server.enums.ClusterStatusEnum;
import org.bigdata.server.exception.BizException;
import org.bigdata.server.mapper.ClusterInfoMapper;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.service.IClusterService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 集群服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterServiceImpl implements IClusterService {
    private final TaskInfoMapper taskInfoMapper;

    private final ClusterInfoMapper clusterInfoMapper;

    @Override
    public List<ClusterVO> pageCluster(ClusterQueryDTO queryDTO) {
        List<ClusterInfo> clusterInfoList = clusterInfoMapper.queryClusterByPage(queryDTO);
        return clusterInfoList.stream()
                .map(cluster -> {
                    ClusterVO vo = new ClusterVO();
                    BeanUtils.copyProperties(cluster, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer addCluster(AbstractClusterInfoDTO clusterInfoDTO) {
        ClusterInfo clusterInfo = clusterInfoDTO.toEntity();
        clusterInfo.setId(null);
        clusterInfo.setHealthStatus(ClusterHealthStatusEnum.UNKNOWN.getCode());
        clusterInfo.setHealthCheckedAt(null);
        return clusterInfoMapper.insert(clusterInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer updateCluster(AbstractClusterInfoDTO clusterInfoDTO) {
        Integer id = clusterInfoDTO.getId();
        ClusterInfo clusterInfoOld = clusterInfoMapper.selectById(id);
        if (clusterInfoOld == null) {
            throw new BizException("集群不存在");
        }
        if (clusterInfoOld.getClusterStatus().equals(ClusterStatusEnum.ENABLE.getCode())) {
            throw new BizException("集群启用中！");
        }
        
        ClusterInfo clusterInfo = clusterInfoDTO.toEntity();
        return clusterInfoMapper.updateById(clusterInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer deleteCluster(Integer id) {
        Long taskCount = taskInfoMapper.selectCount(Wrappers.<TaskInfo>lambdaQuery()
                .eq(TaskInfo::getClusterId, id));
        if (taskCount > 0) {
            throw new BizException("该集群已绑定任务！");
        }
        return clusterInfoMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer deleteClusterBatch(List<Integer> id) {
        if (id == null || id.isEmpty()) {
            return 0;
        }

        List<TaskInfo> taskInfos = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery()
                .select(TaskInfo::getClusterId)
                .in(TaskInfo::getClusterId, id));
        List<Integer> taskClusterIds = taskInfos.stream()
                .map(TaskInfo::getClusterId)
                .distinct()
                .collect(Collectors.toList());
        id.removeAll(taskClusterIds);
        return clusterInfoMapper.deleteByIds(id);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer updateClusterStatus(Integer id, Integer status) {
        if (Arrays.stream(ClusterStatusEnum.values())
                .noneMatch(allowStatus -> allowStatus.getCode().equals(status))) {
            throw new BizException("无效的集群状态值");
        }
        return clusterInfoMapper.update(null, Wrappers.<ClusterInfo>lambdaUpdate()
                .set(ClusterInfo::getClusterStatus, status)
                .eq(ClusterInfo::getId, id));
    }

    @Override
    public Integer updateClusterStatusBatch(Map<Integer, Integer> idStatus) {
        if (idStatus == null || idStatus.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Map.Entry<Integer, Integer> entry : idStatus.entrySet()) {
            if (Arrays.stream(ClusterStatusEnum.values())
                    .noneMatch(allowStatus -> allowStatus.getCode().equals(entry.getValue()))) {
                throw new BizException("无效的集群状态值");
            }
            count += clusterInfoMapper.update(null, Wrappers.<ClusterInfo>lambdaUpdate()
                    .set(ClusterInfo::getClusterStatus, entry.getValue())
                    .eq(ClusterInfo::getId, entry.getKey()));
        }
        return count;
    }

    @Override
    public ClusterVO getById(Integer id) {
        ClusterInfo clusterInfo = clusterInfoMapper.selectById(id);
        if (clusterInfo == null) {
            return null;
        }
        ClusterVO vo = new ClusterVO();
        BeanUtils.copyProperties(clusterInfo, vo);
        return vo;
    }
}
