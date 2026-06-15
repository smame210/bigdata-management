package org.bigdata.server.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bigdata.scheduler.core.ScheduleJob;
import org.bigdata.server.exception.BizException;
import org.bigdata.server.job.flink.FlinkExecuteJob;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum JobBeanClassEnum {
    FLINK_ON_YARN("flink", FlinkExecuteJob.class),
    ;

    private final String engineType;

    private final Class<? extends ScheduleJob> clazz;

    public static Class<? extends ScheduleJob> getJobClass(String engineType) {
        return Arrays.stream(values())
                .filter(e ->
                        e.engineType.equalsIgnoreCase(engineType) )
                .findFirst()
                .map(JobBeanClassEnum::getClazz)
                .orElseThrow(() -> new BizException("任务调度失败，无法找到任务调度的实现！"));
    }
}
