package org.bigdata.alert.template;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class TemplateProvider {

    private static Map<String, AbstractTemplate> templatesMap = new HashMap<>();

    static {
        ServiceLoader<AbstractTemplate> templates = ServiceLoader.load(AbstractTemplate.class);
        for (AbstractTemplate template : templates) {
            templatesMap.put(template.getAlertTarget().english, template);
        }
    }

    public static Map<String, String> getTemplateParams(String type) {
        AbstractTemplate abstractTemplate = templatesMap.get(type);
        if (abstractTemplate != null) {
            return abstractTemplate.getParams().getParams();
        }
        throw new IllegalArgumentException("No template found for type: " + type);
    }

    public static String format(String type, String template, Map<String, Object> params) {
        AbstractTemplate abstractTemplate = templatesMap.get(type);
        if (abstractTemplate != null) {
            return abstractTemplate.format(template, params);
        }
        throw new IllegalArgumentException("No template found for type: " + type);
    }
}
