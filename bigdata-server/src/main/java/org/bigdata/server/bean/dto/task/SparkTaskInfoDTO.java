package org.bigdata.server.bean.dto.task;

import lombok.Data;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.exception.BizException;

@Data
public class SparkTaskInfoDTO extends AbstractTaskInfoDTO {

    @Override
    public TaskInfo toEntity() {
        throw new BizException("暂不支持spark任务类型");
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
