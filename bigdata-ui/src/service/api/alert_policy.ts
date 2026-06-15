import { request } from '../request';

export function fetchAlertPolicyList(params?: Api.AlertPolicyManage.AlertPolicySearchParams) {
  return request<Api.AlertPolicyManage.AlertPolicyList>({
    url: '/api/alert-policy/page',
    method: 'get',
    params
  });
}

export function fetchAlertPolicyDetail(id: number) {
  return request<Api.AlertPolicyManage.AlertPolicy>({
    url: `/api/alert-policy/${id}`,
    method: 'get'
  });
}

export function fetchAlertPolicyAdd(data: Partial<Api.AlertPolicyManage.AlertPolicy>) {
  return request<number>({
    url: '/api/alert-policy',
    method: 'post',
    data
  });
}

export function fetchAlertPolicyDelete(id: number) {
  return request<number>({
    url: `/api/alert-policy/${id}`,
    method: 'delete'
  });
}

export function fetchAlertPolicyUpdate(id: number, data: Partial<Api.AlertPolicyManage.AlertPolicy>) {
  return request<number>({
    url: `/api/alert-policy/${id}`,
    method: 'put',
    data
  });
}

export function fetchAlertPolicyUpdateStatus(id: number, status: number) {
  return request<number>({
    url: `/api/alert-policy/${id}/status`,
    method: 'put',
    params: { status }
  });
}
