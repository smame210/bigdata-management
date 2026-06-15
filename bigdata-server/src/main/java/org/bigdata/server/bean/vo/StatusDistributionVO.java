package org.bigdata.server.bean.vo;

import lombok.Data;

/**
 * 任务状态分布
 */
@Data
public class StatusDistributionVO {
    private int status;
    private String statusName;
    private long count;
}
