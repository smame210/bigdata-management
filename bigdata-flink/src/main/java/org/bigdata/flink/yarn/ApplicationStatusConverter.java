package org.bigdata.flink.yarn;

import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.bigdata.api.enums.JobStatusEnum;

public class ApplicationStatusConverter {

    public static JobStatusEnum convert(YarnApplicationState status) {
        switch (status) {
            case NEW:
                return JobStatusEnum.CREATED;
            case NEW_SAVING:
                return JobStatusEnum.CREATED;
            case SUBMITTED:
                return JobStatusEnum.CREATED;
            case ACCEPTED:
                return JobStatusEnum.ACCEPTED;
            case RUNNING:
                return JobStatusEnum.RUNNING;
            case FINISHED:
                return JobStatusEnum.FINISHED;
            case FAILED:
                return JobStatusEnum.FAILED;
            case KILLED:
                return JobStatusEnum.KILLED;
            default:
                return JobStatusEnum.UNKNOWN;
        }
    }
}
