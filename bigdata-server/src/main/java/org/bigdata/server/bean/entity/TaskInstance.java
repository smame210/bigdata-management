package org.bigdata.server.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务实例记录表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("task_instance")
public class TaskInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 任务ID，关联task_info表
     */
    private Integer taskId;

    /**
     * 任务实例id
     */
    private String taskInstanceId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 引擎类型
     */
    @TableField(exist = false)
    private String engineType;

    /**
     * 集群名称（来自关联查询）
     */
    @TableField(exist = false)
    private String clusterName;

    /**
     * 任务类型，如SESSION/APPLICATION/PRE_JOB
     */
    private String taskType;

    /**
     * 任务运行模式，如STREAMING/BATCH
     */
    private String taskMode;

    /**
     * 任务状态：0-未启动(CREATED)，1-已接收(ACCEPTED)，2-运行中(RUNNING)，3-已完成(FINISHED)，4-失败(FAILED)，5-已终止(KILLED)，6-未知(UNKNOWN)
     */
    private Integer taskStatus;

    /**
     * 元数据，包括任务配置等
     */
    private String taskMetadata;

    /**
     * 关联的集群ID
     */
    private Integer clusterId;

    /**
     * 集群类型，如YARN/K8S等
     */
    private String clusterType;

    /**
     * 集群元数据，JSON格式存储集群配置信息
     */
    private String clusterMetadata;

    /**
     * 调度id
     */
    private Integer scheduleId;

    /**
     * 调度名称
     */
    private String scheduleName;

    /**
     * 调度实例名称
     */
    private String scheduleInstanceName;

    /**
     * 任务启动时间
     */
    private LocalDateTime launchTime;

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
     * 重试次数（第几次执行，首次为 0）
     */
    private Integer retryCount;

    /**
     * 重试链路的根实例 id
     */
    private Integer rootInstanceId;

    /**
     * 上一次失败的父实例 id
     */
    private Integer parentInstanceId;

    /**
     * 是否已为本次失败创建过 retry job（防止重复调度）
     */
    private Boolean retryScheduled;

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDeleted;
}
