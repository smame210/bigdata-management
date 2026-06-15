package org.bigdata.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum FlinkTaskTypeEnum {

    SESSION("session"),
    PRE_JOB("pre-job"),
    APPLICATION("application");

    private final String code;

    public static List<String> supportedTaskTypes() {
        return Arrays.stream(values())
                .map(e -> {
                    return e.getCode().toLowerCase();
                })
                .collect(Collectors.toList());
    }



}
