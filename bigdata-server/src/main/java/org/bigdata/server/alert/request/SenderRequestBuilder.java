package org.bigdata.server.alert.request;

import org.bigdata.alert.SenderRequest;
import org.bigdata.server.bean.entity.AlertInfo;

public interface SenderRequestBuilder {
    String getType();
    SenderRequest build(AlertInfo alertInfo, String content);
}
