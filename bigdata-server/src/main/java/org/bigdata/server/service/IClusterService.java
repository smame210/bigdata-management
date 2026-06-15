package org.bigdata.server.service;

import org.bigdata.server.bean.dto.cluster.AbstractClusterInfoDTO;
import org.bigdata.server.bean.dto.cluster.ClusterQueryDTO;
import org.bigdata.server.bean.vo.ClusterVO;

import java.util.List;
import java.util.Map;

/**
 * 集群服务接口
 */
public interface IClusterService {

    /**
     * 查询集群列表
     *
     * @param queryDTO 查询条件
     * @return 结果
     */
    List<ClusterVO> pageCluster(ClusterQueryDTO queryDTO);

    Integer addCluster(AbstractClusterInfoDTO clusterInfoDTO);

    Integer updateCluster(AbstractClusterInfoDTO clusterInfoDTO);

    Integer deleteCluster(Integer id);

    Integer deleteClusterBatch(List<Integer> id);

    Integer updateClusterStatus(Integer id, Integer status);

    Integer updateClusterStatusBatch(Map<Integer, Integer> idStatus);

    ClusterVO getById(Integer id);
}