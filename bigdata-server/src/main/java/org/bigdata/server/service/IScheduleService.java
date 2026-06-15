package org.bigdata.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.bigdata.server.bean.dto.schedule.ScheduleDTO;
import org.bigdata.server.bean.dto.schedule.ScheduleQueryDTO;
import org.bigdata.server.bean.vo.ScheduleVO;

public interface IScheduleService {

    /**
     * 创建调度任务
     *
     * @param scheduleDTO 调度任务DTO
     * @return 创建的调度任务ID
     */
    Integer add(ScheduleDTO scheduleDTO);

    /**
     * 获取调度任务详情
     *
     * @param id 调度任务ID
     * @return 调度任务详情
     */
    ScheduleVO getScheduleById(Integer id);

    /**
     * 更新调度任务
     *
     * @param id 调度任务ID
     * @param scheduleDTO 调度任务DTO
     * @return 是否更新成功
     */
    Integer updateSchedule(Integer id, ScheduleDTO scheduleDTO);

    /**
     * 删除调度任务
     *
     * @param id 调度任务ID
     * @return 是否删除成功
     */
    Integer deleteSchedule(Integer id);

    /**
     * 更新调度任务状态
     *
     * @param id 调度任务ID
     * @param status 状态：0-禁用，1-启用
     * @return 是否更新成功
     */
    Integer updateScheduleStatus(Integer id, Integer status);

    /**
     * 分页查询调度任务列表
     *
     * @param scheduleQueryDTO 查询参数
     * @return 分页结果
     */
    IPage<ScheduleVO> pageSchedule(ScheduleQueryDTO scheduleQueryDTO);
}
