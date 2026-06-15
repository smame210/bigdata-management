package org.bigdata.scheduler.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduledType {
    CRON,
    IMMEDIATE,
    ;
}
