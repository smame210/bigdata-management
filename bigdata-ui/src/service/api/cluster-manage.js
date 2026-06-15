import { request } from '../request';
/**
 * 获取集群列表
 *
 * @param params 查询参数
 * @returns 集群列表数据
 */
export function fetchGetClusterList(params) {
  return request({
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
export function fetchGetClusterById(id) {
  return request({
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
export function updateClusterStatus(idStatus) {
  return request({
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
export function deleteCluster(id) {
  return request({
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
export function batchDeleteClusters(ids) {
  return request({
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
export function addCluster(data) {
  return request({
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
export function updateCluster(id, data) {
  return request({
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
export function uploadFile(file) {
  const formData = new FormData();
  formData.append('file', file);
  return request({
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
export function downloadFile(url) {
  return request({
    url: '/api/file/download',
    method: 'post',
    params: { url },
    responseType: 'blob',
    headers: {
      'content-type': 'application/json'
    }
  });
}
