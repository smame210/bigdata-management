import { request } from '../request';

/** get task list */
export function fetchGetTaskList(params?: Api.TaskManage.TaskSearchParams) {
  return request<Api.TaskManage.TaskList>({
    url: '/api/tasks/page',
    method: 'get',
    params
  });
}

export function fetchGetAllTask() {
  return request<Api.TaskManage.Task[]>({
    url: '/api/tasks/list',
    method: 'get'
  });
}

/** operation task */
export function fetchOperateTask(id: number, operation: number) {
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
export function batchDeleteTask(ids: number[]) {
  return request<number>({
    url: '/api/tasks/batch',
    method: 'delete',
    data: ids
  });
}

/** create task */
export function fetchCreateTask(data: Partial<Api.TaskManage.Task>) {
  return request<number>({
    url: '/api/tasks',
    method: 'post',
    data
  });
}

/** update task */
export function fetchUpdateTask(id: number, data: Partial<Api.TaskManage.Task>) {
  return request<number>({
    url: `/api/tasks/${id}`,
    method: 'put',
    data
  });
}

/** get task by id */
export function fetchGetTaskById(id: number) {
  return request<Api.TaskManage.Task>({
    url: `/api/tasks/${id}`,
    method: 'get'
  });
}

/** execute task (long timeout for YARN submission) */
export function fetchExecuteTask(id: number) {
  return request<number>({
    url: `/api/tasks/${id}/execute`,
    method: 'post',
    timeout: 120_000
  });
}

export function fetchTaskTypeOptions(params: { clusterType: string; engineType: Api.TaskManage.EngineType }) {
  return request<Api.TaskManage.TaskType[]>({
    url: '/api/tasks/task-types',
    method: 'get',
    params
  });
}
