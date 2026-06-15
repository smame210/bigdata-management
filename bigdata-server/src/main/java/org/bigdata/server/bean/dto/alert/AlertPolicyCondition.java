package org.bigdata.server.bean.dto.alert;

import lombok.Data;

import jakarta.validation.constraints.Pattern;

@Data
public class AlertPolicyCondition {
    private String key;

    @Pattern(regexp = "=|!=")
    private String operator;

    private String value;
}
