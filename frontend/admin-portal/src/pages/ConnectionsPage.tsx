import { useEffect, useMemo, useRef, useState } from 'react';
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  getEConnectorAPI,
  type ConnectionResult,
  type CreateConnectionRequest,
  type PageConnectionResult,
  type UpdateConnectionRequest,
} from '../api/connector';
import { navIcons } from '../components/layout/navIcons';
import { IconButton } from '../components/ui/IconButton';
import { SearchToolbar } from '../components/ui/SearchToolbar';
import { useNotify } from '../components/ui/NotificationProvider';
import { DEFAULT_CACHE_TIME, DEFAULT_STALE_TIME } from '../lib/queryClient';
import type {
  ConnectionPageResult,
  ConnectionRecord,
  ConnectionSearchFilters,
} from '../types/connection';

const connectorApi = getEConnectorAPI();
const CONNECTION_PAGE_SIZE = 20;

const defaultFilters: ConnectionSearchFilters = {
  keyword: '',
  connectionType: '',
  active: '',
  page: 0,
  size: CONNECTION_PAGE_SIZE,
};

const connectionTypeOptions = ['DB', 'REST', 'SOAP', 'SFTP', 'SMTP', 'KAFKA'] as const;

function safeParseJson(value?: string | null): Record<string, unknown> | null {
  if (!value) {
    return null;
  }

  try {
    const parsed = JSON.parse(value);
    return typeof parsed === 'object' && parsed !== null && !Array.isArray(parsed) ? parsed : null;
  } catch {
    return null;
  }
}

function prettyJson(value?: string | null): string {
  const parsed = safeParseJson(value);

  if (!parsed) {
    return value ?? '';
  }

  return JSON.stringify(parsed, null, 2);
}

function parseConfigText(configText: string): Record<string, unknown> | undefined {
  const trimmed = configText.trim();

  if (!trimmed) {
    return undefined;
  }

  try {
    const parsed = JSON.parse(trimmed);
    if (typeof parsed !== 'object' || parsed === null || Array.isArray(parsed)) {
      throw new Error('Configuration must be a JSON object.');
    }

    return parsed;
  } catch (error) {
    if (error instanceof Error) {
      throw new Error(`Invalid configuration JSON: ${error.message}`);
    }

    throw new Error('Invalid configuration JSON.');
  }
}

function mapConnectionResult(result: ConnectionResult): ConnectionRecord {
  return {
    id: result.id,
    connectionCode: result.connectionCode ?? '',
    connectionName: result.connectionName ?? '',
    connectionType: result.connectionType ?? '',
    configJson: result.configJson,
    configText: prettyJson(result.configJson),
    active: Boolean(result.active),
    createdAt: result.createdAt,
    updatedAt: result.updatedAt,
  };
}

function normalizeDraft(connection: ConnectionRecord): ConnectionRecord {
  return {
    ...connection,
    connectionCode: connection.connectionCode ?? '',
    connectionName: connection.connectionName ?? '',
    connectionType: connection.connectionType ?? '',
    configText: connection.configText ?? '',
    active: Boolean(connection.active),
  };
}

function matchesFilters(connection: ConnectionRecord, filters: ConnectionSearchFilters) {
  const keyword = filters.keyword.trim().toLowerCase();
  const connectionType = filters.connectionType.trim().toLowerCase();
  const haystack = [
    connection.connectionCode,
    connection.connectionName,
    connection.connectionType,
    connection.configText,
  ]
    .filter(Boolean)
    .join(' ')
    .toLowerCase();

  const matchesKeyword = !keyword || haystack.includes(keyword);
  const matchesType = !connectionType || (connection.connectionType ?? '').toLowerCase().includes(connectionType);
  const matchesActive =
    !filters.active ||
    (filters.active === 'true' && connection.active) ||
    (filters.active === 'false' && !connection.active);

  return matchesKeyword && matchesType && matchesActive;
}

