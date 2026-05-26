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