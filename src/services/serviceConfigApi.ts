import { api } from './api';
import type {
  PageResult,
  ServiceConfig,
  ServiceFormOptions,
  ServiceSearchFilters,
} from '../types/service';

type MessageResponse = {
  message?: string;
};

function toOptionalBoolean(value: ServiceSearchFilters['active']) {
  if (value === 'true') {
    return true;
  }

  if (value === 'false') {
    return false;
  }

  return undefined;
}

export async function fetchServiceConfigs(filters: ServiceSearchFilters) {
  const { data } = await api.get<PageResult<ServiceConfig>>('/services', {
    params: {
      app: filters.app || undefined,
      service: filters.service || undefined,
      system: filters.system || undefined,
      active: toOptionalBoolean(filters.active),
      page: filters.page,
      size: filters.size,
    },
  });

  return data;
}

export async function fetchServiceConfigDetail(id: number) {
  const { data } = await api.get<ServiceConfig>(`/services/${id}`);
  return data;
}

export async function fetchNewServiceConfig() {
  return {
    appName: '',
    serviceName: '',
    serviceType: '',
    systemName: '',
    version: '1.0',
    description: '',
    status: true,
    logOn: true,
    detailJson: '',
    apiConfig: {},
    smtpConfig: {},
    sqlConfig: {
      params: [],
    },
  } satisfies ServiceConfig;
}

export async function fetchServiceFormOptions() {
  const { data } = await api.get<ServiceFormOptions>('/services/options');
  return data;
}

export async function saveServiceConfig(payload: ServiceConfig) {
  if (payload.id) {
    await api.put(`/services/${payload.id}`, payload);
    return;
  }

  await api.post('/services', payload);
}

export async function toggleServiceActive(id: number, active: boolean) {
  await api.patch(`/services/${id}/status`, undefined, {
    params: { active },
  });
}

export async function toggleServiceLog(id: number, enabled: boolean) {
  await api.patch(`/services/${id}/logging`, undefined, {
    params: { enabled },
  });
}

export async function clearServiceCache(id: number) {
  const { data } = await api.post<MessageResponse>(`/services/${id}/cache/clear`);
  return data;
}

export async function clearAllServiceCache() {
  const { data } = await api.post<MessageResponse>('/services/cache/clear');
  return data;
}
