package org.bigdata.server.bean.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 告警策略表
 * @TableName alert_policy
 */
@TableName(value ="alert_policy")
@Data
public class AlertPolicy {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
    @TableField(exist = false)
    private String alertName;

    /**
     * 任务表id
     */
    private Integer taskId;

    /**
     * 告警任务名称
     */
    @TableField(exist = false)
    private String taskName;

    /**
     * 告警条件
     */
    private String conditions;

    /**
     * 状态 0-停用 1-启用
     */
    private Integer status;

    /**
     * 记录创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDeleted;
}