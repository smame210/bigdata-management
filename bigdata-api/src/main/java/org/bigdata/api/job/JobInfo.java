package org.bigdata.api.job;

import org.bigdata.api.enums.JobStatusEnum;

public abstract class JobInfo {
    public abstract JobStatusEnum getJobStatus();

    public abstract String getJobId();
}
