package org.bigdata.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.cluster.AbstractClusterInfoDTO;
import org.bigdata.server.bean.dto.cluster.ClusterQueryDTO;
import org.bigdata.server.bean.vo.ClusterVO;
import org.bigdata.server.service.IClusterService;
import org.bigdata.server.util.R;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 集群管理
 */
@Slf4j
@RestController
@RequestMapping("/api/cluster")
@RequiredArgsConstructor
public class ClusterController {

    private final IClusterService clusterService;

    /**
     * 分页查询集群列表
     *
     * @param queryDTO 查询条件
     * @return 结果
     */
    @GetMapping("")
    public R<List<ClusterVO>> pageCluster(@ModelAttribute ClusterQueryDTO queryDTO) {
        log.info("查询集群列表: {}", queryDTO);
        return R.ok(clusterService.pageCluster(queryDTO));
    }

    /**
     * 根据id查询集群信息
     * @param id id
     * @return 集群信息
     */
    @GetMapping("/{id}")
    public R<ClusterVO> getById(@PathVariable Integer id) {
        return R.ok(clusterService.getById(id));
    }

    /**
     * 新增集群
     *
     * @param clusterInfoDTO 集群信息
     * @return 结果
     */
    @PostMapping("")
    public R<Integer> addCluster(@Valid @RequestBody AbstractClusterInfoDTO clusterInfoDTO) {
        log.info("新增集群: {}", clusterInfoDTO);
        return R.ok(clusterService.addCluster(clusterInfoDTO));
    }


    /**
     * 更新集群
     *
     * @param clusterInfoDTO 集群信息
     * @return 结果
     */
    @PutMapping("/{id}")
    public R<Integer> updateCluster(@PathVariable Integer id,
                                    @RequestBody AbstractClusterInfoDTO clusterInfoDTO) {
        clusterInfoDTO.setId(id);
        log.info("更新集群: {}", clusterInfoDTO);
        return R.ok(clusterService.updateCluster(clusterInfoDTO));
    }

    /**
     * 删除集群
     *
     * @param id 集群ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public R<Integer> deleteCluster(@PathVariable Integer id) {
        log.info("删除集群: {}", id);
        return R.ok(clusterService.deleteCluster(id));
    }

    /**
     * 批量删除集群
     *
     * @param ids 集群ID列表
     * @return 结果
     */
    @DeleteMapping("/batch")
    public R<Integer> deleteClusterBatch(@RequestBody List<Integer> ids) {
        log.info("批量删除集群: {}", ids);
        return R.ok(clusterService.deleteClusterBatch(ids));
    }

    /**
     * 更新集群状态
     *
     * @param id     集群ID
     * @param status 状态
     * @return 结果
     */
    @PutMapping("/{id}/status")
    public R<Integer> updateClusterStatus(@PathVariable Integer id,
                                          @RequestParam(value = "status") Integer status) {
        log.info("更新集群状态: {}, {}", id, status);
        return R.ok(clusterService.updateClusterStatus(id, status));
    }

    /**
     * 批量更新集群状态
     *
     * @param idStatus  状态集合
     * @return 结果
     */
    @PutMapping("/status/batch")
    public R<Integer> updateClusterStatus(@RequestBody Map<Integer, Integer> idStatus) {
        log.info("批量更新集群状态: {}", idStatus);
        return R.ok(clusterService.updateClusterStatusBatch(idStatus));
    }

}