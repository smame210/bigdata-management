package org.bigdata.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bigdata.server.bean.entity.TaskStatusEvent;

@Mapper
public interface TaskStatusEventMapper extends BaseMapper<TaskStatusEvent> {
}
