package org.bigdata.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClusterStatusEnum {
    DISABLE(0),
    ENABLE(1),
    ;

    private final Integer code;
}
