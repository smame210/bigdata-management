import { request } from '../request';
export function fetchGetTaskInstanceList(params) {
  return request({
    url: '/api/task-instance/page',
    method: 'get',
    params
  });
}
export function fetchKillTaskInstance(id) {
  return request({
    url: `/api/task-instance/kill/${id}`,
    method: 'put'
  });
}
export function fetchTrackingUrl(id) {
  return request({
    url: `/api/task-instance/tracking-url/${id}`,
    method: 'get'
  });
}
