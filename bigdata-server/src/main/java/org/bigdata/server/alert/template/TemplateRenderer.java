package org.bigdata.server.alert.template;

import org.bigdata.alert.template.TemplateProvider;
import org.bigdata.server.bean.entity.AlertInfo;
import org.bigdata.server.bean.entity.TaskInstance;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateRenderer {

    private static final String DEFAULT_CONTENT = """
            ### 大数据平台任务告警

            - **任务名称**: %s
            - **实例ID**: %s
            - **当前状态**: %s
            - **告警时间**: %s
            """;

    public String render(AlertInfo alertInfo, String taskName, TaskInstance instance, String statusName, String extraMsg) {
        String template = alertInfo.getTemplate();
        if (template == null || template.isBlank()) {
            return String.format(DEFAULT_CONTENT,
                    taskName,
                    instance.getTaskInstanceId(),
                    statusName,
                    LocalDateTime.now().toString().replace("T", " "));
        }

        Map<String, Object> params = Map.of(
                "TaskName", taskName,
                "TaskStatus", statusName,
                "AlterTime", LocalDateTime.now().toString().replace("T", " ")
        );

        return TemplateProvider.format(alertInfo.getType(), template, params);
    }
}
