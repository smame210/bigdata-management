package org.bigdata.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.task.AbstractTaskInfoDTO;
import org.bigdata.server.bean.dto.task.TaskQueryDTO;
import org.bigdata.server.bean.entity.ClusterInfo;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.entity.TaskSchedule;
import org.bigdata.server.bean.vo.TaskVO;
import org.bigdata.server.enums.TaskStatusEnum;
import org.bigdata.server.exception.BizException;
import org.bigdata.server.mapper.ClusterInfoMapper;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.mapper.TaskScheduleMapper;
import org.bigdata.server.service.ITaskExecutionService;
import org.bigdata.server.service.ITaskService;
import org.bigdata.api.job.JobHandlerInvoker;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements ITaskService {

    private final TaskInfoMapper taskInfoMapper;

    private final TaskScheduleMapper taskScheduleMapper;

    private final ClusterInfoMapper clusterInfoMapper;

    private final ITaskExecutionService taskExecutionService;

    @Override
    public IPage<TaskVO> pageTask(TaskQueryDTO queryDTO) {
        Page<TaskInfo> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<TaskInfo> taskInfoIPage = taskInfoMapper.queryTaskByPage(page, queryDTO);
        return taskInfoIPage
                .convert(e -> {
                    TaskVO taskVO = new TaskVO();
                    BeanUtils.copyProperties(e, taskVO);
                    if (e.getClusterId() != null) {
                        ClusterInfo clusterInfo = clusterInfoMapper.selectById(e.getClusterId());
                        if (clusterInfo != null) {
                            taskVO.setClusterName(clusterInfo.getClusterName());
                            taskVO.setClusterStatus(clusterInfo.getClusterStatus());
                        }
                    }
                    return taskVO;
                });
    }

    @Override
    public List<TaskVO> listAllTask() {
        List<TaskInfo> taskInfos = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery());
        return taskInfos.stream()
                .map(e -> {
                    TaskVO taskVO = new TaskVO();
                    BeanUtils.copyProperties(e, taskVO);
                    if (e.getClusterId() != null) {
                        ClusterInfo clusterInfo = clusterInfoMapper.selectById(e.getClusterId());
                        if (clusterInfo != null) {
                            taskVO.setClusterName(clusterInfo.getClusterName());
                            taskVO.setClusterStatus(clusterInfo.getClusterStatus());
                        }
                    }
                    return taskVO;
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer add(AbstractTaskInfoDTO taskInfoDTO) {
        if (taskInfoDTO.getClusterId() != null) {
            ClusterInfo clusterInfo = clusterInfoMapper.selectById(taskInfoDTO.getClusterId());
            if (clusterInfo == null) {
                throw new BizException("为查询到该集群信息！");
            }
        }

        TaskInfo taskInfo = taskInfoDTO.toEntity();
        taskInfo.setId(null);
        taskInfo.setTaskStatus(TaskStatusEnum.DISABLE.getCode());
        return taskInfoMapper.insert(taskInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer deleteBatchById(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        // 排除查询调度的绑定
        List<TaskSchedule> taskSchedules = taskScheduleMapper.selectList(Wrappers.<TaskSchedule>lambdaQuery()
                .in(TaskSchedule::getTaskId, ids));
        if (taskSchedules != null && !taskSchedules.isEmpty()) {
            List<Integer> bindIds = taskSchedules.stream()
                    .map(TaskSchedule::getId)
                    .distinct()
                    .collect(Collectors.toList());
            ids.removeAll(bindIds);
        }
        if (ids.isEmpty()) {
            return 0;
        }
        return taskInfoMapper.delete(Wrappers.<TaskInfo>lambdaUpdate()
                .in(TaskInfo::getId, ids)
                .notIn(TaskInfo::getTaskStatus, TaskStatusEnum.ENABLE.getCode())
        );
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer update(AbstractTaskInfoDTO taskInfoDTO) {
        if (taskInfoDTO.getId() == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (taskInfoDTO.getClusterId() != null) {
            ClusterInfo clusterInfo = clusterInfoMapper.selectById(taskInfoDTO.getClusterId());
            if (clusterInfo == null) {
                throw new BizException("为查询到该集群信息！");
            }
        }
        TaskInfo taskInfo = taskInfoDTO.toEntity();
        taskInfo.setTaskStatus(null);
        return taskInfoMapper.updateById(taskInfo);
    }

    @Override
    public TaskVO getById(Integer id) {
        // 查询任务信息
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        if (taskInfo == null) {
            return null;
        }

        TaskVO taskVO = new TaskVO();
        BeanUtils.copyProperties(taskInfo, taskVO);
        taskVO.setConfig(taskInfo.getMetadata());
        if (taskInfo.getClusterId() != null) {
            ClusterInfo clusterInfo = clusterInfoMapper.selectById(taskInfo.getClusterId());
            if (clusterInfo != null) {
                taskVO.setClusterName(clusterInfo.getClusterName());
                taskVO.setClusterStatus(clusterInfo.getClusterStatus());
            }
        }
        return taskVO;
    }

    @Override
    public Integer executeTask(Integer id) {
        taskExecutionService.submitTask(id);
        return 1;
    }

    @Override
    public List<String> listSupportedTaskTypes(String clusterType, String engineType) {
        if (clusterType == null || clusterType.isBlank() || engineType == null || engineType.isBlank()) {
            return List.of();
        }
        return JobHandlerInvoker.listSupportedJobModes(clusterType, engineType);
    }
}
