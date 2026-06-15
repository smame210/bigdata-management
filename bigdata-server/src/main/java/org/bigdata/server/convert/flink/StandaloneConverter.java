package org.bigdata.server.convert.flink;

import com.alibaba.fastjson2.JSON;
import com.google.auto.service.AutoService;
import org.bigdata.api.enums.ClusterTypeEnum;
import org.bigdata.api.enums.EngineTypeEnum;
import org.bigdata.flink.bean.ApplicationJobParam;
import org.bigdata.flink.enums.FlinkDeployMode;
import org.bigdata.server.bean.dto.cluster.StandaloneClusterInfoDTO;
import org.bigdata.server.bean.dto.task.FlinkTaskConfigDTO;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.convert.JobParamConverter;

@AutoService(JobParamConverter.class)
public class StandaloneConverter implements JobParamConverter<ApplicationJobParam> {

    @Override
    public ApplicationJobParam convert(TaskInstance taskInstance) {
        StandaloneClusterInfoDTO clusterInfoDTO = JSON.parseObject(taskInstance.getClusterMetadata(), StandaloneClusterInfoDTO.class);
        FlinkTaskConfigDTO flinkTaskConfigDTO = JSON.parseObject(taskInstance.getTaskMetadata(), FlinkTaskConfigDTO.class);

        return ApplicationJobParam.builder()
                .jobName(taskInstance.getTaskName())
                .mainClass(flinkTaskConfigDTO.getMainClass())
                .args(flinkTaskConfigDTO.getArgs())
                .jarPath(flinkTaskConfigDTO.getJarPath())
                .parallelism(flinkTaskConfigDTO.getParallelism())
                .clusterUrl(clusterInfoDTO.getJobManagerUrl())
                .build();
    }

    @Override
    public String getClusterType() {
        return ClusterTypeEnum.STANDALONE.name();
    }

    @Override
    public String getEngineType() {
        return EngineTypeEnum.FLINK.name();
    }

    @Override
    public String getJobMode() {
        return FlinkDeployMode.SESSION.name();
    }
}