function toPageResult(result: PageConnectionResult | undefined, filters: ConnectionSearchFilters): ConnectionPageResult {
  const items = (result?.content ?? []).map(mapConnectionResult).filter((item) => matchesFilters(item, filters));
  const pageNumber = result?.number ?? 0;
  const pageSize = result?.size ?? CONNECTION_PAGE_SIZE;
  const totalPages = result?.totalPages ?? 0;

  return {
    items,
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

function createBlankConnectionDraft(): ConnectionRecord {
  return normalizeDraft({
    id: undefined,
    connectionCode: '',
    connectionName: '',
    connectionType: '',
    configJson: undefined,
    configText: '{}',
    active: true,
    createdAt: undefined,
    updatedAt: undefined,
  });
}

function buildCreatePayload(connection: ConnectionRecord): CreateConnectionRequest {
  const connectionCode = connection.connectionCode?.trim() ?? '';
  const connectionName = connection.connectionName?.trim() ?? '';
  const connectionType = connection.connectionType.trim() as CreateConnectionRequest['connectionType'];

  if (!connectionCode) {
    throw new Error('Connection code is required.');
  }

  if (!connectionName) {
    throw new Error('Connection name is required.');
  }

  if (!connectionType) {
    throw new Error('Connection type is required.');
  }

  return {
    connectionCode,
    connectionName,
    connectionType,
    config: parseConfigText(connection.configText),
    active: Boolean(connection.active),
  };
}

function buildUpdatePayload(connection: ConnectionRecord): UpdateConnectionRequest {
  const connectionName = connection.connectionName?.trim() ?? '';
  const connectionType = connection.connectionType.trim() as UpdateConnectionRequest['connectionType'];

  if (!connectionName) {
    throw new Error('Connection name is required.');
  }

  if (!connectionType) {
    throw new Error('Connection type is required.');
  }

  return {
    connectionName,
    connectionType,
    config: parseConfigText(connection.configText),
    active: Boolean(connection.active),
  };
}

export function ConnectionsPage() {
  const notify = useNotify();
  const queryClient = useQueryClient();
  const slideOverTimeoutRef = useRef<number | null>(null);

  const [draftFilters, setDraftFilters] = useState<ConnectionSearchFilters>(defaultFilters);
  const [filters, setFilters] = useState<ConnectionSearchFilters>(defaultFilters);
  const [selectedConnectionId, setSelectedConnectionId] = useState<number | null>(null);
  const [selectedConnection, setSelectedConnection] = useState<ConnectionRecord | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [readOnly, setReadOnly] = useState(true);
  const [isSlideOverMounted, setIsSlideOverMounted] = useState(false);
  const [isSlideOverVisible, setIsSlideOverVisible] = useState(false);
  const [isToolbarExpanded, setIsToolbarExpanded] = useState(false);

  const connectionsQuery = useQuery({
    queryKey: ['connector', 'connections', filters.page] as const,
    queryFn: () =>
      connectorApi.findAll6({
        page: filters.page,
        size: filters.size,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
    placeholderData: keepPreviousData,
  });

  const selectedConnectionQuery = useQuery({
    queryKey: ['connector', 'connection', selectedConnectionId] as const,
    queryFn: () => connectorApi.findById6(selectedConnectionId!),
    enabled: selectedConnectionId !== null,
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const createMutation = useMutation({
    mutationFn: (connection: ConnectionRecord) => connectorApi.create6(buildCreatePayload(connection)),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['connector', 'connections'] });
      notify.success('Connection created.');
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to create connection.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, connection }: { id: number; connection: ConnectionRecord }) =>
      connectorApi.update6(id, buildUpdatePayload(connection)),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['connector', 'connections'] }),
        queryClient.invalidateQueries({ queryKey: ['connector', 'connection'] }),
      ]);
      notify.success('Connection saved.');
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to save connection.');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => connectorApi.delete6(id),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['connector', 'connections'] }),
        queryClient.invalidateQueries({ queryKey: ['connector', 'connection'] }),
      ]);
      notify.success('Connection deleted.');
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to delete connection.');
    },
  });

  useEffect(() => {
    if (!connectionsQuery.error) {
      return;
    }

    notify.error(connectionsQuery.error instanceof Error ? connectionsQuery.error.message : 'Failed to load connections.');
  }, [connectionsQuery.error, notify]);

  useEffect(() => {
    if (!selectedConnectionQuery.error) {
      return;
    }

    notify.error(
      selectedConnectionQuery.error instanceof Error
        ? selectedConnectionQuery.error.message
        : 'Failed to load connection.',
    );
    closeSlideOver();
  }, [notify, selectedConnectionQuery.error]);

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

  useEffect(() => {
    if (selectedConnectionId === null || !selectedConnectionQuery.data) {
      return;
    }

    setSelectedConnection(normalizeDraft(mapConnectionResult(selectedConnectionQuery.data)));
    setDetailLoading(false);
  }, [selectedConnectionId, selectedConnectionQuery.data]);

  const pageResult = useMemo(() => toPageResult(connectionsQuery.data, filters), [connectionsQuery.data, filters]);

  const visiblePages = useMemo(() => {
    const pages: number[] = [];
    const startPage = Math.max(0, pageResult.pageNumber - 2);
    const endPage = Math.min(Math.max(pageResult.totalPages - 1, 0), pageResult.pageNumber + 2);

    for (let currentPage = startPage; currentPage <= endPage; currentPage += 1) {
      pages.push(currentPage);
    }

    return pages;
  }, [pageResult.pageNumber, pageResult.totalPages]);

  function updateDraft<K extends keyof ConnectionSearchFilters>(key: K, value: ConnectionSearchFilters[K]) {
    setDraftFilters((current) => ({
      ...current,
      [key]: value,
    }));
  }

  function submitSearch() {
    setFilters({
      ...draftFilters,
      page: 0,
      size: CONNECTION_PAGE_SIZE,
    });
  }

  function changePage(nextPage: number) {
    setFilters((current) => ({
      ...current,
      page: Math.max(0, nextPage),
    }));
  }

  function openSlideOver() {
    if (slideOverTimeoutRef.current !== null) {
      window.clearTimeout(slideOverTimeoutRef.current);
      slideOverTimeoutRef.current = null;
    }

    setIsSlideOverMounted(true);
    window.requestAnimationFrame(() => {
      setIsSlideOverVisible(true);
    });
  }

  function closeSlideOver() {
    setIsSlideOverVisible(false);

    if (slideOverTimeoutRef.current !== null) {
      window.clearTimeout(slideOverTimeoutRef.current);
    }

    slideOverTimeoutRef.current = window.setTimeout(() => {
      setIsSlideOverMounted(false);
      setSelectedConnectionId(null);
      setSelectedConnection(null);
      setDetailLoading(false);
      setSaving(false);
      setReadOnly(true);
      slideOverTimeoutRef.current = null;
    }, 280);
  }

  async function openConnection(id: number) {
    setSaving(false);
    setReadOnly(true);
    setDetailLoading(true);
    setSelectedConnection(null);
    setSelectedConnectionId(id);
    openSlideOver();
  }

  async function openNewConnection() {
    setSaving(false);
    setReadOnly(false);
    setDetailLoading(false);
    setSelectedConnectionId(null);
    setSelectedConnection(createBlankConnectionDraft());
    openSlideOver();
  }

  async function handleSave() {
    if (!selectedConnection) {
      return;
    }

    setSaving(true);

    try {
      if (selectedConnection.id) {
        await updateMutation.mutateAsync({ id: selectedConnection.id, connection: selectedConnection });
        setReadOnly(true);
      } else {
        await createMutation.mutateAsync(selectedConnection);
        closeSlideOver();
        setFilters((current) => ({ ...current, page: 0 }));
      }
    } catch {
      // Mutation handlers already surface errors.
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(connection: ConnectionRecord) {
    if (!connection.id) {
      notify.error('Connection id is missing.');
      return;
    }

    if (!window.confirm(`Delete connection ${connection.connectionName || connection.connectionCode}?`)) {
      return;
    }

    try {
      await deleteMutation.mutateAsync(connection.id);
      if (selectedConnectionId === connection.id) {
        closeSlideOver();
      }
    } catch {
      // Mutation handlers already surface errors.
    }
  }

  return (
    <div className="flex h-full min-h-0 flex-col gap-3 overflow-hidden p-4">
      <div className="flex min-h-0 flex-1 flex-col gap-4 overflow-visible">
        <div className="flex items-start justify-between gap-3 py-4">
          <form
            className="min-w-0"
            onSubmit={(event) => {
              event.preventDefault();
              submitSearch();
            }}
          >
            <SearchToolbar
              onSearch={submitSearch}
              onExpandedChange={setIsToolbarExpanded}
              collapsedWidthClassName="w-full max-w-md"
              expandedWidthClassName="w-full max-w-5xl"
              primary={
                <input
                  value={draftFilters.keyword}
                  onChange={(event) => updateDraft('keyword', event.target.value)}
                  className="theme-input w-full border-none px-2 py-2 text-sm"
                  placeholder="Search code, name, type, or config..."
                />
              }
              advanced={
                <>
                  <select
                    value={draftFilters.connectionType}
                    onChange={(event) => updateDraft('connectionType', event.target.value)}
                    className="theme-input min-w-[180px] border-none"
                  >
                    <option value="">All types</option>
                    {connectionTypeOptions.map((type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                    ))}
                  </select>
                  <select
                    value={draftFilters.active}
                    onChange={(event) => updateDraft('active', event.target.value as ConnectionSearchFilters['active'])}
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
              onClick={() => void openNewConnection()}
              icon={navIcons.plus}
              label="Add connection"
              tone="success"
              size="sm"
            />
          </div>
        </div>

        <div className="min-h-0 flex-1 overflow-y-auto pr-1">
          {connectionsQuery.isLoading ? (
            <div className="flex min-h-[320px] items-center justify-center text-sm theme-muted-text">
              Loading connections...
            </div>
          ) : pageResult.items.length === 0 ? (
            <div className="flex min-h-[320px] items-center justify-center text-sm theme-muted-text">
              No connection definitions found.
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-4 xl:grid-cols-2">
              {pageResult.items.map((connection, index) => (
                <article
                  key={connection.id ?? `${connection.connectionCode}-${index}`}
                  className="rounded-2xl border border-black/5 bg-white/70 p-4 shadow-sm backdrop-blur transition hover:-translate-y-0.5 hover:shadow-md dark:border-white/10 dark:bg-slate-900/60"
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <p className="theme-muted-text text-xs uppercase tracking-[0.18em]">Connection</p>
                      <h3 className="mt-1 truncate text-lg font-semibold theme-strong-text">
                        {connection.connectionName || connection.connectionCode || 'Untitled Connection'}
                      </h3>
                      <p className="theme-muted-text mt-1 text-sm">
                        {connection.connectionCode || '-'} / {connection.connectionType || '-'}
                      </p>
                    </div>

                    <span
                      className={`rounded-full px-3 py-1 text-xs font-semibold ${
                        connection.active
                          ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-300'
                          : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300'
                      }`}
                    >
                      {connection.active ? 'Active' : 'Inactive'}
                    </span>
                  </div>

                  <div className="mt-4 grid gap-2 text-sm">
                    <div className="flex items-start justify-between gap-3">
                      <span className="theme-muted-text">Code</span>
                      <span className="theme-strong-text text-right">{connection.connectionCode || '-'}</span>
                    </div>
                    <div className="flex items-start justify-between gap-3">
                      <span className="theme-muted-text">Type</span>
                      <span className="theme-strong-text text-right">{connection.connectionType || '-'}</span>
                    </div>
                    <div className="flex items-start justify-between gap-3">
                      <span className="theme-muted-text">Updated</span>
                      <span className="theme-strong-text text-right">{connection.updatedAt || connection.createdAt || '-'}</span>
                    </div>
                  </div>

                  <p className="theme-muted-text mt-4 line-clamp-3 text-sm">
                    {connection.configText ? connection.configText : 'No configuration provided.'}
                  </p>

                  <div className="mt-5 flex flex-wrap items-center justify-between gap-3">
                    <div className="flex items-center gap-2">
                      <button
                        type="button"
                        onClick={() => void openConnection(connection.id!)}
                        className="rounded-xl border border-[var(--border-subtle)] px-3 py-2 text-sm font-medium theme-strong-text transition hover:bg-[var(--surface-input)]"
                      >
                        View
                      </button>
                      <button
                        type="button"
                        onClick={() => void openConnection(connection.id!)}
                        className="rounded-xl border border-[var(--border-subtle)] px-3 py-2 text-sm font-medium theme-strong-text transition hover:bg-[var(--surface-input)]"
                      >
                        Edit
                      </button>
                      <button
                        type="button"
                        onClick={() => void handleDelete(connection)}
                        className="rounded-xl border border-rose-200 px-3 py-2 text-sm font-medium text-rose-700 transition hover:bg-rose-50 dark:border-rose-500/30 dark:text-rose-300 dark:hover:bg-rose-500/10"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                </article>
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
            aria-label="Close connection panel"
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
                {selectedConnection?.id ? 'Connection' : 'Add Connection'}
              </h2>
              <IconButton onClick={closeSlideOver} icon={navIcons.close} label="Close connection panel" size="sm" />
            </div>

            <div className="h-[calc(100%-61px)] overflow-y-auto p-4">
              {detailLoading ? (
                <div className="flex min-h-[320px] items-center justify-center text-sm theme-muted-text">
                  Loading connection...
                </div>
              ) : selectedConnection ? (
                <div className="space-y-4">
                  <div className="grid gap-4 md:grid-cols-2">
                    <label className="grid gap-2">
                      <span className="text-sm font-medium theme-strong-text">Connection Code</span>
                      <input
                        value={selectedConnection.connectionCode}
                        onChange={(event) =>
                          setSelectedConnection((current) =>
                            current ? { ...current, connectionCode: event.target.value } : current,
                          )
                        }
                        disabled={Boolean(selectedConnection.id) || readOnly}
                        className="theme-input"
                        placeholder="CONN-001"
                      />
                    </label>
                    <label className="grid gap-2">
                      <span className="text-sm font-medium theme-strong-text">Connection Name</span>
                      <input
                        value={selectedConnection.connectionName}
                        onChange={(event) =>
                          setSelectedConnection((current) =>
                            current ? { ...current, connectionName: event.target.value } : current,
                          )
                        }
                        disabled={readOnly}
                        className="theme-input"
                        placeholder="Primary database"
                      />
                    </label>
                  </div>

                  <div className="grid gap-4 md:grid-cols-2">
                    <label className="grid gap-2">
                      <span className="text-sm font-medium theme-strong-text">Connection Type</span>
                      <select
                        value={selectedConnection.connectionType}
                        onChange={(event) =>
                          setSelectedConnection((current) =>
                            current ? { ...current, connectionType: event.target.value } : current,
                          )
                        }
                        disabled={readOnly}
                        className="theme-input"
                      >
                        <option value="">Select a type</option>
                        {connectionTypeOptions.map((type) => (
                          <option key={type} value={type}>
                            {type}
                          </option>
                        ))}
                      </select>
                    </label>

                    <label className="flex items-end gap-2 rounded-2xl border border-[var(--border-subtle)] px-4 py-3">
                      <input
                        type="checkbox"
                        checked={selectedConnection.active}
                        onChange={(event) =>
                          setSelectedConnection((current) =>
                            current ? { ...current, active: event.target.checked } : current,
                          )
                        }
                        disabled={readOnly}
                        className="h-4 w-4"
                      />
                      <span className="text-sm font-medium theme-strong-text">Active</span>
                    </label>
                  </div>

                  <label className="grid gap-2">
                    <span className="text-sm font-medium theme-strong-text">Configuration JSON</span>
                    <textarea
                      value={selectedConnection.configText}
                      onChange={(event) =>
                        setSelectedConnection((current) =>
                          current ? { ...current, configText: event.target.value } : current,
                        )
                      }
                      disabled={readOnly}
                      className="theme-input min-h-[320px] font-mono text-sm"
                      placeholder='{"host":"localhost","port":5432}'
                    />
                  </label>

                  <div className="grid gap-3 rounded-2xl border border-[var(--border-subtle)] bg-[var(--surface-input)] p-4 text-sm md:grid-cols-2">
                    <div className="flex items-start justify-between gap-3">
                      <span className="theme-muted-text">Created</span>
                      <span className="theme-strong-text text-right">{selectedConnection.createdAt || '-'}</span>
                    </div>
                    <div className="flex items-start justify-between gap-3">
                      <span className="theme-muted-text">Updated</span>
                      <span className="theme-strong-text text-right">{selectedConnection.updatedAt || '-'}</span>
                    </div>
                  </div>

                  <div className="flex flex-wrap items-center justify-between gap-3 pt-2">
                    <div className="flex items-center gap-2">
                      {selectedConnection.id && readOnly ? (
                        <button
                          type="button"
                          onClick={() => setReadOnly(false)}
                          className="rounded-xl border border-[var(--border-subtle)] px-3 py-2 text-sm font-medium theme-strong-text transition hover:bg-[var(--surface-input)]"
                        >
                          Edit
                        </button>
                      ) : null}
                      <button
                        type="button"
                        onClick={closeSlideOver}
                        className="rounded-xl border border-[var(--border-subtle)] px-3 py-2 text-sm font-medium theme-strong-text transition hover:bg-[var(--surface-input)]"
                      >
                        Cancel
                      </button>
                    </div>

                    <button
                      type="button"
                      onClick={() => void handleSave()}
                      disabled={saving || readOnly}
                      className="rounded-xl bg-brand-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-600 disabled:cursor-not-allowed disabled:opacity-60"
                    >
                      {saving ? 'Saving...' : selectedConnection.id ? 'Save changes' : 'Create connection'}
                    </button>
                  </div>
                </div>
              ) : null}
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
