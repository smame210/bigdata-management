package org.bigdata.flink.bean;

import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.api.job.JobInfo;

public class ApplicationJobInfo extends JobInfo {
    @Override
    public JobStatusEnum getJobStatus() {
        return null;
    }

    @Override
    public String getJobId() {
        return "";
    }
}
