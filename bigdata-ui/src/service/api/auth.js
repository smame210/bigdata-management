import { request } from '../request';
/**
 * Login
 *
 * @param userName User name
 * @param password Password
 */
export function fetchLogin(userName, password) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: {
      userName,
      password
    }
  });
}
/** Get user info */
export function fetchGetUserInfo() {
  return request({ url: '/auth/getUserInfo' });
}
/**
 * Refresh token
 *
 * @param refreshToken Refresh token
 */
export function fetchRefreshToken(refreshToken) {
  return request({
    url: '/auth/refreshToken',
    method: 'post',
    data: {
      refreshToken
    }
  });
}
/**
 * return custom backend error
 *
 * @param code error code
 * @param msg error message
 */
export function fetchCustomBackendError(code, msg) {
  return request({ url: '/auth/error', params: { code, msg } });
}
