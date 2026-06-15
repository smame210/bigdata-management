package org.bigdata.scheduler.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bigdata.scheduler.core.ScheduleJob;
import org.bigdata.scheduler.enums.ScheduledType;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchedulerCreateParam {
    private String name;

    private ScheduledType type;

    private String cron;

    private Date startTime;

    private Date endTime;

    private Class<? extends ScheduleJob> jobClass;

    private Map<String, Object> data;
}
