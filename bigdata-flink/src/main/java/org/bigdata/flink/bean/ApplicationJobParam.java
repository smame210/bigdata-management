package org.bigdata.flink.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationJobParam {
    private String jobId;

    private String jobName;

    private String mainClass;

    private String jarPath;

    private String libPath;

    private String distPath;

    private String coreSitePath;

    private String hdfsSitePath;

    private String yarnSitePath;

    /**
     * 任务参数 key-value形式
     */
    private Map<String, String> args;

    /**
     * 并行度 选择集群时生效
     */
    @Builder.Default
    private Integer parallelism = 1;

    /**
     * job manager内存大小 选择集群时生效
     */
    @Builder.Default
    private Integer jobManagerMemory = 1024;

    /**
     * task manager内存大小 选择集群时生效
     */
    @Builder.Default
    private Integer taskManagerMemory = 1024;

    /**
     * Solt数量 选择集群时生效
     */
    @Builder.Default
    private Integer taskManagerSlots = 1;

    /**
     * task manager managed memory fraction 选择集群时生效
     */
    @Builder.Default
    private Float taskManagerMemoryManagedFraction = 0.4f;

    /**
     * Standalone集群JobManager地址
     */
    private String clusterUrl;

    /**
     * 日志配置文件(log4j/logback)路径
     */
    private String logConfPath;

    /**
     * Flink配置文件(flink-conf.yaml)路径
     */
    private String flinkConfPath;

    /**
     * YARN ResourceManager WebUI地址，用于构建追踪URL
     */
    private String yarnResourceManagerUrl;
}
