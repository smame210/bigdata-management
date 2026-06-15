import axios, { AxiosError } from 'axios';
import axiosRetry from 'axios-retry';
import { nanoid } from '@sa/utils';
import { createAxiosConfig, createDefaultOptions, createRetryOptions } from './options';
import { BACKEND_ERROR_CODE, REQUEST_ID_KEY } from './constant';
function createCommonRequest(axiosConfig, options) {
  const opts = createDefaultOptions(options);
  const axiosConf = createAxiosConfig(axiosConfig);
  const instance = axios.create(axiosConf);
  const abortControllerMap = new Map();
  // config axios retry
  const retryOptions = createRetryOptions(axiosConf);
  axiosRetry(instance, retryOptions);
  instance.interceptors.request.use(conf => {
    const config = { ...conf };
    // set request id
    const requestId = nanoid();
    config.headers.set(REQUEST_ID_KEY, requestId);
    // config abort controller
    if (!config.signal) {
      const abortController = new AbortController();
      config.signal = abortController.signal;
      abortControllerMap.set(requestId, abortController);
    }
    // handle config by hook
    const handledConfig = opts.onRequest?.(config) || config;
    return handledConfig;
  });
  instance.interceptors.response.use(
    async response => {
      const responseType = response.config?.responseType || 'json';
      if (responseType !== 'json' || opts.isBackendSuccess(response)) {
        return Promise.resolve(response);
      }
      const fail = await opts.onBackendFail(response, instance);
      if (fail) {
        return fail;
      }
      const backendError = new AxiosError(
        'the backend request error',
        BACKEND_ERROR_CODE,
        response.config,
        response.request,
        response
      );
      await opts.onError(backendError);
      return Promise.reject(backendError);
    },
    async error => {
      await opts.onError(error);
      return Promise.reject(error);
    }
  );
  function cancelRequest(requestId) {
    const abortController = abortControllerMap.get(requestId);
    if (abortController) {
      abortController.abort();
      abortControllerMap.delete(requestId);
    }
  }
  function cancelAllRequest() {
    abortControllerMap.forEach(abortController => {
      abortController.abort();
    });
    abortControllerMap.clear();
  }
  return {
    instance,
    opts,
    cancelRequest,
    cancelAllRequest
  };
}
/**
 * create a request instance
 *
 * @param axiosConfig axios config
 * @param options request options
 */
export function createRequest(axiosConfig, options) {
  const { instance, opts, cancelRequest, cancelAllRequest } = createCommonRequest(axiosConfig, options);
  const request = async function request(config) {
    const response = await instance(config);
    const responseType = response.config?.responseType || 'json';
    if (responseType === 'json') {
      return opts.transformBackendResponse(response);
    }
    return response.data;
  };
  request.cancelRequest = cancelRequest;
  request.cancelAllRequest = cancelAllRequest;
  request.state = {};
  return request;
}
/**
 * create a flat request instance
 *
 * The response data is a flat object: { data: any, error: AxiosError }
 *
 * @param axiosConfig axios config
 * @param options request options
 */
export function createFlatRequest(axiosConfig, options) {
  const { instance, opts, cancelRequest, cancelAllRequest } = createCommonRequest(axiosConfig, options);
  const flatRequest = async function flatRequest(config) {
    try {
      const response = await instance(config);
      const responseType = response.config?.responseType || 'json';
      if (responseType === 'json') {
        const data = opts.transformBackendResponse(response);
        return { data, error: null, response };
      }
      return { data: response.data, error: null };
    } catch (error) {
      return { data: null, error, response: error.response };
    }
  };
  flatRequest.cancelRequest = cancelRequest;
  flatRequest.cancelAllRequest = cancelAllRequest;
  flatRequest.state = {};
  return flatRequest;
}
export { BACKEND_ERROR_CODE, REQUEST_ID_KEY };
