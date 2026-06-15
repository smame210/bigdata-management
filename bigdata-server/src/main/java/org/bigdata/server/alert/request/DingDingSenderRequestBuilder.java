package org.bigdata.server.alert.request;

import com.alibaba.fastjson2.JSON;
import org.bigdata.alert.ding.DingDingContent;
import org.bigdata.alert.ding.DingDingReq;
import org.bigdata.alert.enums.AlertTargetEnum;
import org.bigdata.server.bean.dto.alert.DingDingAlertConfig;
import org.bigdata.server.bean.entity.AlertInfo;
import org.springframework.stereotype.Component;

@Component
public class DingDingSenderRequestBuilder implements SenderRequestBuilder {

    @Override
    public String getType() {
        return AlertTargetEnum.DING_DING.english;
    }

    @Override
    public DingDingReq build(AlertInfo alertInfo, String content) {
        DingDingAlertConfig config = JSON.parseObject(alertInfo.getMetadata(), DingDingAlertConfig.class);
        DingDingContent dingContent = new DingDingContent();
        DingDingContent.Markdown markdown = dingContent.new Markdown();
        markdown.setTitle("大数据平台任务告警");
        markdown.setText(content);
        dingContent.setMsgtype("markdown");
        dingContent.setMarkdown(markdown);

        return DingDingReq.builder()
                .webhook(config.getWebhook())
                .secret(config.getSecret())
                .content(dingContent)
                .atAll(config.getAtAll())
                .atMobiles(config.getAtMobiles())
                .build();
    }
}
