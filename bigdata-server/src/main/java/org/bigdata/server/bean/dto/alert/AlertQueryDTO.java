package org.bigdata.server.bean.dto.alert;

import lombok.Data;

@Data
public class AlertQueryDTO {
    /**
     * 告警名称
     */
    private String name;

    /**
     * 告警类型
     */
    private String type;

    /**
     * 告警状态 0-停用 1-启用
     */
    private Integer status;
}
