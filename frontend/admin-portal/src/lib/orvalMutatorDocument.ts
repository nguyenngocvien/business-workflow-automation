import type { AxiosRequestConfig } from 'axios';

import { orvalMutator as baseOrvalMutator } from './orvalMutator';

function normalizePrefix(prefix: string) {
  if (!prefix) {
    return '';
  }

  return prefix.startsWith('/') ? prefix.replace(/\/$/, '') : `/${prefix.replace(/\/$/, '')}`;
}

const normalizedPrefix = normalizePrefix('/document');

export async function orvalMutator<T>(config: AxiosRequestConfig, options?: AxiosRequestConfig): Promise<T> {
  const nextConfig: AxiosRequestConfig = {
    ...config,
    url: config.url ? `${normalizedPrefix}${config.url.startsWith('/') ? config.url : `/${config.url}`}` : config.url,
  };

  return baseOrvalMutator<T>(nextConfig, options);
}
