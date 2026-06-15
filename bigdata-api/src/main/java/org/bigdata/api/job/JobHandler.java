package org.bigdata.api.job;

import org.bigdata.api.enums.JobStatusEnum;

public interface JobHandler<T> extends EngineType, JobDeployMode, ClusterType {

    String submit(T params);

    boolean cancel(T params);

    JobStatusEnum status(T params);

    /**
     * 获取任务实例的集群追踪URL（WebUI），由各实现类根据自身集群类型构造
     */
    default String getTrackingUrl(T params) {
        return "";
    }
}
