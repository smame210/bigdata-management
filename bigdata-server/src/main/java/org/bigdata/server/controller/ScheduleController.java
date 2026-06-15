package org.bigdata.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.server.bean.dto.schedule.ScheduleDTO;
import org.bigdata.server.bean.dto.schedule.ScheduleQueryDTO;
import org.bigdata.server.bean.vo.ScheduleVO;
import org.bigdata.server.service.IScheduleService;
import org.bigdata.server.util.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    
    private final IScheduleService scheduleService;
    
    /**
     * 创建调度任务
     *
     * @param scheduleDTO 调度任务DTO
     * @return 创建结果
     */
    @PostMapping("")
    public R<Integer> add(@RequestBody @Validated ScheduleDTO scheduleDTO) {
        log.info("创建调度任务: {}", scheduleDTO);
        Integer scheduleId = scheduleService.add(scheduleDTO);
        return R.ok(scheduleId);
    }
    
    /**
     * 获取调度任务详情
     *
     * @param id 调度任务ID
     * @return 调度任务详情
     */
    @GetMapping("/{id}")
    public R<ScheduleVO> getScheduleById(@PathVariable Integer id) {
        log.info("获取调度任务详情, id: {}", id);
        ScheduleVO scheduleVO = scheduleService.getScheduleById(id);
        return R.ok(scheduleVO);
    }
    
    /**
     * 更新调度任务
     *
     * @param id 调度任务ID
     * @param scheduleDTO 调度任务DTO
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public R<Integer> updateSchedule(@PathVariable Integer id, @RequestBody @Validated ScheduleDTO scheduleDTO) {
        log.info("更新调度任务, id: {}, data: {}", id, scheduleDTO);
        return R.ok(scheduleService.updateSchedule(id, scheduleDTO));
    }
    
    /**
     * 删除调度任务
     *
     * @param id 调度任务ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<Integer> deleteSchedule(@PathVariable Integer id) {
        log.info("删除调度任务, id: {}", id);
        return R.ok(scheduleService.deleteSchedule(id));
    }
    
    /**
     * 启用/禁用调度任务
     *
     * @param id 调度任务ID
     * @param status 状态：0-禁用，1-启用
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public R<Integer> updateScheduleStatus(@PathVariable Integer id,
                                           @RequestParam Integer status) {
        log.info("更新调度任务状态, id: {}, status: {}", id, status);
        return R.ok(scheduleService.updateScheduleStatus(id, status));
    }
    
    /**
     * 分页查询调度任务列表
     *
     * @param scheduleQueryDTO 查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    public R<IPage<ScheduleVO>> pageSchedule(@ModelAttribute ScheduleQueryDTO scheduleQueryDTO) {
        log.info("分页查询调度任务列表, 参数: {}", scheduleQueryDTO);
        return R.ok(scheduleService.pageSchedule(scheduleQueryDTO));
    }
}