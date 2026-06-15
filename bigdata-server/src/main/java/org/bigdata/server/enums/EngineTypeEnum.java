package org.bigdata.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bigdata.server.bean.dto.task.AbstractTaskInfoDTO;
import org.bigdata.server.bean.dto.task.FlinkTaskInfoDTO;
import org.bigdata.server.bean.dto.task.SparkTaskInfoDTO;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum EngineTypeEnum {
    FLINK(FlinkTaskInfoDTO.class),
    SPARK(SparkTaskInfoDTO.class),
    ;

    private final Class<? extends AbstractTaskInfoDTO> clazz;

    public static Map<String, Class<? extends AbstractTaskInfoDTO>> supportedEngineTypes() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(Enum::name, EngineTypeEnum::getClazz));
    }

}
