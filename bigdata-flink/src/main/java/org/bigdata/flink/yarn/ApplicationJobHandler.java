package org.bigdata.flink.yarn;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.util.FlinkException;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnConfigOptionsInternal;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.bigdata.api.enums.ClusterTypeEnum;
import org.bigdata.api.enums.EngineTypeEnum;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.api.exception.JobHandleException;
import org.bigdata.api.job.JobHandler;
import org.bigdata.flink.bean.ApplicationJobParam;
import org.bigdata.flink.enums.FlinkDeployMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.flink.configuration.MemorySize.MemoryUnit.MEGA_BYTES;

@Slf4j
@AutoService(JobHandler.class)
public class ApplicationJobHandler implements JobHandler<ApplicationJobParam> {

    @Override
    public String submit(ApplicationJobParam job) {
        log.info("提交Flink YARN Application任务: jobName={}, mainClass={}, jarPath={}",
                job.getJobName(), job.getMainClass(), job.getJarPath());
        log.info("任务资源配置: jobManagerMemory={}MB, taskManagerMemory={}MB, slots={}, parallelism={}",
                job.getJobManagerMemory(), job.getTaskManagerMemory(), job.getTaskManagerSlots(), job.getParallelism());
        // todo get HADOOP_USER_NAME config from param
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration flinkConfig = null;
        if (job.getFlinkConfPath() != null && !job.getFlinkConfPath().isBlank()) {
            java.io.File srcFile = new java.io.File(job.getFlinkConfPath());
            if (srcFile.exists()) {
                java.nio.file.Path tempDir = null;
                try {
                    tempDir = Files.createTempDirectory("flink-conf-");
                    Files.copy(srcFile.toPath(), tempDir.resolve("flink-conf.yaml"), StandardCopyOption.REPLACE_EXISTING);
                    flinkConfig = GlobalConfiguration.loadConfiguration(tempDir.toString());
                    log.info("加载自定义Flink配置: file={}", job.getFlinkConfPath());
                } catch (IOException e) {
                    log.error("加载Flink配置文件失败，将使用默认配置: {}", job.getFlinkConfPath(), e);
                } finally {
                    if (tempDir != null) {
                        try { Files.deleteIfExists(tempDir.resolve("flink-conf.yaml")); } catch (IOException ignored) {}
                        try { Files.deleteIfExists(tempDir); } catch (IOException ignored) {}
                    }
                }
            }
        }
        
        if (flinkConfig == null) {
            flinkConfig = GlobalConfiguration.loadConfiguration();
        }
        flinkConfig
                .set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName())
                // job name
                .set(YarnConfigOptions.APPLICATION_NAME, job.getJobName())
                //flink on yarn dependency
                .set(YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList(new Path(job.getLibPath()).toString()))
                .set(YarnConfigOptions.FLINK_DIST_JAR, job.getDistPath())
                .set(PipelineOptions.JARS, Collections.singletonList(new Path(job.getJarPath()).toString()))
                .set(JobManagerOptions.TOTAL_PROCESS_MEMORY,
                        MemorySize.parse(job.getJobManagerMemory() == null ? "1024" : job.getJobManagerMemory().toString(), MEGA_BYTES))
                .set(TaskManagerOptions.TOTAL_PROCESS_MEMORY,
                        MemorySize.parse(job.getTaskManagerMemory() == null ? "1024" : job.getTaskManagerMemory().toString(), MEGA_BYTES))
                .set(TaskManagerOptions.NUM_TASK_SLOTS,
                        job.getTaskManagerSlots() == null ? 1 : job.getTaskManagerSlots())
                .set(CoreOptions.DEFAULT_PARALLELISM, job.getParallelism() == null ? 1 : job.getParallelism());

        if (job.getTaskManagerMemoryManagedFraction() != null) {
            flinkConfig.set(TaskManagerOptions.MANAGED_MEMORY_FRACTION, job.getTaskManagerMemoryManagedFraction());
        }

        if (job.getLogConfPath() != null && !job.getLogConfPath().isBlank()) {
            flinkConfig.set(YarnConfigOptionsInternal.APPLICATION_LOG_CONFIG_FILE, job.getLogConfPath());
        }

