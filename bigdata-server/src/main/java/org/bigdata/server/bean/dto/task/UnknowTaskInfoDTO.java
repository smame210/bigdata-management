package org.bigdata.server.bean.dto.task;

import lombok.Data;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.exception.BizException;

@Data
public class UnknowTaskInfoDTO extends AbstractTaskInfoDTO {

    @Override
    public TaskInfo toEntity() {
        throw new BizException("未知的引擎类型");
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
