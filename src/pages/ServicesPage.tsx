import { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  getEConnectorAPI,
  type ConnectionResult,
  type CreateServiceRequest,
  type PageServiceResult,
  type ServiceResult,
  type UpdateServiceRequest,
} from '../api/connector';
import { navIcons } from '../components/layout/navIcons';
import { ServiceCard } from '../components/services/ServiceCard';
import { ServiceCreatePanel } from '../components/services/ServiceCreatePanel';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import { SearchToolbar } from '../components/ui/SearchToolbar';
import { DEFAULT_CACHE_TIME, DEFAULT_STALE_TIME } from '../lib/queryClient';
import type { PageResult, ServiceConfig, ServiceFormOptions, ServiceSearchFilters } from '../types/service';

const connectorApi = getEConnectorAPI();
const SERVICE_PAGE_SIZE = 20;
const SQL_TYPES = [
  'VARCHAR',
  'CHAR',
  'TEXT',
  'CLOB',
  'INTEGER',
  'BIGINT',
  'SMALLINT',
  'DECIMAL',
  'NUMERIC',
  'BOOLEAN',
  'DATE',
  'TIMESTAMP',
  'BLOB',
];

const serviceConfigKeys = {
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

const defaultFilters: ServiceSearchFilters = {
  app: '',
  service: '',
  system: '',
  active: '',
  page: 0,
  size: SERVICE_PAGE_SIZE,
};

const emptyPage: PageResult<ServiceConfig> = {
  items: [],
  totalElements: 0,
  pageNumber: 0,
  pageSize: SERVICE_PAGE_SIZE,
  totalPages: 0,
  first: true,
  last: true,
  hasNext: false,
  hasPrevious: false,
};

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

function safeParseJson(value?: string | null): Record<string, unknown> | null {
  if (!value) {
    return null;
  }

  try {
    const parsed = JSON.parse(value);
    return isRecord(parsed) ? parsed : null;
  } catch {
    return null;
  }
}

function extractSection(source: Record<string, unknown>, keys: string[]): Record<string, unknown> | null {
  for (const key of keys) {
    const candidate = source[key];
    if (isRecord(candidate)) {
      return candidate;
    }
  }

  return null;
}

function mapServiceTypeFromApi(type?: ServiceResult['serviceType']): ServiceConfig['serviceType'] {
  switch (type) {
    case 'REST':
      return 'API';
    case 'DB':
      return 'SQL';
    case 'PIPELINE':
      return 'DECISION';
    case 'EMAIL':
      return 'EMAIL';
    default:
      return '';
  }
}

function mapServiceTypeToApi(type?: ServiceConfig['serviceType']): CreateServiceRequest['serviceType'] | undefined {
  switch (type) {
    case 'API':
      return 'REST';
    case 'SQL':
      return 'DB';
    case 'EMAIL':
      return 'EMAIL';
    case 'GENDOC':
    case 'DECISION':
      return 'PIPELINE';
    default:
      return undefined;
  }
}

function normalizeServiceDraft(service: ServiceConfig): ServiceConfig {
  return {
    ...service,
    apiConfig: service.apiConfig ?? {},
    smtpConfig: service.smtpConfig ?? {},
    sqlConfig: {
      ...(service.sqlConfig ?? {}),
      params: service.sqlConfig?.params ?? [],
    },
  };
}

function mapServiceResult(result: ServiceResult): ServiceConfig {
  const configSource = safeParseJson(result.configJson) ?? (isRecord(result.config) ? result.config : null) ?? {};
  const serviceType = mapServiceTypeFromApi(result.serviceType);
  const apiConfig = extractSection(configSource, ['apiConfig', 'api']) ?? (serviceType === 'API' ? configSource : {});
  const smtpConfig = extractSection(configSource, ['smtpConfig', 'smtp']) ?? (serviceType === 'EMAIL' ? configSource : {});
  const sqlConfigSource = extractSection(configSource, ['sqlConfig', 'sql']) ?? (serviceType === 'SQL' ? configSource : {});

  return normalizeServiceDraft({
    id: result.id ?? undefined,
    appName: result.appId ?? undefined,
    serviceName: result.serviceName ?? result.serviceCode ?? undefined,
    serviceType,
    systemName: result.serviceCode ?? undefined,
    version: result.serviceVersion ?? undefined,
    description: configSource.description && typeof configSource.description === 'string' ? configSource.description : undefined,
    status: result.active ?? undefined,
    logOn: result.logEnable ?? undefined,
    detailJson: result.configJson ?? undefined,
    apiConfig: apiConfig as ServiceConfig['apiConfig'],
    smtpConfig: smtpConfig as ServiceConfig['smtpConfig'],
    sqlConfig: {
      ...(sqlConfigSource ?? {}),
      params: Array.isArray(sqlConfigSource?.params) ? sqlConfigSource.params : [],
    } as ServiceConfig['sqlConfig'],
  });
}

function buildServiceConfigPayload(service: ServiceConfig) {
  const serviceName = service.serviceName?.trim() || '';
  const serviceType = mapServiceTypeToApi(service.serviceType);

  if (!serviceName) {
    throw new Error('Service name is required.');
  }

  if (!serviceType) {
    throw new Error('Service type is required.');
  }

  const config: Record<string, unknown> = {
    apiConfig: service.apiConfig ?? {},
    smtpConfig: service.smtpConfig ?? {},
    sqlConfig: {
      ...(service.sqlConfig ?? {}),
      params: service.sqlConfig?.params ?? [],
    },
  };

  return {
    serviceName,
    serviceType,
    serviceVersion: service.version?.trim() || undefined,
    appId: service.appName?.trim() || undefined,
    config,
    active: Boolean(service.status),
    logEnable: Boolean(service.logOn),
  };
}

function buildCreateServiceRequest(service: ServiceConfig): CreateServiceRequest {
  const payload = buildServiceConfigPayload(service);

  return {
    ...payload,
    serviceCode: payload.serviceName,
    serviceName: payload.serviceName,
    createdBy: service.createdBy?.trim() || undefined,
  };
}

function buildUpdateServiceRequest(service: ServiceConfig): UpdateServiceRequest {
  const payload = buildServiceConfigPayload(service);

  return {
    ...payload,
    updatedBy: service.updatedBy?.trim() || undefined,
  };
}

function toPageResult(result: PageServiceResult | undefined, filters: ServiceSearchFilters): PageResult<ServiceConfig> {
  const items = (result?.content ?? []).map(mapServiceResult);
  const filteredItems = items.filter((service) => {
    const app = filters.app.trim().toLowerCase();
    const serviceTerm = filters.service.trim().toLowerCase();
    const system = filters.system.trim().toLowerCase();

    const matchesApp = !app || (service.appName ?? '').toLowerCase().includes(app);
    const matchesService = !serviceTerm || (service.serviceName ?? '').toLowerCase().includes(serviceTerm);
    const matchesSystem = !system || (service.systemName ?? '').toLowerCase().includes(system);
    const matchesActive =
      !filters.active ||
      (filters.active === 'true' && Boolean(service.status)) ||
      (filters.active === 'false' && !Boolean(service.status));

    return matchesApp && matchesService && matchesSystem && matchesActive;
  });

  const pageNumber = result?.number ?? 0;
  const pageSize = result?.size ?? SERVICE_PAGE_SIZE;
  const totalPages = result?.totalPages ?? 0;

  return {
    items: filteredItems,
    totalElements: result?.totalElements ?? 0,
    pageNumber,
    pageSize,
    totalPages,
    first: Boolean(result?.first),
    last: Boolean(result?.last),
    hasNext: !result?.last && pageNumber + 1 < totalPages,
    hasPrevious: !result?.first && pageNumber > 0,
  };
}

function buildDatasourceUrl(connection: ConnectionResult): string | null {
  const configObject = safeParseJson(connection.configJson);
  if (configObject) {
    const urlCandidate =
      (typeof configObject.url === 'string' && configObject.url) ||
      (typeof configObject.jdbcUrl === 'string' && configObject.jdbcUrl) ||
      (typeof configObject.connectionString === 'string' && configObject.connectionString);

    if (urlCandidate) {
      return urlCandidate;
    }
  }

  return connection.configJson ?? null;
}

async function fetchServiceFormOptions() {
  const response = await connectorApi.findAll6({
    page: 0,
    size: 100,
    sort: ['connectionName,asc'],
  });

  const datasources = (response.content ?? [])
    .filter((connection) => connection.connectionType === 'DB')
    .map((connection, index) => ({
      label: connection.connectionName ?? connection.connectionCode ?? `Datasource ${index + 1}`,
      datasourceName: connection.connectionCode ?? connection.connectionName ?? null,
      url: buildDatasourceUrl(connection),
    }));

  return {
    datasources,
    sqlTypes: SQL_TYPES,
  } satisfies ServiceFormOptions;
}

function createBlankServiceDraft(): ServiceConfig {
  return normalizeServiceDraft({
    id: undefined,
    appName: '',
    serviceName: '',
    serviceType: '',
    systemName: '',
    version: '',
    description: '',
    status: true,
    logOn: true,
    detailJson: undefined,
    apiConfig: {},
    smtpConfig: {},
    sqlConfig: {
      params: [],
    },
  });
}

export function ServicesPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const slideOverTimeoutRef = useRef<number | null>(null);
  const [draftFilters, setDraftFilters] = useState<ServiceSearchFilters>(defaultFilters);
  const [filters, setFilters] = useState<ServiceSearchFilters>(defaultFilters);
  const [serviceDraft, setServiceDraft] = useState<ServiceConfig | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [readOnly, setReadOnly] = useState(true);
  const [isSlideOverMounted, setIsSlideOverMounted] = useState(false);
  const [isSlideOverVisible, setIsSlideOverVisible] = useState(false);
  const [isToolbarExpanded, setIsToolbarExpanded] = useState(false);
  const notify = useNotify();

  const servicesQuery = useQuery({
    queryKey: serviceConfigKeys.list(filters),
    queryFn: () =>
      connectorApi.findAll({
        page: filters.page,
        size: filters.size,
        sort: ['updatedAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
    placeholderData: keepPreviousData,
  });

  const serviceFormOptionsQuery = useQuery({
    queryKey: serviceConfigKeys.options(),
    queryFn: fetchServiceFormOptions,
    enabled: false,
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const saveMutation = useMutation({
    mutationFn: async (service: ServiceConfig) => {
      if (service.id) {
        return connectorApi.update(service.id, buildUpdateServiceRequest(service));
      }

      return connectorApi.create(buildCreateServiceRequest(service));
    },
    onSuccess: async (_, payload) => {
      if (payload.id) {
        queryClient.removeQueries({
          queryKey: serviceConfigKeys.detail(payload.id),
        });
      }

      await queryClient.invalidateQueries({
        queryKey: serviceConfigKeys.all,
      });
      await queryClient.invalidateQueries({
        queryKey: serviceConfigKeys.list(filters),
      });
    },
  });

  const pageResult = useMemo(() => toPageResult(servicesQuery.data, filters), [filters, servicesQuery.data]);
  const loading = servicesQuery.isLoading;
  const saving = saveMutation.isPending;

  useEffect(() => {
    if (!servicesQuery.error) {
      return;
    }

    notify.error(servicesQuery.error instanceof Error ? servicesQuery.error.message : 'Failed to load services.');
  }, [notify, servicesQuery.error]);

  useEffect(() => {
    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        closeSlideOver();
      }
    }

    document.addEventListener('keydown', handleKeyDown);

    return () => {
      if (slideOverTimeoutRef.current !== null) {
        window.clearTimeout(slideOverTimeoutRef.current);
      }
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  function updateDraft<K extends keyof ServiceSearchFilters>(key: K, value: ServiceSearchFilters[K]) {
    setDraftFilters((current) => ({
      ...current,
      [key]: value,
    }));
  }

  function submitSearch() {
    setFilters({
      ...draftFilters,
      page: 0,
    });
  }

  function changePage(page: number) {
    setFilters((current) => ({
      ...current,
      page,
    }));
  }

  async function ensureServiceFormOptions() {
    return queryClient.ensureQueryData({
      queryKey: serviceConfigKeys.options(),
      queryFn: fetchServiceFormOptions,
      staleTime: DEFAULT_STALE_TIME,
      gcTime: DEFAULT_CACHE_TIME,
    });
  }

  async function ensureServiceConfigDetail(id: number) {
    const detail = await queryClient.ensureQueryData({
      queryKey: serviceConfigKeys.detail(id),
      queryFn: () => connectorApi.findById(id),
      staleTime: DEFAULT_STALE_TIME,
      gcTime: DEFAULT_CACHE_TIME,
    });

    return normalizeServiceDraft(mapServiceResult(detail));
  }

  async function ensureNewServiceDraft() {
    const draft = await queryClient.ensureQueryData({
      queryKey: serviceConfigKeys.draft(),
      queryFn: async () => createBlankServiceDraft(),
      staleTime: DEFAULT_STALE_TIME,
      gcTime: DEFAULT_CACHE_TIME,
    });

    return normalizeServiceDraft(draft);
  }

  async function openServiceDetail(id: number) {
    await openServicePanel(id, true);
  }

  async function openServiceEditor(id: number) {
    await openServicePanel(id, false);
  }

  async function openServicePanel(id: number, nextReadOnly: boolean) {
    setDetailLoading(true);
    setReadOnly(nextReadOnly);
    if (slideOverTimeoutRef.current !== null) {
      window.clearTimeout(slideOverTimeoutRef.current);
      slideOverTimeoutRef.current = null;
    }
    setIsSlideOverMounted(true);
    window.requestAnimationFrame(() => {
      setIsSlideOverVisible(true);
    });

    try {
      const [detail] = await Promise.all([ensureServiceConfigDetail(id), ensureServiceFormOptions()]);

      setServiceDraft(detail);
    } catch (detailError) {
      notify.error(detailError instanceof Error ? detailError.message : 'Failed to load service detail.');
      closeSlideOver();
    } finally {
      setDetailLoading(false);
    }
  }

  async function openNewService() {
    setDetailLoading(true);
    setReadOnly(false);
    if (slideOverTimeoutRef.current !== null) {
      window.clearTimeout(slideOverTimeoutRef.current);
      slideOverTimeoutRef.current = null;
    }
    setIsSlideOverMounted(true);
    window.requestAnimationFrame(() => {
      setIsSlideOverVisible(true);
    });

    try {
      const [draft] = await Promise.all([ensureNewServiceDraft(), ensureServiceFormOptions()]);

      setServiceDraft(draft);
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to load add service form.');
      closeSlideOver();
    } finally {
      setDetailLoading(false);
    }
  }

  function closeSlideOver() {
    setIsSlideOverVisible(false);
    if (slideOverTimeoutRef.current !== null) {
      window.clearTimeout(slideOverTimeoutRef.current);
    }
    slideOverTimeoutRef.current = window.setTimeout(() => {
      setIsSlideOverMounted(false);
      setServiceDraft(null);
      setDetailLoading(false);
      setReadOnly(true);
      slideOverTimeoutRef.current = null;
    }, 280);
  }

  async function handleSaveService() {
    if (!serviceDraft) {
      return;
    }

    try {
      await saveMutation.mutateAsync(serviceDraft);
      notify.success(`Service ${serviceDraft.serviceName || 'configuration'} saved.`);
      if (serviceDraft.id) {
        setReadOnly(true);
      } else {
        closeSlideOver();
        setFilters((current) => ({ ...current, page: 0 }));
      }
    } catch (saveError) {
      notify.error(saveError instanceof Error ? saveError.message : 'Failed to save service.');
    }
  }

  async function handleToggleActive(service: ServiceConfig) {
    try {
      if (!service.id) {
        throw new Error('Service id is missing.');
      }

      const detail = await ensureServiceConfigDetail(service.id);
      await saveMutation.mutateAsync({
        ...detail,
        status: !Boolean(service.status),
      });
      notify.success(`Service ${service.serviceName} ${service.status ? 'disabled' : 'enabled'}.`);
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to update service status.');
    }
  }

  async function handleToggleLog(service: ServiceConfig) {
    try {
      if (!service.id) {
        throw new Error('Service id is missing.');
      }

      const detail = await ensureServiceConfigDetail(service.id);
      await saveMutation.mutateAsync({
        ...detail,
        logOn: !Boolean(service.logOn),
      });
      notify.success(`Logging ${service.logOn ? 'disabled' : 'enabled'} for ${service.serviceName}.`);
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to update logging status.');
    }
  }

  const visiblePages: number[] = [];
  const startPage = Math.max(0, pageResult.pageNumber - 2);
  const endPage = Math.min(Math.max(pageResult.totalPages - 1, 0), pageResult.pageNumber + 2);

  for (let page = startPage; page <= endPage; page += 1) {
    visiblePages.push(page);
  }

  return (
    <div className="flex h-full min-h-0 flex-col gap-3 overflow-hidden p-4">
      <div className="flex min-h-0 flex-1 flex-col gap-4 overflow-visible">
        <div className="flex items-start justify-between gap-3 py-4">
          <form
            onSubmit={(event) => {
              event.preventDefault();
              submitSearch();
            }}
            className="min-w-0"
          >
            <SearchToolbar
              onSearch={submitSearch}
              onExpandedChange={setIsToolbarExpanded}
              collapsedWidthClassName="w-full max-w-md"
              expandedWidthClassName="w-full max-w-5xl"
              primary={
                <input
                  value={draftFilters.service}
                  onChange={(event) => updateDraft('service', event.target.value)}
                  placeholder="Search service name..."
                  className="theme-input w-full border-none px-2 py-2 text-sm"
                />
              }
              advanced={
                <>
                  <input
                    value={draftFilters.app}
                    onChange={(event) => updateDraft('app', event.target.value)}
                    className="theme-input min-w-[180px] border-none"
                    placeholder="Application"
                  />
                  <input
                    value={draftFilters.system}
                    onChange={(event) => updateDraft('system', event.target.value)}
                    className="theme-input min-w-[180px] border-none"
                    placeholder="System"
                  />
                  <select
                    value={draftFilters.active}
                    onChange={(event) => updateDraft('active', event.target.value as ServiceSearchFilters['active'])}
                    className="theme-input min-w-[160px] border-none"
                  >
                    <option value="">All</option>
                    <option value="true">Active</option>
                    <option value="false">Inactive</option>
                  </select>
                </>
              }
            />
          </form>

          <div className={`shrink-0 transition-all duration-300 ${isToolbarExpanded ? 'pt-0.5' : ''}`}>
            <IconButton
              onClick={() => void openNewService()}
              icon={navIcons.plus}
              label="Add service"
              tone="success"
              size="sm"
            />
          </div>
        </div>

        <div className="min-h-0 flex-1 overflow-y-auto pr-1">
          {loading ? (
            <div className="flex min-h-[320px] items-center justify-center text-sm theme-muted-text">
              Loading services...
            </div>
          ) : pageResult.items.length === 0 ? (
            <div className="flex min-h-[320px] items-center justify-center text-sm theme-muted-text">
              No service configurations found.
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {pageResult.items.map((service, index) => (
                <ServiceCard
                  key={service.id ?? `${service.serviceName}-${index}`}
                  service={service}
                  index={index}
                  onView={(item) => void openServiceDetail(item.id!)}
                  onEdit={(item) => void openServiceEditor(item.id!)}
                  onViewLogs={(item) => navigate(`/logs?service=${encodeURIComponent(item.serviceName ?? '')}`)}
                  onToggleActive={(item) => void handleToggleActive(item)}
                  onToggleLog={(item) => void handleToggleLog(item)}
                />
              ))}
            </div>
          )}
        </div>

        <div className="theme-pagination-bar sticky bottom-0 z-20 mt-auto flex flex-col gap-4 px-6 py-2 backdrop-blur lg:flex-row lg:items-center lg:justify-between">
          <div className="theme-muted-text text-sm">
            Page <span className="theme-strong-text font-semibold">{pageResult.pageNumber + 1}</span> /{' '}
            <span>{Math.max(pageResult.totalPages, 1)}</span> | <span>{pageResult.totalElements}</span> items
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <div className="flex items-center gap-1">
              <IconButton
                onClick={() => changePage(0)}
                disabled={pageResult.first}
                icon={navIcons.chevronsLeft}
                label="First page"
                size="sm"
              />
              <IconButton
                onClick={() => changePage(pageResult.pageNumber - 1)}
                disabled={!pageResult.hasPrevious}
                icon={navIcons.chevronLeft}
                label="Previous page"
                size="sm"
              />
              {visiblePages.map((page) => (
                <button
                  type="button"
                  key={page}
                  onClick={() => changePage(page)}
                  className={`rounded-xl px-3 py-2 text-sm font-semibold ${
                    page === pageResult.pageNumber ? 'theme-table-page-active' : 'theme-input'
                  }`}
                >
                  {page + 1}
                </button>
              ))}
              <IconButton
                onClick={() => changePage(pageResult.pageNumber + 1)}
                disabled={!pageResult.hasNext}
                icon={navIcons.chevronRight}
                label="Next page"
                size="sm"
              />
              <IconButton
                onClick={() => changePage(Math.max(pageResult.totalPages - 1, 0))}
                disabled={pageResult.last}
                icon={navIcons.chevronsRight}
                label="Last page"
                size="sm"
              />
            </div>
          </div>
        </div>
      </div>

      {isSlideOverMounted ? (
        <div className="fixed inset-0 z-40">
          <button
            type="button"
            aria-label="Close configuration panel"
            onClick={closeSlideOver}
            className={`absolute inset-0 backdrop-blur-[1px] transition-all duration-200 ${
              isSlideOverVisible ? 'bg-slate-950/60 opacity-100' : 'bg-slate-950/0 opacity-0'
            }`}
          />
          <div
            className={`absolute right-0 top-0 h-full w-[70vw] min-w-[640px] overflow-hidden border-l border-[var(--border-subtle)] bg-[var(--surface-card-strong)] shadow-2xl transition-all duration-300 ease-out ${
              isSlideOverVisible ? 'translate-x-0 opacity-100' : 'translate-x-10 opacity-0'
            }`}
          >
            <div className="theme-table-divider flex items-center justify-between p-4">
              <h2 className="theme-strong-text text-lg font-semibold uppercase tracking-[0.18em]">
                {serviceDraft?.id ? 'Configuration' : 'Add Service'}
              </h2>
              <IconButton onClick={closeSlideOver} icon={navIcons.close} label="Close configuration panel" size="sm" />
            </div>
            <div className="h-[calc(100%-61px)] overflow-y-auto">
              <ServiceCreatePanel
                service={serviceDraft}
                options={serviceFormOptionsQuery.data ?? null}
                loading={detailLoading}
                saving={saving}
                readOnly={readOnly}
                onChange={setServiceDraft}
                onSave={() => void handleSaveService()}
                onEnableEdit={() => setReadOnly(false)}
              />
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
