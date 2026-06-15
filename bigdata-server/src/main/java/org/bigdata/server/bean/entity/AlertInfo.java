package org.bigdata.server.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警配置表
 * @TableName alter_info
 */
@TableName(value ="alert_info")
@Data
public class AlertInfo {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 告警名称
     */
    private String name;

    /**
     * 告警类型 sms、dingding等
     */
    private String type;

    /**
     * 告警状态 0-停用 1-启用
     */
    private Integer status;

    /**
     * 告警配置的元数据
     */
    private String metadata;

    /**
     * 告警模版
     */
    private String template;

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
    private Integer isDeleted;
}