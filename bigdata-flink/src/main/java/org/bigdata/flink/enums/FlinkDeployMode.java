package org.bigdata.flink.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlinkDeployMode {
    SESSION,
    PRE_JOB,
    APPLICATION,
    ;
}
