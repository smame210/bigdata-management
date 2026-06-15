package org.bigdata.server.bean.vo;

import lombok.Data;
import org.bigdata.server.bean.dto.alert.AlertPolicyCondition;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class AlertPolicyVO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 告警策略名称
     */
    private String name;

    /**
     * 告警表id
     */
    private Integer alertId;

    /**
     * 告警实例名称
     */
    private String alertName;

    /**
     * 任务表id
     */
    private Integer taskId;

    /**
     * 告警任务名称
     */
    private String taskName;

    /**
     * 告警条件
     */
    private List<AlertPolicyCondition> conditions;

    /**
     * 状态 0-停用 1-启用
     */
    private Integer status;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    private LocalDateTime updateTime;
}