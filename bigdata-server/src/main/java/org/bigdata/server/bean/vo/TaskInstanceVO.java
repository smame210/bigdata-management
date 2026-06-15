package org.bigdata.server.bean.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskInstanceVO {
    private Integer id;

    private String taskName;

    private String engineType;

    private String clusterName;

    private String taskMode;

    private String clusterType;

    private Integer taskStatus;

    private String scheduleName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String taskInstanceId;

    private String clusterMetadata;

    private Integer retryCount;
}
