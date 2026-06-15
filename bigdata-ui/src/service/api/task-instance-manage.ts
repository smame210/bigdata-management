import { request } from '../request';

export function fetchGetTaskInstanceList(params?: Api.TaskInstanceManage.TaskInstanceSearchParams) {
  return request<Api.TaskInstanceManage.TaskInstanceList>({
    url: '/api/task-instance/page',
    method: 'get',
    params
  });
}

export function fetchKillTaskInstance(id: number) {
  return request<boolean>({
    url: `/api/task-instance/kill/${id}`,
    method: 'put'
  });
}

export function fetchTrackingUrl(id: number) {
  return request<string>({
    url: `/api/task-instance/tracking-url/${id}`,
    method: 'get'
  });
}
