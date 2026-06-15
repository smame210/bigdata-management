package org.bigdata.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.alert.Sender;
import org.bigdata.alert.SenderRequest;
import org.bigdata.alert.registry.SenderRegistry;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.server.alert.condition.ConditionMatcher;
import org.bigdata.server.alert.request.SenderRequestBuilder;
import org.bigdata.server.alert.template.TemplateRenderer;
import org.bigdata.server.bean.entity.AlertInfo;
import org.bigdata.server.bean.entity.AlertPolicy;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.mapper.AlertInfoMapper;
import org.bigdata.server.mapper.AlertPolicyMapper;
import org.bigdata.server.mapper.TaskInfoMapper;
import org.bigdata.server.service.IAlertTriggerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertTriggerServiceImpl implements IAlertTriggerService {

    private final AlertPolicyMapper alertPolicyMapper;

    private final AlertInfoMapper alertInfoMapper;

    private final TaskInfoMapper taskInfoMapper;

    private final SenderRegistry senderRegistry;

    private final ConditionMatcher conditionMatcher;

    private final TemplateRenderer templateRenderer;

    private final List<SenderRequestBuilder> requestBuilders;

    @Override
    public void onTaskStatusChange(TaskInstance taskInstance, JobStatusEnum newStatus) {
        if (newStatus == null || taskInstance == null) {
            return;
        }

        List<AlertPolicy> policies = alertPolicyMapper.selectList(
                Wrappers.<AlertPolicy>lambdaQuery()
                        .eq(AlertPolicy::getTaskId, taskInstance.getTaskId())
                        .eq(AlertPolicy::getStatus, 1)
        );

        if (policies.isEmpty()) {
            return;
        }

        for (AlertPolicy policy : policies) {
            try {
                if (conditionMatcher.matches(policy, newStatus)) {
                    sendAlert(policy, taskInstance, newStatus, null);
                }
            } catch (Exception e) {
                log.error("Failed to trigger alert for policy: {}", policy.getId(), e);
            }
        }
    }

    @Override
    public void onTaskTimeout(TaskInstance taskInstance) {
        if (taskInstance == null) {
            return;
        }

        List<AlertPolicy> policies = alertPolicyMapper.selectList(
                Wrappers.<AlertPolicy>lambdaQuery()
                        .eq(AlertPolicy::getTaskId, taskInstance.getTaskId())
                        .eq(AlertPolicy::getStatus, 1)
        );

        if (policies.isEmpty()) {
            return;
        }

        for (AlertPolicy policy : policies) {
            try {
                if (conditionMatcher.matchesForTimeout(policy)) {
                    sendAlert(policy, taskInstance, JobStatusEnum.UNKNOWN, "任务运行超时");
                }
            } catch (Exception e) {
                log.error("Failed to trigger timeout alert for policy: {}", policy.getId(), e);
            }
        }
    }

    private void sendAlert(AlertPolicy policy, TaskInstance taskInstance, JobStatusEnum status, String extraMsg) {
        AlertInfo alertInfo = alertInfoMapper.selectById(policy.getAlertId());
        if (alertInfo == null || alertInfo.getStatus() != 1) {
            log.warn("Alert info not found or disabled, alertId: {}", policy.getAlertId());
            return;
        }

        TaskInfo taskInfo = taskInfoMapper.selectById(taskInstance.getTaskId());
        String taskName = taskInfo != null ? taskInfo.getTaskName() : "未知任务";

        String statusName = status != null ? status.name() : "未知";
        String content = templateRenderer.render(alertInfo, taskName, taskInstance, statusName, extraMsg);

        SenderRequestBuilder builder = resolveBuilder(alertInfo.getType());
        if (builder == null) {
            log.error("SenderRequestBuilder not found for type: {}", alertInfo.getType());
            return;
        }
        SenderRequest request = builder.build(alertInfo, content);

        Sender sender = senderRegistry.getSender(alertInfo.getType());
        if (sender == null) {
            log.error("Sender not found for type: {}", alertInfo.getType());
            return;
        }

        sender.send(request);
        log.info("Alert sent for task: {}, status: {}, policy: {}", taskName, status, policy.getId());
    }

    private SenderRequestBuilder resolveBuilder(String type) {
        for (SenderRequestBuilder builder : requestBuilders) {
            if (builder.getType().equals(type)) {
                return builder;
            }
        }
        return null;
    }
}
