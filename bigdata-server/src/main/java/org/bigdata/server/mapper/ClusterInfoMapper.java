package org.bigdata.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.bigdata.server.bean.dto.cluster.ClusterQueryDTO;
import org.bigdata.server.bean.entity.ClusterInfo;

import java.util.List;

/**
 * 集群信息Mapper接口
 */
@Mapper
public interface ClusterInfoMapper extends BaseMapper<ClusterInfo> {

    /**
     * 分页查询集群列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    List<ClusterInfo> queryClusterByPage(@Param("query") ClusterQueryDTO queryDTO);
}