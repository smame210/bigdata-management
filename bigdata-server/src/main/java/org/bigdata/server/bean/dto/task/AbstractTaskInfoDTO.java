package org.bigdata.server.bean.dto.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.VerificationProperties;
import org.bigdata.server.bean.dto.AbstractTypeIdResolver;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.enums.EngineTypeEnum;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "engineType", visible = true)
@JsonTypeIdResolver(AbstractTaskInfoDTO.EngineTypeIdResolver.class)
public abstract class AbstractTaskInfoDTO implements VerificationProperties {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不可为空")
    private String taskName;

    /**
     * 引擎类型
     */
    @NotBlank(message = "引擎类型不可为空")
    private String engineType;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 任务运行模式
     */
    @NotBlank(message = "任务模式不可为空")
    private String taskMode;

    /**
     * 集群ID
     */
    private Integer clusterId;

    @Slf4j
    @RequiredArgsConstructor
    public static final class EngineTypeIdResolver extends AbstractTypeIdResolver<String> {

        @PostConstruct
        public void bindDataSource() {
            bindDefault(UnknowTaskInfoDTO.class);

            EngineTypeEnum.supportedEngineTypes()
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

    public abstract TaskInfo toEntity();
}
