package org.bigdata.server.bean.dto.alert;

import lombok.Data;
import org.bigdata.server.bean.PageQueryDTO;

@Data
public class AlertPolicyQueryDTO extends PageQueryDTO {

    /**
     * 告警实例名称
     */
    private String alertName;

    /**
     * 告警策略名称
     */
    private String name;

    /**
     * 告警任务名称
     */
    private String taskName;

    /**
     * 状态 0-停用 1-启用
     */
    private Integer status;
}
