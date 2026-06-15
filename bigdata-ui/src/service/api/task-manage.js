import { request } from '../request';
/** get task list */
export function fetchGetTaskList(params) {
  return request({
    url: '/api/tasks/page',
    method: 'get',
    params
  });
}
export function fetchGetAllTask() {
  return request({
    url: '/api/tasks/list',
    method: 'get'
  });
}
/** operation task */
export function fetchOperateTask(id, operation) {
  return request({
    url: '/taskManagement/status',
    method: 'post',
    data: {
      id,
      operation
    }
  });
}
/**
 * 批量删除集群
 *
 * @param ids 集群ID列表
 * @returns 删除结果
 */
export function batchDeleteTask(ids) {
  return request({
    url: '/api/tasks/batch',
    method: 'delete',
    data: ids
  });
}
/** create task */
export function fetchCreateTask(data) {
  return request({
    url: '/api/tasks',
    method: 'post',
    data
  });
}
/** update task */
export function fetchUpdateTask(id, data) {
  return request({
    url: `/api/tasks/${id}`,
    method: 'put',
    data
  });
}
/** get task by id */
export function fetchGetTaskById(id) {
  return request({
    url: `/api/tasks/${id}`,
    method: 'get'
  });
}
/** execute task (long timeout for YARN submission) */
export function fetchExecuteTask(id) {
  return request({
    url: `/api/tasks/${id}/execute`,
    method: 'post',
    timeout: 120_000
  });
}
export function fetchTaskTypeOptions(params) {
  return request({
    url: '/api/tasks/task-types',
    method: 'get',
    params
  });
}
