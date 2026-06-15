import { request } from '../request';
export function fetchAlertList(params) {
  return request({
    url: '/api/alert/list',
    method: 'get',
    params
  });
}
export function fetchAlertDetail(id) {
  return request({
    url: `/api/alert/${id}`,
    method: 'get'
  });
}
export function fetchAlertAdd(data) {
  return request({
    url: '/api/alert',
    method: 'post',
    data
  });
}
export function fetchAlertDelete(id) {
  return request({
    url: `/api/alert/${id}`,
    method: 'delete'
  });
}
export function fetchAlertUpdate(id, data) {
  return request({
    url: `/api/alert/${id}`,
    method: 'put',
    data
  });
}
export function fetchAlertUpdateStatus(id, status) {
  return request({
    url: `/api/alert/${id}/status`,
    method: 'put',
    params: { status }
  });
}
export function fetchAlertTemplateParams(type) {
  return request({
    url: `/api/alert/template/params/${type}`,
    method: 'get'
  });
}
