package org.bigdata.server.alert.condition;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.server.bean.dto.alert.AlertPolicyCondition;
import org.bigdata.server.bean.entity.AlertPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConditionMatcher {

    public boolean matches(AlertPolicy policy, JobStatusEnum status) {
        if (policy.getConditions() == null || policy.getConditions().isEmpty()) {
            return false;
        }

        List<AlertPolicyCondition> conditions = JSON.parseObject(policy.getConditions(),
                new TypeReference<>() {});

        for (AlertPolicyCondition condition : conditions) {
            if ("taskStatus".equals(condition.getKey())) {
                String actual = String.valueOf(status.getCode());
                String expected = condition.getValue();
                String operator = condition.getOperator();

                if ("=".equals(operator)) {
                    return actual.equals(expected);
                } else if ("!=".equals(operator)) {
                    return !actual.equals(expected);
                }
            }
            if ("timeout".equals(condition.getKey())) {
                return true;
            }
        }

        return false;
    }

    public boolean matchesForTimeout(AlertPolicy policy) {
        if (policy.getConditions() == null || policy.getConditions().isEmpty()) {
            return false;
        }

        List<AlertPolicyCondition> conditions = JSON.parseObject(policy.getConditions(),
                new TypeReference<>() {});

        for (AlertPolicyCondition condition : conditions) {
            if ("timeout".equals(condition.getKey())) {
                return true;
            }
        }

        return false;
    }
}
