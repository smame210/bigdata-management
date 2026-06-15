import { request } from '../request';

/** ill return 首页统计概览 */
export function fetchDashboardStatistics() {
  return request({
    url: '/api/dashboard/statistics',
    method: 'get'
  });
}

/** ill return 近N天任务趋势 */
export function fetchDashboardTaskTrend(days = 7) {
  return request({
    url: '/api/dashboard/task-trend',
    method: 'get',
    params: { days }
  });
}

/** ill return 任务状态分布 */
export function fetchDashboardStatusDistribution() {
  return request({
    url: '/api/dashboard/status-distribution',
    method: 'get'
  });
}
