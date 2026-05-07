import {
  type QueryClient,
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from '@tanstack/react-query';
import type { ServiceConfig, ServiceSearchFilters } from '../types/service';
import {
  fetchNewServiceConfig,
  fetchServiceConfigDetail,
  fetchServiceConfigs,
  fetchServiceFormOptions,
  saveServiceConfig,
  toggleServiceActive,
  toggleServiceLog,
} from '../services/serviceConfigApi';
import { DEFAULT_CACHE_TIME, DEFAULT_STALE_TIME } from '../lib/queryClient';

const SERVICE_CONFIG_STALE_TIME = DEFAULT_STALE_TIME;
const SERVICE_CONFIG_CACHE_TIME = DEFAULT_CACHE_TIME;

export const serviceConfigKeys = {
  all: ['service-configs'] as const,
  list: (filters: ServiceSearchFilters) =>
    [
      ...serviceConfigKeys.all,
      'list',
      filters.app,
      filters.service,
      filters.system,
      filters.active,
      filters.page,
      filters.size,
    ] as const,
  detail: (id: number) => [...serviceConfigKeys.all, 'detail', id] as const,
  options: () => [...serviceConfigKeys.all, 'options'] as const,
  draft: () => [...serviceConfigKeys.all, 'draft'] as const,
};

export function normalizeServiceDraft(service: ServiceConfig): ServiceConfig {
  return {
    ...service,
    apiConfig: service.apiConfig ?? {},
    smtpConfig: service.smtpConfig ?? {},
    sqlConfig: {
      ...service.sqlConfig,
      params: service.sqlConfig?.params ?? [],
    },
  };
}

export function useServiceConfigsQuery(filters: ServiceSearchFilters) {
  return useQuery({
    queryKey: serviceConfigKeys.list(filters),
    queryFn: () => fetchServiceConfigs(filters),
    staleTime: SERVICE_CONFIG_STALE_TIME,
    gcTime: SERVICE_CONFIG_CACHE_TIME,
    placeholderData: keepPreviousData,
  });
}

export function useServiceFormOptionsQuery(enabled = true) {
  return useQuery({
    queryKey: serviceConfigKeys.options(),
    queryFn: fetchServiceFormOptions,
    enabled,
    staleTime: SERVICE_CONFIG_STALE_TIME,
    gcTime: SERVICE_CONFIG_CACHE_TIME,
  });
}

export function useServiceConfigMutations(filters: ServiceSearchFilters) {
  const queryClient = useQueryClient();

  const invalidateServiceQueries = async () => {
    await queryClient.invalidateQueries({
      queryKey: serviceConfigKeys.all,
    });
    await queryClient.invalidateQueries({
      queryKey: serviceConfigKeys.list(filters),
    });
  };

  const saveMutation = useMutation({
    mutationFn: saveServiceConfig,
    onSuccess: async (_, payload) => {
      if (payload.id) {
        queryClient.removeQueries({
          queryKey: serviceConfigKeys.detail(payload.id),
        });
      }

      await invalidateServiceQueries();
    },
  });

  const toggleActiveMutation = useMutation({
    mutationFn: ({ id, active }: { id: number; active: boolean }) => toggleServiceActive(id, active),
    onSuccess: async (_, variables) => {
      queryClient.removeQueries({
        queryKey: serviceConfigKeys.detail(variables.id),
      });
      await invalidateServiceQueries();
    },
  });

  const toggleLogMutation = useMutation({
    mutationFn: ({ id, enabled }: { id: number; enabled: boolean }) => toggleServiceLog(id, enabled),
    onSuccess: async (_, variables) => {
      queryClient.removeQueries({
        queryKey: serviceConfigKeys.detail(variables.id),
      });
      await invalidateServiceQueries();
    },
  });

  return {
    saveMutation,
    toggleActiveMutation,
    toggleLogMutation,
  };
}

export async function ensureServiceFormOptions(queryClient: QueryClient) {
  return queryClient.ensureQueryData({
    queryKey: serviceConfigKeys.options(),
    queryFn: fetchServiceFormOptions,
    staleTime: SERVICE_CONFIG_STALE_TIME,
    gcTime: SERVICE_CONFIG_CACHE_TIME,
  });
}

export async function ensureServiceConfigDetail(
  queryClient: QueryClient,
  id: number,
) {
  const detail = await queryClient.ensureQueryData({
    queryKey: serviceConfigKeys.detail(id),
    queryFn: () => fetchServiceConfigDetail(id),
    staleTime: SERVICE_CONFIG_STALE_TIME,
    gcTime: SERVICE_CONFIG_CACHE_TIME,
  });

  return normalizeServiceDraft(detail);
}

export async function ensureNewServiceDraft(queryClient: QueryClient) {
  const draft = await queryClient.ensureQueryData({
    queryKey: serviceConfigKeys.draft(),
    queryFn: fetchNewServiceConfig,
    staleTime: SERVICE_CONFIG_STALE_TIME,
    gcTime: SERVICE_CONFIG_CACHE_TIME,
  });

  return normalizeServiceDraft(draft);
}
