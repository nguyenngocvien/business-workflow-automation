import axios from 'axios';
import type { InternalAxiosRequestConfig } from 'axios';
import { getStoredAccessToken } from './keycloak';
import { handleUnauthorized } from '../hooks/authHandler';

const baseURL = import.meta.env.VITE_API_BASE_URL?.trim();

export const httpClient = axios.create({
  baseURL: baseURL || undefined,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
});

export function setHttpBaseUrl(nextBaseUrl: string | undefined) {
  httpClient.defaults.baseURL = nextBaseUrl?.trim() || undefined;
}

function readJwtToken() {
  return getStoredAccessToken();
}

httpClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = readJwtToken();

  if (!token) {
    return config;
  }

  if (!config.headers.hasAuthorization()) {
    config.headers.set('Authorization', `Bearer ${token}`);
  }

  return config;
});

httpClient.interceptors.response.use(
  (response) => response,
  async (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      handleUnauthorized();
    }

    return Promise.reject(error);
  },
);
