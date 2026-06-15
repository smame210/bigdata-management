package org.bigdata.alert.ding;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.google.auto.service.AutoService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.alert.Sender;
import org.bigdata.alert.SenderRequest;
import org.bigdata.alert.enums.AlertTargetEnum;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
@AutoService(Sender.class)
public class DingDingSender implements Sender {

    @Override
    public String getSenderName() {
        return AlertTargetEnum.DING_DING.english;
    }

    @Override
    public void send(SenderRequest request) {
        DingDingReq dingDingReq = (DingDingReq) request;
        String webhook = dingDingReq.getWebhook();
        String secret = dingDingReq.getSecret();

        try {
            String token = extractToken(webhook);
            String baseUrl = webhook.contains("?") ? webhook.substring(0, webhook.indexOf("?")) : webhook;

            StringBuilder urlBuilder = new StringBuilder(baseUrl);
            if (secret != null && !secret.isBlank()) {
                Long timestamp = System.currentTimeMillis();
                String sign = getSign(timestamp, secret);
                urlBuilder.append("?sign=").append(sign).append("&timestamp=").append(timestamp);
            }

            DingTalkClient client = new DefaultDingTalkClient(urlBuilder.toString());

            OapiRobotSendRequest req = new OapiRobotSendRequest();
            DingDingContent content = dingDingReq.getContent();
            if ("markdown".equals(content.getMsgtype()) && content.getMarkdown() != null) {
                req.setMsgtype("markdown");
                OapiRobotSendRequest.Markdown md = new OapiRobotSendRequest.Markdown();
                md.setTitle(content.getMarkdown().getTitle());
                md.setText(content.getMarkdown().getText());
                req.setMarkdown(md);
            }

            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setIsAtAll(Boolean.TRUE.equals(dingDingReq.getAtAll()));
            List<String> atMobiles = dingDingReq.getAtMobiles();
            if (atMobiles != null && !atMobiles.isEmpty()) {
                at.setAtMobiles(atMobiles);
            }
            req.setAt(at);

            OapiRobotSendResponse rsp = client.execute(req, token);
            if (!rsp.isSuccess()) {
                log.error("DingDing send failed, errcode: {}, errmsg: {}", rsp.getErrcode(), rsp.getErrmsg());
            }
        } catch (ApiException e) {
            log.error("DingDing send error, errcode: {}", e.getErrCode(), e);
        } catch (Exception e) {
            log.error("DingDing send unexpected error", e);
        }
    }

    private String getSign(Long timestamp, String secret) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(Base64.getEncoder().encodeToString(signData), StandardCharsets.UTF_8);
    }

    private String extractToken(String webhook) {
        int idx = webhook.indexOf("access_token=");
        if (idx == -1) return "";
        String token = webhook.substring(idx + "access_token=".length());
        int ampIdx = token.indexOf('&');
        return ampIdx > 0 ? token.substring(0, ampIdx) : token;
    }
}
