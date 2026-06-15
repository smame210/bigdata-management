export function getContentType(config) {
  const contentType = config.headers?.['Content-Type'] || 'application/json';
  return contentType;
}
/**
 * check if http status is success
 *
 * @param status
 */
export function isHttpSuccess(status) {
  const isSuccessCode = status >= 200 && status < 300;
  return isSuccessCode || status === 304;
}
/**
 * is response json
 *
 * @param response axios response
 */
export function isResponseJson(response) {
  const { responseType } = response.config;
  return responseType === 'json' || responseType === undefined;
}
