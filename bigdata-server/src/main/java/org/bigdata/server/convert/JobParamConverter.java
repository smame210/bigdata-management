package org.bigdata.server.convert;

import org.bigdata.api.job.ClusterType;
import org.bigdata.api.job.EngineType;
import org.bigdata.api.job.JobDeployMode;
import org.bigdata.server.bean.entity.TaskInstance;

public interface JobParamConverter<U> extends ClusterType, EngineType, JobDeployMode {
    U convert(TaskInstance taskInstance);
}
