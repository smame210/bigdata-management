import { request } from '../request';

/**
 * 获取集群列表
 *
 * @param params 查询参数
 * @returns 集群列表数据
 */

export function fetchGetClusterList(params?: Api.ClusterManage.ClusterSearchParams) {
  return request<Api.ClusterManage.Cluster[]>({
    url: '/api/cluster',
    method: 'get',
    params
  });
}

/**
 * 获取集群列表
 *
 * @param id 查询参数
 * @returns 集群列表数据
 */

export function fetchGetClusterById(id: number) {
  return request<Api.ClusterManage.Cluster>({
    url: `/api/cluster/${id}`,
    method: 'get'
  });
}

/**
 * 更新集群状态
 *
 * @param idStatus id status映射集合
 * @returns 更新结果
 */
export function updateClusterStatus(idStatus: Record<number, number>) {
  return request<number>({
    url: '/api/cluster/status/batch',
    method: 'put',
    data: idStatus
  });
}

/**
 * 删除集群
 *
 * @param id 集群ID
 * @returns 删除结果
 */
export function deleteCluster(id: number) {
  return request<number>({
    url: `/api/cluster/${id}`,
    method: 'delete'
  });
}

/**
 * 批量删除集群
 *
 * @param ids 集群ID列表
 * @returns 删除结果
 */
export function batchDeleteClusters(ids: number[]) {
  return request<number>({
    url: '/api/cluster/batch',
    method: 'delete',
    data: ids
  });
}

/**
 * 添加集群
 *
 * @param data 集群数据
 * @returns 结果
 */
export function addCluster(data: Partial<Api.ClusterManage.Cluster> & Record<string, unknown>) {
  return request<number>({
    url: '/api/cluster',
    method: 'post',
    data
  });
}

/**
 * 更新集群
 *
 * @param data 集群数据
 * @returns 结果
 */
export function updateCluster(id: number, data: Partial<Api.ClusterManage.Cluster> & Record<string, unknown>) {
  return request<number>({
    url: `/api/cluster/${id}`,
    method: 'put',
    data
  });
}

/**
 * 上传文件
 *
 * @param file 文件对象
 * @returns 文件URL
 */
export function uploadFile(file: File) {
  const formData = new FormData();
  formData.append('file', file);

  return request<string>({
    url: '/api/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 下载文件
 *
 * @param url 文件URL
 */
export function downloadFile(url: string) {
  return request<Blob>({
    url: '/api/file/download',
    method: 'post',
    params: { url },
    responseType: 'blob' as any,
    headers: {
      'content-type': 'application/json'
    }
  });
}
