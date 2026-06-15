package org.bigdata.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.bigdata.server.bean.dto.task.TaskQueryDTO;
import org.bigdata.server.bean.entity.TaskInfo;


/**
 * 任务信息Mapper接口
 */
@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {

    /**
     * 分页查询任务列表
     *
     * @param queryDTO 查询条件
     */
    IPage<TaskInfo> queryTaskByPage(Page<?> page, @Param("query") TaskQueryDTO queryDTO);

}