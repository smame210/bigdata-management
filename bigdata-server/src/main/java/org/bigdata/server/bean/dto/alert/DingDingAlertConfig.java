package org.bigdata.server.bean.dto.alert;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
public class DingDingAlertConfig {
    @NotBlank(message = "webhook地址不可为空")
    private String webhook;

    private String secret;

    private String dingType = "group_robot";

    private List<String> atMobiles;

    private Boolean atAll = false;

    private String template;

    private Map<String, String> params;
}
