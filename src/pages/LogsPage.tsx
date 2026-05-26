import { useEffect, useMemo, useRef, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getEConnectorAPI, type LogResult, type PageLogResult } from '../api/connector';
import { navIcons } from '../components/layout/navIcons';
import { LogDetailPanel } from '../components/logger/LogDetailPanel';
import { LogStatusCell } from '../components/logger/LogStatusCell';
import { IconButton } from '../components/ui/IconButton';
import { SearchToolbar } from '../components/ui/SearchToolbar';
import { useNotify } from '../components/ui/NotificationProvider';
import { DEFAULT_CACHE_TIME, DEFAULT_STALE_TIME } from '../lib/queryClient';
import type { LogPageResult, LogRecord, LogSearchFilters } from '../types/log';

const connectorApi = getEConnectorAPI();
const LOG_PAGE_SIZE = 20;

const defaultFilters: LogSearchFilters = {
  service: '',
  caseId: '',
  logId: '',
  from: '',
  to: '',
  status: '',
  page: 0,
  size: LOG_PAGE_SIZE,
};

const emptyConnectorPage: PageLogResult = {
  totalPages: 0,
  totalElements: 0,
  last: true,
  first: true,
  size: LOG_PAGE_SIZE,
  content: [],
  number: 0,
  numberOfElements: 0,
  empty: true,
};

function formatDate(value?: string) {
  if (!value) {
    return '-';
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat('en-GB', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  }).format(date);
}

function timeAgo(date: string | number | Date) {
  const now = new Date().getTime();
  const input = new Date(date).getTime();

  if (Number.isNaN(input)) {
    return '-';
  }

  const diff = Math.floor((now - input) / 1000);

  if (diff < 60) return `${diff}s ago`;
  if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;

  return `${Math.floor(diff / 86400)}d ago`;
}

function isSuccessStatus(statusCode?: number) {
  return typeof statusCode === 'number' ? statusCode === 0 || (statusCode >= 200 && statusCode < 400) : true;
}

function mapLogResult(log: LogResult): LogRecord {
  const createdDate = log.createdAt ?? log.requestTime ?? log.responseTime ?? undefined;

  return {
    id: log.id ?? 0,
    service: log.serviceId !== undefined ? `Service #${log.serviceId}` : 'Service -',
    fromInput: log.requestHeaders,
    fromOutput: log.requestAfterTransform,
    toInput: log.requestBody,
    toOutput: log.responseAfterTransform ?? log.responseBody,
    errorCode: log.statusCode !== undefined ? (isSuccessStatus(log.statusCode) ? '0' : String(log.statusCode)) : undefined,
    errorMessage: log.errorMessage ?? (log.statusCode !== undefined ? `HTTP ${log.statusCode}` : undefined),
    stacktrace: log.stacktrace,
    logCode: log.traceId ?? log.correlationId,
    caseId: log.correlationId ?? log.traceId,
    timing: log.durationMs !== undefined ? `${log.durationMs} ms` : undefined,
    system: log.serviceId !== undefined ? `Service ${log.serviceId}` : undefined,
    createdDate,
  };
}

function inDateRange(value?: string, from?: string, to?: string) {
  if (!from && !to) {
    return true;
  }

  const timestamp = value ? new Date(value).getTime() : Number.NaN;
  if (Number.isNaN(timestamp)) {
    return false;
  }

  if (from) {
    const fromTime = new Date(from).getTime();
    if (!Number.isNaN(fromTime) && timestamp < fromTime) {
      return false;
    }
  }

  if (to) {
    const toTime = new Date(to).getTime();
    if (!Number.isNaN(toTime) && timestamp > toTime) {
      return false;
    }
  }

  return true;
}

function matchesFilters(log: LogRecord, filters: LogSearchFilters) {
  const searchService = filters.service.trim().toLowerCase();
  const searchCaseId = filters.caseId.trim().toLowerCase();
  const searchLogId = filters.logId.trim().toLowerCase();
  const matchesService = !searchService || log.service.toLowerCase().includes(searchService);
  const matchesCaseId = !searchCaseId || (log.caseId ?? '').toLowerCase().includes(searchCaseId);
  const matchesLogId =
    !searchLogId ||
    String(log.id).includes(searchLogId) ||
    (log.logCode ?? '').toLowerCase().includes(searchLogId);
  const matchesStatus =
    !filters.status ||
    (filters.status === 'true' && isSuccessStatus(Number(log.errorCode))) ||
    (filters.status === 'false' && !isSuccessStatus(Number(log.errorCode)));
  const matchesDateRange = inDateRange(log.createdDate, filters.from, filters.to);

  return matchesService && matchesCaseId && matchesLogId && matchesStatus && matchesDateRange;
}

