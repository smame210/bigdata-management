package org.bigdata.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.bigdata.server.bean.dto.schedule.ScheduleQueryDTO;
import org.bigdata.server.bean.entity.TaskSchedule;

@Mapper
//@DependsOn("databaseInitialize")
public interface TaskScheduleMapper extends BaseMapper<TaskSchedule> {
    IPage<TaskSchedule> page(Page<?> page, @Param("params") ScheduleQueryDTO scheduleQueryDTO);
}