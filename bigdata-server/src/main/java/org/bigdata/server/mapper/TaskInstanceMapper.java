package org.bigdata.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.bigdata.server.bean.dto.task.instance.TaskInstanceQueryDTO;
import org.bigdata.server.bean.entity.TaskInstance;
import org.bigdata.server.bean.vo.StatusDistributionVO;
import org.bigdata.server.bean.vo.TaskTrendVO;

import java.util.List;

@Mapper
public interface TaskInstanceMapper extends BaseMapper<TaskInstance> {
    IPage<TaskInstance> page(Page<?> page, @Param("params") TaskInstanceQueryDTO taskInstanceQueryDTO);

    List<TaskTrendVO> queryTaskTrend(@Param("days") int days);

    List<StatusDistributionVO> queryStatusDistribution();
}
