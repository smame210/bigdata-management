package org.bigdata.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClusterHealthStatusEnum {
    UNKNOWN(0),
    HEALTHY(1),
    DEGRADED(2),
    UNHEALTHY(3),
    ;

    private final Integer code;
}
