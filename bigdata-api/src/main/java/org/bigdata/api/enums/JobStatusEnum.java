package org.bigdata.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStatusEnum {
    CREATED(0),
    ACCEPTED(1),
    RUNNING(2),
    FINISHED(3),
    FAILED(4),
    KILLED(5),
    UNKNOWN(6),
    ;

    private final int code;

    public static JobStatusEnum getByCode(int code) {
        for (JobStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
