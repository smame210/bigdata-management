package org.bigdata.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.api.job.JobHandlerIdentifier;
import org.bigdata.api.job.JobHandlerInvoker;
import org.bigdata.server.bean.dto.task.instance.TaskInstanceQueryDTO;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.bean.vo.TaskInstanceVO;
import org.bigdata.server.convert.JobParamConverter;
import org.bigdata.server.convert.JobParamConverterInvoker;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.service.ITaskInstanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInstanceServiceImpl implements ITaskInstanceService {

    private final TaskInstanceMapper taskInstanceMapper;

    private final TaskInfoMapper taskInfoMapper;

    @Override
    public IPage<TaskInstanceVO> page(TaskInstanceQueryDTO taskInstanceQueryDTO) {
        Page<TaskInstance> page = new Page<>(taskInstanceQueryDTO.getCurrent(), taskInstanceQueryDTO.getSize());
        IPage<TaskInstance> taskInstanceIPage = taskInstanceMapper.page(page, taskInstanceQueryDTO);
        return taskInstanceIPage.convert(taskInstance -> {
            TaskInstanceVO taskInstanceVO = new TaskInstanceVO();
            BeanUtils.copyProperties(taskInstance, taskInstanceVO);
            return taskInstanceVO;
        });
    }

    @Override
    public Boolean kill(Integer id) {
        TaskInstance taskInstance = taskInstanceMapper.selectById(id);
        try {
            JobHandlerIdentifier identifier = new JobHandlerIdentifier(
                    taskInstance.getClusterType(), taskInstance.getEngineType(), taskInstance.getTaskType());
            JobParamConverter converter = JobParamConverterInvoker.convert(identifier);
            boolean result = JobHandlerInvoker.cancel(identifier, converter.convert(taskInstance));
            if (result) {
                taskInstanceMapper.update(Wrappers.<TaskInstance>lambdaUpdate()
                        .set(TaskInstance::getTaskStatus, JobStatusEnum.KILLED.getCode())
                        .eq(TaskInstance::getId, id));
                return true;
            }
        } catch (Exception e) {
            log.error("kill task instance error id:{}", id, e);
        }
        return false;
    }

    @Override
    public String getTrackingUrl(Integer id) {
        TaskInstance taskInstance = taskInstanceMapper.selectById(id);
        if (taskInstance == null) {
            return "";
        }

        TaskInfo taskInfo = taskInfoMapper.selectById(taskInstance.getTaskId());
        if (taskInfo == null) {
            return "";
        }

        try {
            JobHandlerIdentifier identifier = new JobHandlerIdentifier(
                    taskInstance.getClusterType(), taskInfo.getEngineType(), taskInstance.getTaskType());
            JobParamConverter converter = JobParamConverterInvoker.convert(identifier);
            Object params = converter.convert(taskInstance);
            return JobHandlerInvoker.getTrackingUrl(identifier, params);
        } catch (Exception e) {
            log.error("获取任务实例追踪URL失败, id:{}", id, e);
            return "";
        }
    }
}
