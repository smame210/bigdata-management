package org.bigdata.server.bean.dto.alert;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.bigdata.server.bean.entity.AlertInfo;
import org.bigdata.server.exception.BizException;
import org.springframework.beans.BeanUtils;

@Data
public class DingDingAlertInfoDTO extends AbstractAlertInfoDTO {

    private DingDingAlertConfig metadata;

    @Override
    public AlertInfo toEntity() {
        if (!isValid()) {
            throw new BizException("参数校验异常！");
        }
        AlertInfo alterInfo = new AlertInfo();
        BeanUtils.copyProperties(this, alterInfo);
        alterInfo.setMetadata(JSON.toJSONString(metadata));
        return alterInfo;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
