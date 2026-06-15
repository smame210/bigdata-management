package org.bigdata.server.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bigdata.scheduler.bean.SchedulerCreateParam;
import org.bigdata.scheduler.core.AbstractSchedulerProvider;
import org.bigdata.scheduler.core.SchedulerProviderRegistry;
import org.bigdata.scheduler.enums.ScheduledType;
import org.bigdata.server.bean.dto.schedule.ScheduleDTO;
import org.bigdata.server.bean.dto.schedule.ScheduleQueryDTO;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.bean.entity.TaskSchedule;
import org.bigdata.server.bean.vo.ScheduleVO;
import org.bigdata.server.enums.Frequency;
import org.bigdata.server.enums.ScheduleStatusEnum;
import org.bigdata.server.enums.TaskModeEnum;
import org.bigdata.server.exception.BizException;
import org.bigdata.server.job.JobBeanClassEnum;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.mapper.TaskInstanceMapper;
import org.bigdata.server.mapper.TaskScheduleMapper;
import org.bigdata.server.service.IScheduleService;
import org.bigdata.server.util.CronUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements IScheduleService {

    private final SchedulerProviderRegistry schedulerProviderRegistry;

    private final TaskScheduleMapper taskScheduleMapper;

    private final TaskInfoMapper taskInfoMapper;

    private final TaskInstanceMapper taskInstanceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(ScheduleDTO scheduleDTO) {
        // 验证任务是否存在
        TaskInfo taskInfo = taskInfoMapper.selectById(scheduleDTO.getTaskId());
        if (taskInfo == null) {
            throw new BizException("任务不存在");
        }
        // 验证cron表达式
        if (Frequency.CUSTOM.name().equals(scheduleDTO.getScheduleFrequency())) {
            if (StringUtils.isAllBlank(scheduleDTO.getCronExpression())) {
                throw new BizException("自定义调度需要设置Cron表达式");
            }
            if (!CronUtil.isCronExpression(scheduleDTO.getCronExpression())) {
                throw new BizException("Cron表达式错误！");
            }
        }
        if (TaskModeEnum.STREAMING.name().equalsIgnoreCase(taskInfo.getTaskMode()) &&
                !Frequency.ONCE.name().equals(scheduleDTO.getScheduleFrequency())) {
            throw new BizException("流处理任务只支持单次调度频率！");
        }

        Long count = taskScheduleMapper.selectCount(Wrappers.<TaskSchedule>lambdaQuery()
                .eq(TaskSchedule::getScheduleName, scheduleDTO.getScheduleName()));
        if (count > 0) {
            throw new BizException("名称重复");
        }

        // 转换为实体对象
        TaskSchedule taskSchedule = new TaskSchedule();
        BeanUtils.copyProperties(scheduleDTO, taskSchedule);
        taskSchedule.setId(null);
        taskSchedule.setScheduleStatus(ScheduleStatusEnum.DISABLE.getCode());
        return taskScheduleMapper.insert(taskSchedule);
    }

    @Override
    public ScheduleVO getScheduleById(Integer id) {
        TaskSchedule taskSchedule = taskScheduleMapper.selectById(id);
        if (taskSchedule == null) {
            return null;
        }
        TaskInfo taskInfo = taskInfoMapper.selectById(taskSchedule.getTaskId());

        ScheduleVO scheduleVO = new ScheduleVO();
        BeanUtils.copyProperties(taskSchedule, scheduleVO);
        scheduleVO.setTaskName(taskInfo != null ? taskInfo.getTaskName() : null);
        return scheduleVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateSchedule(Integer id, ScheduleDTO scheduleDTO) {
        TaskSchedule taskSchedule = taskScheduleMapper.selectById(id);
        if (taskSchedule == null) {
            return 0;
        }
        if (!Objects.equals(taskSchedule.getTaskId(), scheduleDTO.getTaskId())) {
            throw new BizException("调度的任务不可变更");
        }
        if (!Frequency.support(scheduleDTO.getScheduleFrequency())) {
            throw new BizException("不支持的调度频率");
        }
        if ("CUSTOM".equals(scheduleDTO.getScheduleFrequency()) &&
                StringUtils.isAllBlank(scheduleDTO.getCronExpression())) {
            throw new BizException("自定义调度需要设置Cron表达式");
        }
        TaskInfo taskInfo = taskInfoMapper.selectById(taskSchedule.getTaskId());
        if (TaskModeEnum.STREAMING.name().equalsIgnoreCase(taskInfo.getTaskMode()) &&
                !Frequency.ONCE.name().equals(scheduleDTO.getScheduleFrequency())) {
            throw new BizException("流处理任务只支持单次调度频率！");
        }

        // 更新前的状态
        Integer oldStatus = taskSchedule.getScheduleStatus();
        // 更新实体
        BeanUtils.copyProperties(scheduleDTO, taskSchedule);
        taskSchedule.setId(id);
        taskSchedule.setScheduleStatus(oldStatus);
        // 更新数据库
        int update = taskScheduleMapper.updateById(taskSchedule);

        if (Objects.equals(oldStatus, ScheduleStatusEnum.ENABLE.getCode()) &&
                taskSchedule.getScheduleInstanceName() != null) {
            Frequency frequency = Frequency.getFrequency(taskSchedule.getScheduleFrequency());
            Date startTime = taskSchedule.getStartTime() == null ? null : DateUtil.date(taskSchedule.getStartTime());
            Date endTime = taskSchedule.getEndTime() == null ? null : DateUtil.date(taskSchedule.getEndTime());
            String cron = getCronByFrequency(frequency, taskSchedule.getCronExpression(), startTime);

            AbstractSchedulerProvider provider = schedulerProviderRegistry.getProvider(ScheduledType.CRON);
            provider.rescheduleJob(SchedulerCreateParam.builder()
                    .name(taskSchedule.getScheduleInstanceName())
                    .cron(cron)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build());
        }
        return update;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteSchedule(Integer id) {
        TaskSchedule taskSchedule = taskScheduleMapper.selectById(id);
        if (taskSchedule == null) {
            return 0;
        }
        if (Objects.equals(taskSchedule.getScheduleStatus(), ScheduleStatusEnum.ENABLE.getCode())) {
            throw new BizException("调度处于启用状态，不可删除！");
        }
        return taskScheduleMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateScheduleStatus(Integer id, Integer status) {
        ScheduleStatusEnum.getScheduleStatusEnum(status);
        TaskSchedule taskSchedule = taskScheduleMapper.selectById(id);
        if (taskSchedule == null || Objects.equals(taskSchedule.getScheduleStatus(), status)) {
            return 0;
        }
        TaskInfo taskInfo = taskInfoMapper.selectById(taskSchedule.getTaskId());
        int update = taskScheduleMapper.update(Wrappers.<TaskSchedule>lambdaUpdate()
                .set(TaskSchedule::getScheduleStatus, status)
                .eq(TaskSchedule::getId, id));

        AbstractSchedulerProvider provider = schedulerProviderRegistry.getProvider(ScheduledType.CRON);
        if (status == 1) {
            // 启用任务
            TaskInstance taskInstance = taskInstanceMapper.selectOne(Wrappers.<TaskInstance>lambdaQuery()
                    .eq(TaskInstance::getScheduleId, id));
            if (taskInstance != null) {
                log.error("存在未结束的调度实例！ schedule id: {} ", id);
                throw new BizException("存在未结束的调度实例！");
            }
            Frequency frequency = Frequency.getFrequency(taskSchedule.getScheduleFrequency());
            Date startTime = taskSchedule.getStartTime() == null ? null : DateUtil.date(taskSchedule.getStartTime());
            Date endTime = taskSchedule.getEndTime() == null ? null : DateUtil.date(taskSchedule.getEndTime());
            String cron = getCronByFrequency(frequency, taskSchedule.getCronExpression(), startTime);
            String jobName = UUID.fastUUID().toString();
            provider.createScheduler(SchedulerCreateParam.builder()
                    .name(jobName)
                    .cron(cron)
                    .startTime(startTime)
                    .endTime(endTime)
                    .jobClass(JobBeanClassEnum.getJobClass(taskInfo.getEngineType()))
                    .build());

            log.info("调度启用提交 调度名称: {}", jobName);
            int updated = taskScheduleMapper.update(Wrappers.<TaskSchedule>lambdaUpdate()
                    .set(TaskSchedule::getScheduleInstanceName, jobName)
                    .eq(TaskSchedule::getId, id));
            if (updated != 1) {
                throw new BizException("调度记录保存失败！");
            }
            // todo 保存失败后任务的回滚
        } else {
            // 禁用任务
            if (provider.deleteScheduler(taskSchedule.getScheduleInstanceName())) {
                int updated = taskScheduleMapper.update(Wrappers.<TaskSchedule>lambdaUpdate()
                        .set(TaskSchedule::getScheduleInstanceName, null)
                        .eq(TaskSchedule::getId, id));
                if (updated != 1) {
                    log.error("调度实例停止异常! instance:{}", taskSchedule.getScheduleInstanceName());
                    throw new BizException("调度记录保存失败！");
                }
            }

            // 禁用时清理待执行的重试任务
            if (taskSchedule.getMaxRetryTimes() != null && taskSchedule.getMaxRetryTimes() > 0) {
                List<TaskInstance> pendingRetryInstances = taskInstanceMapper.selectList(
                        Wrappers.<TaskInstance>lambdaQuery()
                                .eq(TaskInstance::getScheduleId, id)
                                .eq(TaskInstance::getRetryScheduled, true));
                if (!pendingRetryInstances.isEmpty()) {
                    AbstractSchedulerProvider immediateProvider = schedulerProviderRegistry.getProvider(ScheduledType.IMMEDIATE);
                    for (TaskInstance pi : pendingRetryInstances) {
                        int rc = pi.getRetryCount() != null ? pi.getRetryCount() : 0;
                        String jobName = "retry-" + pi.getId() + "-" + (rc + 1);
                        immediateProvider.deleteScheduler(jobName);
                        log.info("Cleaned pending retry job {} for disabled schedule {}", jobName, id);
                    }
                }
                taskInstanceMapper.update(null,
                        Wrappers.<TaskInstance>lambdaUpdate()
                                .set(TaskInstance::getRetryScheduled, false)
                                .eq(TaskInstance::getScheduleId, id));
            }
        }
        return update;
    }


    private String getCronByFrequency(Frequency frequency, String defaultCron, Date time) {
        String cron;
        switch (frequency) {
            case CUSTOM:
                cron = defaultCron;
                break;
            case ONCE:
                cron = CronUtil.getCronExpression(time);
                break;
            default:
                cron = Frequency.getCronExpress(frequency);
                break;
        }
        return cron;
    }

    @Override
    public IPage<ScheduleVO> pageSchedule(ScheduleQueryDTO scheduleQueryDTO) {
        Page<TaskSchedule> page = new Page<>(scheduleQueryDTO.getCurrent(), scheduleQueryDTO.getSize());
        IPage<TaskSchedule> taskSchedulePage = taskScheduleMapper.page(page, scheduleQueryDTO);
        return taskSchedulePage.convert(e -> {
            ScheduleVO scheduleVO = new ScheduleVO();
            BeanUtils.copyProperties(e, scheduleVO);
            return scheduleVO;
        });
    }
}