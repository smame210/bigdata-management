package org.bigdata.server.bean.dto.task.instance;

import lombok.Data;
import org.bigdata.server.bean.PageQueryDTO;

@Data
public class TaskInstanceQueryDTO extends PageQueryDTO {
    private String taskName;

    private String engineType;

    private String clusterName;

    private String taskMode;

    private String clusterType;
}
