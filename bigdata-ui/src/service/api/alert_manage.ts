import { request } from '../request';

export function fetchAlertList(params: Api.AlertManage.AlertSearchParams) {
  return request<Api.AlertManage.Alert[]>({
    url: '/api/alert/list',
    method: 'get',
    params
  });
}

export function fetchAlertDetail(id: number) {
  return request<Api.AlertManage.Alert>({
    url: `/api/alert/${id}`,
    method: 'get'
  });
}

export function fetchAlertAdd(data: Api.AlertManage.Alert) {
  return request<number>({
    url: '/api/alert',
    method: 'post',
    data
  });
}

export function fetchAlertDelete(id: number) {
  return request<number>({
    url: `/api/alert/${id}`,
    method: 'delete'
  });
}

export function fetchAlertUpdate(id: number, data: Api.AlertManage.Alert) {
  return request<number>({
    url: `/api/alert/${id}`,
    method: 'put',
    data
  });
}

export function fetchAlertUpdateStatus(id: number, status: number) {
  return request<number>({
    url: `/api/alert/${id}/status`,
    method: 'put',
    params: { status }
  });
}

export function fetchAlertTemplateParams(type: string) {
  return request<Record<string, string>>({
    url: `/api/alert/template/params/${type}`,
    method: 'get'
  });
}
