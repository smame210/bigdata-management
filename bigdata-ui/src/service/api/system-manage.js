import { request } from '../request';
/** get role list */
export function fetchGetRoleList(params) {
  return request({
    url: '/systemManage/getRoleList',
    method: 'get',
    params
  });
}
/**
 * get all roles
 *
 * these roles are all enabled
 */
export function fetchGetAllRoles() {
  return request({
    url: '/systemManage/getAllRoles',
    method: 'get'
  });
}
/** get user list */
export function fetchGetUserList(params) {
  return request({
    url: '/systemManage/getUserList',
    method: 'get',
    params
  });
}
/** add user */
export function fetchAddUser(data) {
  return request({
    url: '/systemManage/addUser',
    method: 'post',
    data
  });
}
/** update user */
export function fetchUpdateUser(data) {
  return request({
    url: '/systemManage/updateUser',
    method: 'put',
    data
  });
}
/** delete user */
export function fetchDeleteUser(id) {
  return request({
    url: `/systemManage/deleteUser/${id}`,
    method: 'delete'
  });
}
/** get menu list */
export function fetchGetMenuList() {
  return request({
    url: '/systemManage/getMenuList/v2',
    method: 'get'
  });
}
/** get all pages */
export function fetchGetAllPages() {
  return request({
    url: '/systemManage/getAllPages',
    method: 'get'
  });
}
/** get menu tree */
export function fetchGetMenuTree() {
  return request({
    url: '/systemManage/getMenuTree',
    method: 'get'
  });
}
/** reset user password */
export function fetchResetPassword(id, oldPassword, newPassword) {
  return request({
    url: '/systemManage/resetPassword',
    method: 'put',
    params: { id, oldPassword, newPassword }
  });
}
/** update user status */
export function fetchUpdateUserStatus(id, status) {
  return request({
    url: '/systemManage/updateUserStatus',
    method: 'put',
    params: { id, status }
  });
}
