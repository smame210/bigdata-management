package org.bigdata.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.bigdata.server.bean.dto.task.instance.TaskInstanceQueryDTO;
import org.bigdata.server.bean.vo.TaskInstanceVO;

public interface ITaskInstanceService {
    IPage<TaskInstanceVO> page(TaskInstanceQueryDTO taskInstanceQueryDTO);

    Boolean kill(Integer id);

    String getTrackingUrl(Integer id);
}
