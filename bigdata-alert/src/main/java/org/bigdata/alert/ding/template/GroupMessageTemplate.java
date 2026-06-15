package org.bigdata.alert.ding.template;

import com.google.auto.service.AutoService;
import org.bigdata.alert.enums.AlertTargetEnum;
import org.bigdata.alert.template.AbstractTemplate;
import org.bigdata.alert.template.TemplateParams;

@AutoService(AbstractTemplate.class)
public class GroupMessageTemplate extends AbstractTemplate {

    private static final TemplateParams params = new TemplateParams();

    @Override
    protected AlertTargetEnum getAlertTarget() {
        return AlertTargetEnum.DING_DING;
    }

    @Override
    public TemplateParams getParams() {
        return params;
    }
}
