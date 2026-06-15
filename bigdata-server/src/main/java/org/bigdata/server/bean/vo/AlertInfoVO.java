package org.bigdata.server.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertInfoVO {
    /**
     * id
     */
    private Integer id;

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

    /**
     * 元数据
     */
    private String metadata;

    /**
     * 告警模版
     */
    private String template;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
