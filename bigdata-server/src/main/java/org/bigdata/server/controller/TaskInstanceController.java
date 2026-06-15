package org.bigdata.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.task.instance.TaskInstanceQueryDTO;
import org.bigdata.server.bean.vo.TaskInstanceVO;
import org.bigdata.server.service.ITaskInstanceService;
import org.bigdata.server.util.R;
import org.springframework.web.bind.annotation.*;

/**
 * 任务实例管理
 */
@Slf4j
@RestController
@RequestMapping("/api/task-instance")
@RequiredArgsConstructor
public class TaskInstanceController {

    private final ITaskInstanceService taskInstanceService;

    /**
     * 分页查询任务实例
     *
     * @param taskInstanceQueryDTO 查询参数
     * @return 查询结果
     */
    @GetMapping("/page")
    public R<IPage<TaskInstanceVO>> page(@ModelAttribute TaskInstanceQueryDTO taskInstanceQueryDTO) {
        IPage<TaskInstanceVO> result = taskInstanceService.page(taskInstanceQueryDTO);
        return R.ok(result);
    }

    /**
     * 停止实例
     *
     * @param id 主键ID
     * @return 停止结果
     */
    @PutMapping("/kill/{id}")
    public R<Boolean> kill(@PathVariable Integer id) {
        return R.ok(taskInstanceService.kill(id));
    }

    /**
     * 获取任务实例的集群追踪URL（WebUI地址）
     *
     * @param id 主键ID
     * @return 追踪URL
     */
    @GetMapping("/tracking-url/{id}")
    public R<String> getTrackingUrl(@PathVariable Integer id) {
        return R.ok(taskInstanceService.getTrackingUrl(id));
    }
}
