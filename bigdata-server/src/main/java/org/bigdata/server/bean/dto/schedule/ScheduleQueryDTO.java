package org.bigdata.server.bean.dto.schedule;

import lombok.Data;
import org.bigdata.server.bean.PageQueryDTO;

@Data
public class ScheduleQueryDTO extends PageQueryDTO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 引擎类型
     */
    private String engineType;

    /**
     * 集群名称
     */
    private String clusterName;
    
    /**
     * 调度名称
     */
    private String scheduleName;
    
    /**
     * 调度状态：0-禁用 1-启用
     */
    private Integer scheduleStatus;

}