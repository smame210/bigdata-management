import { createServiceConfig } from '../../src/utils/service';
/**
 * Set http proxy
 *
 * @param env - The current env
 * @param enable - If enable http proxy
 */
export function createViteProxy(env, enable) {
  const isEnableHttpProxy = enable && env.VITE_HTTP_PROXY === 'Y';
  if (!isEnableHttpProxy) return undefined;
  const { baseURL, proxyPattern, other } = createServiceConfig(env);
  const proxy = createProxyItem({ baseURL, proxyPattern });
  other.forEach(item => {
    Object.assign(proxy, createProxyItem(item));
  });
  return proxy;
}
function createProxyItem(item) {
  const proxy = {};
  proxy[item.proxyPattern] = {
    target: item.baseURL,
    changeOrigin: true,
    rewrite: path => path.replace(new RegExp(`^${item.proxyPattern}`), '')
  };
  return proxy;
}
