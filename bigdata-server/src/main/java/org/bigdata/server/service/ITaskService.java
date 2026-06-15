package org.bigdata.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.bigdata.server.bean.dto.task.AbstractTaskInfoDTO;
import org.bigdata.server.bean.dto.task.TaskQueryDTO;
import org.bigdata.server.bean.vo.TaskVO;

import java.util.List;

/**
 * 任务服务接口
 */
public interface ITaskService {

    /**
     * 分页查询任务列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<TaskVO> pageTask(TaskQueryDTO queryDTO);

    /**
     * 查询任务列表
     *
     * @return 结果
     */
    List<TaskVO> listAllTask();

    /**
     * 任务新增
     *
     * @param taskInfoDTO 任务信息
     * @return 新增条数
     */
    Integer add(AbstractTaskInfoDTO taskInfoDTO);

    /**
     * 任务删除
     *
     * @param ids 任务id
     * @return 删除条数
     */
    Integer deleteBatchById(List<Integer> ids);

    /**
     * 更新任务
     *
     * @param taskInfoDTO 任务信息
     * @return 更新结果
     */
    Integer update(AbstractTaskInfoDTO taskInfoDTO);

    /**
     * 根据ID查询任务
     *
     * @param id 任务ID
     * @return 任务详情
     */
    TaskVO getById(Integer id);

    /**
     * 启动任务
     *
     * @param id 任务ID
     * @return 更新结果
     */
    Integer executeTask(Integer id);

    /**
     * 查询指定集群类型和引擎下支持的任务类型
     *
     * @param clusterType 集群类型
     * @param engineType 引擎类型
     * @return 任务类型列表
     */
    List<String> listSupportedTaskTypes(String clusterType, String engineType);
}
