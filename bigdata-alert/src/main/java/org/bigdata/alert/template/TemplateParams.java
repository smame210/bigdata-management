package org.bigdata.alert.template;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TemplateParams {

    @Getter
    private final Map<String, String> params = new HashMap<>();

    public TemplateParams() {
        // 在构造函数中初始化默认参数
        params.put("TaskName", "任务名称");
        params.put("TaskStatus", "任务状态");
        params.put("AlterTime", "告警时间");
    }


    public Map<String, String> addParams(Map<String, String> newParams) {
        this.params.putAll(newParams);
        return params;
    }
}
