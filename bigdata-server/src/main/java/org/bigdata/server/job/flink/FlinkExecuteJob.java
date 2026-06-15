package org.bigdata.server.job.flink;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.scheduler.core.SchedulerJobBean;
import org.bigdata.server.bean.entity.TaskSchedule;
import org.bigdata.server.mapper.TaskScheduleMapper;
import org.bigdata.server.service.ITaskExecutionService;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlinkExecuteJob extends SchedulerJobBean {

    private final TaskScheduleMapper taskScheduleMapper;

    private final ITaskExecutionService taskExecutionService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        String name = jobDetail.getKey().getName();
        TaskSchedule taskSchedule = taskScheduleMapper.selectOne(Wrappers.<TaskSchedule>lambdaQuery()
                .eq(TaskSchedule::getScheduleInstanceName, name));
        if (taskSchedule == null) {
            log.error("schedule not found, instance name: {}", name);
            return;
        }
        try {
            taskExecutionService.submitTask(taskSchedule.getTaskId(), taskSchedule);
            log.info("quartz schedule flink job success, schedule: {}", name);
        } catch (Exception e) {
            log.error("quartz schedule flink job error, schedule: {}", name, e);
        }
    }

}
