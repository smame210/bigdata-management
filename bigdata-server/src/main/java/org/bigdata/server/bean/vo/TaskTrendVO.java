package org.bigdata.server.bean.vo;

import lombok.Data;

/**
 * 任务趋势（按日聚合）
 */
@Data
public class TaskTrendVO {
    private String date;
    private long total;
    private long finished;
    private long failed;
}
