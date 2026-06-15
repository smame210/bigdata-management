package org.bigdata.server.bean.dto.alert;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.VerificationProperties;
import org.bigdata.server.bean.dto.AbstractTypeIdResolver;
import org.bigdata.server.bean.entity.AlertInfo;
import org.bigdata.server.enums.AlertTypeEnum;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;

@Data
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonTypeIdResolver(AbstractAlertInfoDTO.AlterTypeIdResolver.class)
public abstract class AbstractAlertInfoDTO implements VerificationProperties {

    protected Integer id;

    /**
     * 集群名称
     */
    @NotBlank(message = "名称不可为空")
    protected String name;

    /**
     * 集群类型
     */
    @NotBlank(message = "类型不可为空")
    protected String type;

    /**
     * 模版
     */
    @NotBlank(message = "模版内容不可为空")
    private String template;


    @Slf4j
    @RequiredArgsConstructor
    public static final class AlterTypeIdResolver extends AbstractTypeIdResolver<String> {

        @PostConstruct
        public void bindAlterType() {
            AlertTypeEnum.supportedAlterTypes()
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

    public abstract AlertInfo toEntity();
}
