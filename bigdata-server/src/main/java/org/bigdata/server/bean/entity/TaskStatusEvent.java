package org.bigdata.server.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("task_status_event")
public class TaskStatusEvent {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer instanceId;

    private Integer taskId;

    private String eventType;

    private Integer oldStatus;

    private Integer newStatus;

    private String source;

    private String payload;

    private String eventStatus;

    private String errorMsg;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
