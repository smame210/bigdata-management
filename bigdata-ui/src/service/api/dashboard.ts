import { request } from '../request';

/** 首页统计概览 */
export function fetchDashboardStatistics() {
  return request<Api.Dashboard.Statistics>({
    url: '/api/dashboard/statistics',
    method: 'get'
  });
}

/** 近N天任务趋势 */
export function fetchDashboardTaskTrend(days = 7) {
  return request<Api.Dashboard.TaskTrend[]>({
    url: '/api/dashboard/task-trend',
    method: 'get',
    params: { days }
  });
}

/** 任务状态分布 */
export function fetchDashboardStatusDistribution() {
  return request<Api.Dashboard.StatusDistribution[]>({
    url: '/api/dashboard/status-distribution',
    method: 'get'
  });
}
