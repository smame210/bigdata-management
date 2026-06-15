import { request } from '../request';
/**
 * 分页查询调度任务列表
 *
 * @param params 查询参数
 * @returns 分页结果
 */
export function fetchScheduleList(params) {
  return request({
    url: '/api/schedule/page',
    method: 'get',
    params
  });
}
/**
 * 获取调度任务详情
 *
 * @param id 调度任务ID
 * @returns 调度任务详情
 */
export function fetchScheduleById(id) {
  return request({
    url: `/api/schedule/${id}`,
    method: 'get'
  });
}
/**
 * 创建调度任务
 *
 * @param data 调度任务数据
 * @returns 创建结果
 */
export function createSchedule(data) {
  return request({
    url: '/api/schedule',
    method: 'post',
    data
  });
}
/**
 * 更新调度任务
 *
 * @param id 调度任务ID
 * @param data 调度任务数据
 * @returns 更新结果
 */
export function updateSchedule(id, data) {
  return request({
    url: `/api/schedule/${id}`,
    method: 'put',
    data
  });
}
/**
 * 删除调度任务
 *
 * @param id 调度任务ID
 * @returns 删除结果
 */
export function deleteSchedule(id) {
  return request({
    url: `/api/schedule/${id}`,
    method: 'delete'
  });
}
/**
 * 更新调度任务状态
 *
 * @param id 调度任务ID
 * @param status 状态：0-禁用，1-启用
 * @returns 操作结果
 */
export function updateScheduleStatus(id, status) {
  return request({
    url: `/api/schedule/${id}/status`,
    method: 'put',
    params: { status }
  });
}
/**
 * 手动触发调度任务
 *
 * @param id 调度任务ID
 * @returns 触发结果
 */
export function triggerSchedule(id) {
  return request({
    url: `/api/schedule/${id}/trigger`,
    method: 'post'
  });
}
