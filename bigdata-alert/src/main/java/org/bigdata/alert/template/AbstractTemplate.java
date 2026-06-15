package org.bigdata.alert.template;

import org.bigdata.alert.enums.AlertTargetEnum;

import java.util.Map;

public abstract class AbstractTemplate {

    protected abstract AlertTargetEnum getAlertTarget();

    protected abstract TemplateParams getParams();

    /**
     * replace params keys of template  with the actual value
     *
     * @param template alert message template
     * @param params   param entry
     * @return alert message text
     */
    protected String format(String template, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            template = template.replace("[" + entry.getKey() + "]", entry.getValue().toString());
        }
        return template;
    }
}
