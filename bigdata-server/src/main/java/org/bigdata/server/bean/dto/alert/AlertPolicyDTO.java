package org.bigdata.server.bean.dto.alert;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.bigdata.server.bean.entity.AlertPolicy;
import org.springframework.beans.BeanUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class AlertPolicyDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 告警名称
     */
    @NotBlank(message = "策略名称不可为空")
    private String name;

    /**
     * 告警表id
     */
    @NotNull(message = "告警实例不可为空")
    private Integer alertId;

    /**
     * 任务表id
     */
    @NotNull(message = "告警任务不可为空")
    private Integer taskId;

    /**
     * 告警条件
     */
    private List<AlertPolicyCondition> conditions;

    /**
     * 状态 0-停用 1-启用
     */
    private Integer status;

    public AlertPolicy toEntity() {
        AlertPolicy alertPolicy = new AlertPolicy();
        BeanUtils.copyProperties(this, alertPolicy);
        alertPolicy.setConditions(JSON.toJSONString(conditions));
        return alertPolicy;
    }
}
