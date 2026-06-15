package org.bigdata.server.bean.dto.cluster;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.*;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.AbstractTypeIdResolver;
import org.bigdata.server.bean.entity.ClusterInfo;
import org.bigdata.server.enums.ClusterTypeEnum;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;

@Data
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "clusterType", visible = true)
@JsonTypeIdResolver(AbstractClusterInfoDTO.ClusterTypeIdResolver.class)
public abstract class AbstractClusterInfoDTO {

    protected Integer id;

    /**
     * 集群名称
     */
    @NotBlank(message = "集群名称不可为空")
    protected String clusterName;

    /**
     * 集群类型
     */
    @NotBlank(message = "集群类型不可为空")
    protected String clusterType;

    /**
     * 状态
     */
    protected Integer clusterStatus;


    @Slf4j
    @RequiredArgsConstructor
    public static final class ClusterTypeIdResolver extends AbstractTypeIdResolver<String> {

        @PostConstruct
        public void bindDataSource() {
            bindDefault(UnknowClusterInfoDTO.class);

            ClusterTypeEnum.supportedClusterTypes()
                    .forEach((k, v) -> this.bind(k.toLowerCase(), v));
        }

        @Override
        protected String typeFromSubtype(Object obj) {
            return subTypes.inverse().get(obj.getClass());
        }

        @Override
        protected Class<?> subTypeFromType(String type) {
            Class<?> subType = subTypes.get(type.toLowerCase());
            return subType != null ? subType : defaultClass;
        }
    }

    public abstract ClusterInfo toEntity();
}
