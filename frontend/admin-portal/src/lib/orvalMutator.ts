import type {
  AxiosRequestConfig,
  AxiosResponse,
} from 'axios';

import { httpClient } from './http';

export async function orvalMutator<T>(
  config: AxiosRequestConfig,
  options?: AxiosRequestConfig,
): Promise<T> {
  const response =
    await httpClient.request<T, AxiosResponse<T>>({
      ...config,
      ...options,
    });

  return response.data;
}

function normalizePrefix(prefix: string) {
  if (!prefix) {
    return '';
  }

  return prefix.startsWith('/') ? prefix.replace(/\/$/, '') : `/${prefix.replace(/\/$/, '')}`;
}

export function createPrefixedOrvalMutator(prefix: string) {
  const normalizedPrefix = normalizePrefix(prefix);

  return async function prefixedOrvalMutator<T>(
    config: AxiosRequestConfig,
    options?: AxiosRequestConfig,
  ): Promise<T> {
    const nextConfig: AxiosRequestConfig = {
      ...config,
      url: config.url ? `${normalizedPrefix}${config.url.startsWith('/') ? config.url : `/${config.url}`}` : config.url,
    };

    return orvalMutator<T>(nextConfig, options);
  };
}
