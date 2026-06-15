package org.bigdata.server.bean.dto.task;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.bigdata.server.bean.entity.TaskInfo;
import org.bigdata.server.enums.FlinkTaskTypeEnum;
import org.bigdata.server.exception.BizException;
import org.springframework.beans.BeanUtils;

import jakarta.validation.constraints.NotNull;

@Data
public class FlinkTaskInfoDTO extends AbstractTaskInfoDTO {

    @NotNull(message = "配置项不可为空")
    private FlinkTaskConfigDTO config;

    @Override
    public TaskInfo toEntity() {
        if (!isValid()) {
            throw new BizException("参数配置有误，请检查参数配置！");
        }
        TaskInfo taskInfo = new TaskInfo();
        BeanUtils.copyProperties(this, taskInfo);
        taskInfo.setMetadata(JSON.toJSONString(config));
        return taskInfo;
    }

    @Override
    public boolean isValid() {
        if (!FlinkTaskTypeEnum.supportedTaskTypes().contains(config.getTaskType().toLowerCase())) {
            return false;
        }
        if (config.getParallelism() <= 0) {
            return false;
        }
        if (config.getJobManagerMemory() <= 0) {
            return false;
        }
        if (config.getTaskManagerMemory() <= 0) {
            return false;
        }
        if (config.getTaskManagerSlots() <= 0) {
            return false;
        }
        if (config.getTaskManagerMemoryManagedFraction() != null && (config.getTaskManagerMemoryManagedFraction() < 0f || config.getTaskManagerMemoryManagedFraction() > 1f)) {
            return false;
        }
        return true;
    }
}
