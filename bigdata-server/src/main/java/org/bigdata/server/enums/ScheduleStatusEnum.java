package org.bigdata.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bigdata.server.exception.BizException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ScheduleStatusEnum {
    DISABLE(0),
    ENABLE(1),
    ;

    private final Integer code;

    public static ScheduleStatusEnum getScheduleStatusEnum(Integer code) {
       return Arrays.stream(ScheduleStatusEnum.values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BizException("状态值无效"));
    }
}
