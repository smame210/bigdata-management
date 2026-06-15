import { request } from '../request';
export function fetchAlertPolicyList(params) {
  return request({
    url: '/api/alert-policy/page',
    method: 'get',
    params
  });
}
export function fetchAlertPolicyDetail(id) {
  return request({
    url: `/api/alert-policy/${id}`,
    method: 'get'
  });
}
export function fetchAlertPolicyAdd(data) {
  return request({
    url: '/api/alert-policy',
    method: 'post',
    data
  });
}
export function fetchAlertPolicyDelete(id) {
  return request({
    url: `/api/alert-policy/${id}`,
    method: 'delete'
  });
}
export function fetchAlertPolicyUpdate(id, data) {
  return request({
    url: `/api/alert-policy/${id}`,
    method: 'put',
    data
  });
}
export function fetchAlertPolicyUpdateStatus(id, status) {
  return request({
    url: `/api/alert-policy/${id}/status`,
    method: 'put',
    params: { status }
  });
}