        DefaultClusterClientServiceLoader clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
        ClusterClientFactory<ApplicationId> clientFactory = clusterClientServiceLoader.getClusterClientFactory(flinkConfig);
        ClusterSpecification clusterSpecification = clientFactory.getClusterSpecification(flinkConfig);

        List<String> argList = new ArrayList<>();
        if (job.getArgs() != null) {
            for (Map.Entry<String, String> entry : job.getArgs().entrySet()) {
                argList.add("--" + entry.getKey());
                argList.add(entry.getValue());
            }
        }
        String[] programArguments = argList.toArray(new String[0]);
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(programArguments, job.getMainClass());

        ClusterClient<ApplicationId> clusterClient = null;
        YarnClient yarnClient = YarnClientManager.getYarnClient(
                job.getCoreSitePath(),
                job.getYarnSitePath(),
                job.getHdfsSitePath()
        );
        YarnConfiguration yarnConf = (YarnConfiguration) yarnClient.getConfig();
        try (YarnClusterDescriptor clusterDescriptor = new YarnClusterDescriptor(
                flinkConfig,
                yarnConf,
                yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient),
                false);) {

            ClusterClientProvider<ApplicationId> clusterClientProvider = clusterDescriptor.deployApplicationCluster(
                    clusterSpecification,
                    applicationConfiguration);

            clusterClient = clusterClientProvider.getClusterClient();
            ApplicationId applicationId = clusterClient.getClusterId();
            String webInterfaceURL = clusterClient.getWebInterfaceURL();
            log.info("\n" +
                    "|-------------------------job start success-----------------------|\n" +
                    "|Flink Job Started: applicationId: " + applicationId + "  |\n" +
                    "|Flink Job Web Url: " + webInterfaceURL + "                    |\n" +
                    "|__________________________________________________________________|");
            return applicationId.toString();
        } catch (ClusterDeploymentException e) {
            throw new JobHandleException("提交任务失败", e);
        } finally {
            if (clusterClient != null) {
                clusterClient.close();
            }
        }
    }

    @Override
    public boolean cancel(ApplicationJobParam params) {
        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());
        YarnClient yarnClient = YarnClientManager.getYarnClient(
                params.getCoreSitePath(),
                params.getYarnSitePath(),
                params.getHdfsSitePath()
        );
        YarnConfiguration yarnConf = (YarnConfiguration) yarnClient.getConfig();
        try (YarnClusterDescriptor clusterDescriptor = new YarnClusterDescriptor(
                flinkConfig,
                yarnConf,
                yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient),
                false);) {
            clusterDescriptor.killCluster(ApplicationId.fromString(params.getJobId()));
            log.info("cancel flink job [{}] success!", params.getJobId());
            return true;
        } catch (FlinkException e) {
            log.error("cancel flink job error!", e);
        }
        return false;
    }

    @Override
    public JobStatusEnum status(ApplicationJobParam params) {
        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());
        YarnClient yarnClient = YarnClientManager.getYarnClient(
                params.getCoreSitePath(),
                params.getYarnSitePath(),
                params.getHdfsSitePath()
        );
        try {
            ApplicationReport application = yarnClient.getApplicationReport(ApplicationId.fromString(params.getJobId()));
            return ApplicationStatusConverter.convert(application.getYarnApplicationState());
        } catch (YarnException | IOException e) {
            throw new JobHandleException("查询任务状态错误", e);
        }
    }

    @Override
    public String getTrackingUrl(ApplicationJobParam params) {
        if (params.getYarnResourceManagerUrl() != null && params.getJobId() != null) {
            String rmUrl = params.getYarnResourceManagerUrl().replaceAll("/+$", "");
            return rmUrl + "/cluster/app/" + params.getJobId();
        }
        return "";
    }

    @Override
    public String getEngineType() {
        return EngineTypeEnum.FLINK.name();
    }

    @Override
    public String getJobMode() {
        return FlinkDeployMode.APPLICATION.name();
    }

    @Override
    public String getClusterType() {
        return ClusterTypeEnum.YARN.name();
    }
}
