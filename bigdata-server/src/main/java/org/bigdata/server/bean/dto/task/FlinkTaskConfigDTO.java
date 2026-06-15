package org.bigdata.server.bean.dto.task;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
public class FlinkTaskConfigDTO {
    /**
     * flink 版本
     */
    private String version;

    /**
     * 任务类型 session/pre-job/application
     */
    private String taskType;

    /**
     * job manager地址 standalone方式有效
     */
    private String jobManagerUrl;

    /**
     * jar文件地址 例如hdfs://localhost:9000/flink  选择集群时生效
     */
    @NotNull(message = "文件地址不可为空")
    private String jarPath;

    /**
     * lib地址 例如hdfs://localhost:9000/flink 选择集群时生效
     */
    @NotNull(message = "依赖包地址不可为空")
    private String libPath;

    /**
     * flink配置文件地址 选择集群时生效
     */
    private String flinkConfPath;

    /**
     * log4j配置文件地址 选择集群时生效
     */
    private String logConfPath;

    /**
     * main class
     */
    @NotNull(message = "主类不可为空")
    private String mainClass;

    /**
     * 任务参数 key-value形式
     */
    private Map<String, String> args;

    /**
     * 并行度 选择集群时生效
     */
    private Integer parallelism = 1;

    /**
     * job manager内存大小 选择集群时生效
     */
    private Integer jobManagerMemory = 1024;

    /**
     * task manager内存大小 选择集群时生效
     */
    private Integer taskManagerMemory = 1024;

    /**
     * Solt数量 选择集群时生效
     */
    private Integer taskManagerSlots = 1;

    /**
     * task manager managed memory fraction 选择集群时生效
     */
    private Float taskManagerMemoryManagedFraction = 0.4f;
}
