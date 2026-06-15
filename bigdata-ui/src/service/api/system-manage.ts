import { request } from '../request';

/** get role list */
export function fetchGetRoleList(params?: Api.SystemManage.RoleSearchParams) {
  return request<Api.SystemManage.RoleList>({
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
  return request<Api.SystemManage.AllRole[]>({
    url: '/systemManage/getAllRoles',
    method: 'get'
  });
}

/** get user list */
export function fetchGetUserList(params?: Api.SystemManage.UserSearchParams) {
  return request<Api.SystemManage.UserList>({
    url: '/systemManage/getUserList',
    method: 'get',
    params
  });
}

/** add user */
export function fetchAddUser(data: Api.SystemManage.UserSaveParams) {
  return request({
    url: '/systemManage/addUser',
    method: 'post',
    data
  });
}

/** update user */
export function fetchUpdateUser(data: Api.SystemManage.UserSaveParams) {
  return request({
    url: '/systemManage/updateUser',
    method: 'put',
    data
  });
}

/** delete user */
export function fetchDeleteUser(id: number) {
  return request({
    url: `/systemManage/deleteUser/${id}`,
    method: 'delete'
  });
}

/** reset user password */
export function fetchResetPassword(id: number, oldPassword: string, newPassword: string) {
  return request({
    url: '/systemManage/resetPassword',
    method: 'put',
    params: { id, oldPassword, newPassword }
  });
}

/** update user status */
export function fetchUpdateUserStatus(id: number, status: number) {
  return request({
    url: '/systemManage/updateUserStatus',
    method: 'put',
    params: { id, status }
  });
}

/** get menu list */
export function fetchGetMenuList() {
  return request<Api.SystemManage.MenuList>({
    url: '/systemManage/getMenuList/v2',
    method: 'get'
  });
}

/** get all pages */
export function fetchGetAllPages() {
  return request<string[]>({
    url: '/systemManage/getAllPages',
    method: 'get'
  });
}

/** get menu tree */
export function fetchGetMenuTree() {
  return request<Api.SystemManage.MenuTree[]>({
    url: '/systemManage/getMenuTree',
    method: 'get'
  });
}
