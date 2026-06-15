package org.bigdata.server.convert.flink;

import com.alibaba.fastjson2.JSON;
import com.google.auto.service.AutoService;
import org.bigdata.api.enums.ClusterTypeEnum;
import org.bigdata.api.enums.EngineTypeEnum;
import org.bigdata.flink.bean.ApplicationJobParam;
import org.bigdata.flink.enums.FlinkDeployMode;
import org.bigdata.server.bean.dto.cluster.YarnClusterInfoDTO;
import org.bigdata.server.bean.dto.task.FlinkTaskConfigDTO;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.convert.JobParamConverter;

@AutoService(JobParamConverter.class)
public class YarnApplicationConverter implements JobParamConverter<ApplicationJobParam> {

    @Override
    public ApplicationJobParam convert(TaskInstance taskInstance) {
        YarnClusterInfoDTO yarnClusterInfoDTO = JSON.parseObject(taskInstance.getClusterMetadata(), YarnClusterInfoDTO.class);
        FlinkTaskConfigDTO flinkTaskConfigDTO = JSON.parseObject(taskInstance.getTaskMetadata(), FlinkTaskConfigDTO.class);
        String distPath = flinkTaskConfigDTO.getLibPath();
        if (!flinkTaskConfigDTO.getLibPath().endsWith("/")){
            distPath = distPath + "/";
        }
        distPath = distPath + String.format("flink-dist-%s.jar", flinkTaskConfigDTO.getVersion());

        return ApplicationJobParam.builder()
                .jobId(taskInstance.getTaskInstanceId())
                .jobName(taskInstance.getTaskName())
                .mainClass(flinkTaskConfigDTO.getMainClass())
                .args(flinkTaskConfigDTO.getArgs())
                .jarPath(flinkTaskConfigDTO.getJarPath())
                .libPath(flinkTaskConfigDTO.getLibPath())
                .distPath(distPath)
                .coreSitePath(yarnClusterInfoDTO.getCoreSitePath())
                .hdfsSitePath(yarnClusterInfoDTO.getHdfsSitePath())
                .yarnSitePath(yarnClusterInfoDTO.getYarnSitePath())
                .parallelism(flinkTaskConfigDTO.getParallelism())
                .jobManagerMemory(flinkTaskConfigDTO.getJobManagerMemory())
                .taskManagerMemory(flinkTaskConfigDTO.getTaskManagerMemory())
                .taskManagerSlots(flinkTaskConfigDTO.getTaskManagerSlots())
                .taskManagerMemoryManagedFraction(flinkTaskConfigDTO.getTaskManagerMemoryManagedFraction())
                .logConfPath(flinkTaskConfigDTO.getLogConfPath())
                .flinkConfPath(flinkTaskConfigDTO.getFlinkConfPath())
                .yarnResourceManagerUrl(yarnClusterInfoDTO.getYarnResourceManagerUrl())
                .build();
    }

    @Override
    public String getClusterType() {
        return ClusterTypeEnum.YARN.name();
    }

    @Override
    public String getEngineType() {
        return EngineTypeEnum.FLINK.name();
    }

    @Override
    public String getJobMode() {
        return FlinkDeployMode.APPLICATION.name();
    }
}
