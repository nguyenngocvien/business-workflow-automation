import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';
import { navIcons } from '../components/layout/navIcons';
import { ServiceCard } from '../components/services/ServiceCard';
import { ServiceCreatePanel } from '../components/services/ServiceCreatePanel';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import { SearchToolbar } from '../components/ui/SearchToolbar';
import {
  ensureNewServiceDraft,
  ensureServiceConfigDetail,
  ensureServiceFormOptions,
  useServiceConfigMutations,
  useServiceConfigsQuery,
  useServiceFormOptionsQuery,
} from '../hooks/useServiceConfigQueries';
import type { PageResult, ServiceConfig, ServiceSearchFilters } from '../types/service';

const defaultFilters: ServiceSearchFilters = {
  app: '',
  service: '',
  system: '',
  active: '',
  page: 0,
  size: 20,
};

const emptyPage: PageResult<ServiceConfig> = {
  items: [],
  totalElements: 0,
  pageNumber: 0,
  pageSize: 20,
  totalPages: 0,
  first: true,
  last: true,
  hasNext: false,
  hasPrevious: false,
};

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

  const servicesQuery = useServiceConfigsQuery(filters);
  const serviceFormOptionsQuery = useServiceFormOptionsQuery(false);
  const { saveMutation, toggleActiveMutation, toggleLogMutation } = useServiceConfigMutations(filters);

  const pageResult = servicesQuery.data ?? emptyPage;
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

  function changePageSize(size: number) {
    const nextFilters = {
      ...filters,
      size,
      page: 0,
    };

    setDraftFilters((current) => ({
      ...current,
      size,
      page: 0,
    }));
    setFilters(nextFilters);
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
      const [detail] = await Promise.all([
        ensureServiceConfigDetail(queryClient, id),
        ensureServiceFormOptions(queryClient),
      ]);

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
      const [draft] = await Promise.all([
        ensureNewServiceDraft(queryClient),
        ensureServiceFormOptions(queryClient),
      ]);

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
      await toggleActiveMutation.mutateAsync({
        id: service.id!,
        active: !Boolean(service.status),
      });
      notify.success(`Service ${service.serviceName} ${service.status ? 'disabled' : 'enabled'}.`);
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to update service status.');
    }
  }

  async function handleToggleLog(service: ServiceConfig) {
    try {
      await toggleLogMutation.mutateAsync({
        id: service.id!,
        enabled: !Boolean(service.logOn),
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
      <div className="flex min-h-0 flex-1 flex-col overflow-visible gap-4">
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
                  className="theme-input w-full px-2 py-2 text-sm border-none"
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
              <IconButton onClick={() => changePage(0)} disabled={pageResult.first} icon={navIcons.chevronsLeft} label="First page" size="sm" />
              <IconButton onClick={() => changePage(pageResult.pageNumber - 1)} disabled={!pageResult.hasPrevious} icon={navIcons.chevronLeft} label="Previous page" size="sm" />
              {visiblePages.map((page) => (
                <button
                  type="button"
                  key={page}
                  onClick={() => changePage(page)}
                  className={`rounded-xl px-3 py-2 text-sm font-semibold ${page === pageResult.pageNumber
                    ? 'theme-table-page-active'
                    : 'theme-input'
                    }`}
                >
                  {page + 1}
                </button>
              ))}
              <IconButton onClick={() => changePage(pageResult.pageNumber + 1)} disabled={!pageResult.hasNext} icon={navIcons.chevronRight} label="Next page" size="sm" />
              <IconButton onClick={() => changePage(Math.max(pageResult.totalPages - 1, 0))} disabled={pageResult.last} icon={navIcons.chevronsRight} label="Last page" size="sm" />
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
            className={`absolute inset-0 backdrop-blur-[1px] transition-all duration-200 ${isSlideOverVisible ? 'bg-slate-950/60 opacity-100' : 'bg-slate-950/0 opacity-0'
              }`}
          />
          <div
            className={`absolute right-0 top-0 h-full w-[70vw] min-w-[640px] overflow-hidden border-l border-[var(--border-subtle)] bg-[var(--surface-card-strong)] shadow-2xl transition-all duration-300 ease-out ${isSlideOverVisible ? 'translate-x-0 opacity-100' : 'translate-x-10 opacity-0'
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
