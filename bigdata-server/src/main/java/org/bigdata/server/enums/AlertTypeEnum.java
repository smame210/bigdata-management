package org.bigdata.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bigdata.alert.enums.AlertTargetEnum;
import org.bigdata.server.bean.dto.alert.AbstractAlertInfoDTO;
import org.bigdata.server.bean.dto.alert.DingDingAlertInfoDTO;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum AlertTypeEnum {

    DING_DING(AlertTargetEnum.DING_DING, DingDingAlertInfoDTO.class),
    ;

    private final AlertTargetEnum target;

    private final Class<? extends AbstractAlertInfoDTO> clazz;

    public static Map<String, Class<? extends AbstractAlertInfoDTO>> supportedAlterTypes() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(e -> e.getTarget().english, AlertTypeEnum::getClazz));
    }
}
