package org.bigdata.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.task.AbstractTaskInfoDTO;
import org.bigdata.server.bean.dto.task.TaskQueryDTO;
import org.bigdata.server.bean.vo.TaskVO;
import org.bigdata.server.service.ITaskService;
import org.bigdata.server.util.R;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 任务管理
 */
@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    /**
     * 分页查询任务列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public R<IPage<TaskVO>> pageTask(@Valid @ModelAttribute TaskQueryDTO queryDTO) {
        log.info("分页查询任务列表: {}", queryDTO);
        return R.ok(taskService.pageTask(queryDTO));
    }

    /**
     * 查询任务列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R<List<TaskVO>> listAllTask() {
        log.info("查询任务列表");
        return R.ok(taskService.listAllTask());
    }

    /**
     * 新增任务
     *
     * @param taskInfoDTO 任务信息
     * @return 新增结果
     */
    @PostMapping("")
    public R<Integer> addTask(@Valid @RequestBody AbstractTaskInfoDTO taskInfoDTO) {
        return R.ok(taskService.add(taskInfoDTO));
    }

    /**
     * 删除任务
     *
     * @param ids 任务ID
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public R<Integer> deleteTask(@Valid @RequestBody List<Integer> ids) {
        log.info("删除任务: {}", ids);
        return R.ok(taskService.deleteBatchById(ids));
    }

    /**
     * 更新任务
     *
     * @param taskInfoDTO 任务信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public R<Integer> updateTask(@PathVariable Integer id,
                                 @Valid
                                 @NotNull(message = "任务信息为空！")
                                 @RequestBody AbstractTaskInfoDTO taskInfoDTO) {
        taskInfoDTO.setId(id);
        log.info("更新任务: {}", taskInfoDTO);
        return R.ok(taskService.update(taskInfoDTO));
    }

    /**
     * 根据ID查询任务
     *
     * @param id 任务ID
     * @return 任务详情
     */
    @GetMapping("/{id}")
    public R<TaskVO> getTaskById(@PathVariable Integer id) {
        log.info("根据ID查询任务: {}", id);
        TaskVO taskVO = taskService.getById(id);
        if (taskVO == null) {
            return R.failed("任务不存在");
        }
        return R.ok(taskVO);
    }

    /**
     * 启动任务
     *
     * @param id 任务ID
     * @return 更新结果
     */
    @PostMapping("/{id}/execute")
    public R<Integer> executeTask(@PathVariable Integer id) {
        log.info("启动任务: id:{}", id);
        return R.ok(taskService.executeTask(id));
    }

    /**
     * 根据集群类型和引擎类型查询支持的任务类型
     *
     * @param clusterType 集群类型
     * @param engineType 引擎类型
     * @return 支持的任务类型
     */
    @GetMapping("/task-types")
    public R<List<String>> listTaskTypes(@RequestParam String clusterType,
                                         @RequestParam String engineType) {
        return R.ok(taskService.listSupportedTaskTypes(clusterType, engineType));
    }
}