export function LogsPage() {
  const notify = useNotify();
  const slideOverTimeoutRef = useRef<number | null>(null);

  const [draftFilters, setDraftFilters] = useState<LogSearchFilters>(defaultFilters);
  const [filters, setFilters] = useState<LogSearchFilters>(defaultFilters);
  const [page, setPage] = useState(0);
  const [selectedLogId, setSelectedLogId] = useState<number | null>(null);
  const [isSlideOverMounted, setIsSlideOverMounted] = useState(false);
  const [isSlideOverVisible, setIsSlideOverVisible] = useState(false);

  const logsQuery = useQuery({
    queryKey: ['connector', 'logs', page, LOG_PAGE_SIZE] as const,
    queryFn: () =>
      connectorApi.findAll7({
        page,
        size: LOG_PAGE_SIZE,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const selectedLogQuery = useQuery({
    queryKey: ['connector', 'log', selectedLogId] as const,
    queryFn: () => connectorApi.findById7(selectedLogId!),
    enabled: selectedLogId !== null,
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  useEffect(() => {
    if (!logsQuery.error) {
      return;
    }

    notify.error(logsQuery.error instanceof Error ? logsQuery.error.message : 'Failed to load logs.');
  }, [logsQuery.error, notify]);

  useEffect(() => {
    if (!selectedLogQuery.error) {
      return;
    }

    notify.error(selectedLogQuery.error instanceof Error ? selectedLogQuery.error.message : 'Failed to load log detail.');
    closeSlideOver();
  }, [notify, selectedLogQuery.error]);

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

  const pageResult = useMemo<LogPageResult>(() => {
    const source = logsQuery.data ?? emptyConnectorPage;
    const items = (source.content ?? []).map(mapLogResult).filter((log) => matchesFilters(log, filters));

    return {
      items,
      totalElements: source.totalElements ?? 0,
      pageNumber: source.number ?? 0,
      pageSize: source.size ?? LOG_PAGE_SIZE,
      totalPages: source.totalPages ?? 0,
      first: Boolean(source.first),
      last: Boolean(source.last),
      hasNext: !source.last && (source.number ?? 0) + 1 < (source.totalPages ?? 0),
      hasPrevious: !source.first && (source.number ?? 0) > 0,
    };
  }, [filters, logsQuery.data]);

  function updateDraft<K extends keyof LogSearchFilters>(key: K, value: LogSearchFilters[K]) {
    setDraftFilters((current) => ({
      ...current,
      [key]: value,
    }));
  }

  function submitSearch() {
    setPage(0);
    setFilters({
      ...draftFilters,
      page: 0,
      size: LOG_PAGE_SIZE,
    });
  }

  async function openDetail(id: number) {
    if (slideOverTimeoutRef.current !== null) {
      window.clearTimeout(slideOverTimeoutRef.current);
      slideOverTimeoutRef.current = null;
    }

    setSelectedLogId(id);
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
      setSelectedLogId(null);
      slideOverTimeoutRef.current = null;
    }, 280);
  }

  const visiblePages: number[] = [];
  const startPage = Math.max(0, pageResult.pageNumber - 2);
  const endPage = Math.min(Math.max(pageResult.totalPages - 1, 0), pageResult.pageNumber + 2);

  for (let currentPage = startPage; currentPage <= endPage; currentPage += 1) {
    visiblePages.push(currentPage);
  }

  return (
    <div className="flex h-full min-h-0 flex-col gap-4 overflow-hidden p-4">
      <div className="flex items-start justify-between gap-3 py-2">
        <form
          className="min-w-0"
          onSubmit={(event) => {
            event.preventDefault();
            submitSearch();
          }}
        >
          <SearchToolbar
            onSearch={submitSearch}
            collapsedWidthClassName="w-full max-w-md"
            expandedWidthClassName="w-full max-w-6xl"
            primary={
              <input
                value={draftFilters.service}
                onChange={(event) => updateDraft('service', event.target.value)}
                onKeyDown={(event) => {
                  if (event.key === 'Enter') {
                    event.preventDefault();
                    submitSearch();
                  }
                }}
                className="theme-input w-full border-none px-2 py-2 text-sm"
                placeholder="Service..."
              />
            }
            advanced={
              <>
                <input
                  value={draftFilters.caseId}
                  onChange={(event) => updateDraft('caseId', event.target.value)}
                  className="theme-input min-w-[160px] border-none"
                  placeholder="Correlation ID..."
                />
                <input
                  value={draftFilters.logId}
                  onChange={(event) => updateDraft('logId', event.target.value)}
                  className="theme-input min-w-[160px] border-none"
                  placeholder="Trace ID..."
                />
                <select
                  value={draftFilters.status}
                  onChange={(event) => updateDraft('status', event.target.value as LogSearchFilters['status'])}
                  className="theme-input min-w-[150px] border-none"
                >
                  <option value="">All</option>
                  <option value="true">Success</option>
                  <option value="false">Failed</option>
                </select>
                <input
                  type="datetime-local"
                  value={draftFilters.from}
                  onChange={(event) => updateDraft('from', event.target.value)}
                  className="theme-input min-w-[220px] border-none"
                />
                <input
                  type="datetime-local"
                  value={draftFilters.to}
                  onChange={(event) => updateDraft('to', event.target.value)}
                  className="theme-input min-w-[220px] border-none"
                />
              </>
            }
          />
        </form>
      </div>

      <div className="flex min-h-0 flex-1 flex-col gap-3 overflow-hidden">
        {logsQuery.isLoading ? (
          <div className="flex min-h-[280px] items-center justify-center text-sm text-[var(--text-muted)]">
            Loading logs...
          </div>
        ) : pageResult.items.length === 0 ? (
          <div className="flex min-h-[280px] items-center justify-center text-sm text-[var(--text-muted)]">
            No logs found.
          </div>
        ) : (
          <div className="space-y-3 overflow-auto pr-1">
            {pageResult.items.map((log) => (
                <button
                  key={log.id}
                  type="button"
                  onClick={() => void openDetail(log.id)}
                  className={`
                  flex w-full items-center justify-between gap-4 rounded-2xl px-4 py-3 text-left transition
                  ${log.errorCode ? 'border border-rose-200 bg-rose-50 hover:bg-rose-100' : 'border border-emerald-200 bg-emerald-50 hover:bg-emerald-100'}
                  `}
                >
                <div className="flex min-w-0 flex-col gap-1">
                  <div className="flex items-center gap-2">
                    <LogStatusCell errorCode={log.errorCode} errorMessage={log.errorMessage} />
                    <span className="truncate font-semibold">{log.service}</span>
                    <span className="text-xs text-[var(--text-muted)]">#{log.id}</span>
                  </div>

                  <div className="flex flex-wrap gap-2 text-xs text-[var(--text-muted)]">
                    <span>Correlation: {log.caseId || '-'}</span>
                    <span>-</span>
                    <span>Trace: {log.logCode || '-'}</span>
                    <span>-</span>
                    <span>Timing: {log.timing || '-'}</span>
                  </div>
                </div>

                <div className="flex shrink-0 flex-col items-end text-xs text-[var(--text-muted)]">
                  <span>{timeAgo(log.createdDate || '-')}</span>
                  <span className="opacity-70">{formatDate(log.createdDate)}</span>
                </div>
              </button>
            ))}
          </div>
        )}
      </div>

      {pageResult.totalPages > 0 ? (
        <div className="flex items-center justify-between gap-3 rounded-2xl border border-[var(--border-subtle)] px-4 py-3">
          <div className="text-sm text-[var(--text-muted)]">
            Page <span className="font-semibold text-[var(--text-strong)]">{pageResult.pageNumber + 1}</span> of{' '}
            <span>{Math.max(pageResult.totalPages, 1)}</span> - {pageResult.totalElements} total logs
          </div>

          <div className="flex items-center gap-2">
            <IconButton
              onClick={() => setPage(0)}
              disabled={!pageResult.hasPrevious}
              icon={navIcons.chevronsLeft}
              label="First page"
              size="sm"
            />
            <IconButton
              onClick={() => setPage((current) => Math.max(0, current - 1))}
              disabled={!pageResult.hasPrevious}
              icon={navIcons.chevronLeft}
              label="Previous page"
              size="sm"
            />

            <div className="hidden items-center gap-1 md:flex">
              {visiblePages.map((pageNumber) => (
                <button
                  key={pageNumber}
                  type="button"
                  onClick={() => setPage(pageNumber)}
                  className={`rounded-xl px-3 py-2 text-xs font-semibold transition ${
                    pageNumber === pageResult.pageNumber
                      ? 'bg-brand-500 text-white'
                      : 'theme-surface text-slate-600 hover:bg-[var(--surface-input)]'
                  }`}
                >
                  {pageNumber + 1}
                </button>
              ))}
            </div>

            <IconButton
              onClick={() => setPage((current) => current + 1)}
              disabled={!pageResult.hasNext}
              icon={navIcons.chevronRight}
              label="Next page"
              size="sm"
            />
          </div>
        </div>
      ) : null}

      {isSlideOverMounted ? (
        <div className="fixed inset-0 z-40">
          <button
            type="button"
            aria-label="Close log detail panel"
            onClick={closeSlideOver}
            className={`absolute inset-0 backdrop-blur-[1px] transition-all duration-200 ${
              isSlideOverVisible ? 'bg-slate-950/60 opacity-100' : 'bg-slate-950/0 opacity-0'
            }`}
          />

          <div
            className={`absolute right-0 top-0 h-full w-full overflow-hidden border-l border-[var(--border-subtle)] bg-[var(--surface-card-strong)] shadow-2xl transition-all duration-300 ease-out sm:w-1/2 sm:min-w-[640px] ${
              isSlideOverVisible ? 'translate-x-0 opacity-100' : 'translate-x-10 opacity-0'
            }`}
          >
            <div className="theme-table-divider flex items-center justify-between p-4">
              <h2 className="theme-strong-text text-lg font-semibold uppercase tracking-[0.18em]">Log Detail</h2>
              <IconButton onClick={closeSlideOver} icon={navIcons.close} label="Close log detail panel" size="sm" />
            </div>
            <div className="h-[calc(100%-61px)] overflow-y-auto p-4 sm:p-6">
              <LogDetailPanel log={selectedLogQuery.data ?? null} loading={selectedLogQuery.isFetching} />
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
