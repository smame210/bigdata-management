package org.bigdata.alert.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertTargetEnum {
    DING_DING("钉钉", "dingding"),
    ;

    public final String chinese;

    public final String english;
}
