import { stringify } from 'qs';
import { isHttpSuccess } from './shared';
export function createDefaultOptions(options) {
  const opts = {
    onRequest: async config => config,
    isBackendSuccess: _response => true,
    onBackendFail: async () => {},
    transformBackendResponse: async response => response.data,
    onError: async () => {}
  };
  Object.assign(opts, options);
  return opts;
}
export function createRetryOptions(config) {
  const retryConfig = {
    retries: 0
  };
  Object.assign(retryConfig, config);
  return retryConfig;
}
export function createAxiosConfig(config) {
  const TEN_SECONDS = 10 * 1000;
  const axiosConfig = {
    timeout: TEN_SECONDS,
    headers: {
      'Content-Type': 'application/json'
    },
    validateStatus: isHttpSuccess,
    paramsSerializer: params => {
      return stringify(params);
    }
  };
  Object.assign(axiosConfig, config);
  return axiosConfig;
}
