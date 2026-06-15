package org.bigdata.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bigdata.server.bean.dto.cluster.AbstractClusterInfoDTO;
import org.bigdata.server.bean.dto.cluster.StandaloneClusterInfoDTO;
import org.bigdata.server.bean.dto.cluster.YarnClusterInfoDTO;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ClusterTypeEnum {

    YARN(YarnClusterInfoDTO.class),
    STANDALONE(StandaloneClusterInfoDTO.class),
    ;

    private Class<? extends AbstractClusterInfoDTO> clazz;

    public static Map<String, Class<? extends AbstractClusterInfoDTO>> supportedClusterTypes() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(Enum::name, ClusterTypeEnum::getClazz));
    }
}
