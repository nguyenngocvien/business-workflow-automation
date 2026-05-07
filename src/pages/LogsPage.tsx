import { useEffect, useRef, useState } from 'react';
import { navIcons } from '../components/layout/navIcons';
import { LogDetailPanel } from '../components/logger/LogDetailPanel';
import { LogStatusCell } from '../components/logger/LogStatusCell';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import { SearchToolbar } from '../components/ui/SearchToolbar';
import { fetchLogDetail, fetchLogs } from '../services/logApi';
import type { LogPageResult, LogRecord, LogSearchFilters } from '../types/log';

const defaultFilters: LogSearchFilters = {
  service: '',
  caseId: '',
  logId: '',
  from: '',
  to: '',
  status: '',
  page: 0,
  size: 20,
};

const emptyPage: LogPageResult = {
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

const statusClasses = (log: any) => {
  const isError = log.errorCode;

  return isError
    ? `
      border-[rgba(var(--color-error),0.2)]
      bg-[rgba(var(--color-error),0.08)]
      hover:bg-[rgba(var(--color-error),0.12)]
    `
    : `
      border-[rgba(var(--color-success),0.2)]
      bg-[rgba(var(--color-success),0.08)]
      hover:bg-[rgba(var(--color-success),0.12)]
    `;
};

function timeAgo(date: string | number | Date) {
  const now = new Date().getTime();
  const input = new Date(date).getTime();
  const diff = Math.floor((now - input) / 1000); // seconds

  if (diff < 60) return `${diff}s ago`;
  if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;

  return `${Math.floor(diff / 86400)}d ago`;
}

export function LogsPage() {
  const slideOverTimeoutRef = useRef<number | null>(null);
  const [draftFilters, setDraftFilters] = useState<LogSearchFilters>(defaultFilters);
  const [filters, setFilters] = useState<LogSearchFilters>(defaultFilters);
  const [pageResult, setPageResult] = useState<LogPageResult>(emptyPage);
  const [loading, setLoading] = useState(true);
  const [selectedLog, setSelectedLog] = useState<LogRecord | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [isSlideOverMounted, setIsSlideOverMounted] = useState(false);
  const [isSlideOverVisible, setIsSlideOverVisible] = useState(false);
  const notify = useNotify();

  useEffect(() => {
    let active = true;
    setLoading(true);

    void fetchLogs(filters)
      .then((response) => {
        if (!active) {
          return;
        }

        setPageResult(response);
      })
      .catch((requestError) => {
        if (!active) {
          return;
        }
        notify.error(requestError instanceof Error ? requestError.message : 'Failed to load logs.');
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, [filters, notify]);

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

  function updateDraft<K extends keyof LogSearchFilters>(key: K, value: LogSearchFilters[K]) {
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

  async function openDetail(id: number) {
    setDetailLoading(true);
    if (slideOverTimeoutRef.current !== null) {
      window.clearTimeout(slideOverTimeoutRef.current);
      slideOverTimeoutRef.current = null;
    }
    setIsSlideOverMounted(true);
    window.requestAnimationFrame(() => {
      setIsSlideOverVisible(true);
    });

    try {
      const detail = await fetchLogDetail(id);
      setSelectedLog(detail);
    } catch (detailError) {
      setSelectedLog(null);
      notify.error(detailError instanceof Error ? detailError.message : 'Failed to load log detail.');
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
      setSelectedLog(null);
      setDetailLoading(false);
      slideOverTimeoutRef.current = null;
    }, 280);
  }

  const visiblePages: number[] = [];
  const startPage = Math.max(0, pageResult.pageNumber - 2);
  const endPage = Math.min(Math.max(pageResult.totalPages - 1, 0), pageResult.pageNumber + 2);

  for (let page = startPage; page <= endPage; page += 1) {
    visiblePages.push(page);
  }

  const exportUrl = new URL('/e-connector/logs/export', window.location.origin);
  if (filters.service) exportUrl.searchParams.set('service', filters.service);
  if (filters.caseId) exportUrl.searchParams.set('caseId', filters.caseId);
  if (filters.logId) exportUrl.searchParams.set('logId', filters.logId);
  if (filters.from) exportUrl.searchParams.set('from', filters.from);
  if (filters.to) exportUrl.searchParams.set('to', filters.to);
  if (filters.status) exportUrl.searchParams.set('status', filters.status);

  return (
    <div className="p-4 flex h-full min-h-0 flex-col gap-4 overflow-hidden">
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
                className="theme-input w-full px-2 py-2 text-sm border-none"
                placeholder="Service Code..."
              />
            }
            advanced={
              <>
                <input
                  value={draftFilters.caseId}
                  onChange={(event) => updateDraft('caseId', event.target.value)}
                  className="theme-input min-w-[160px] border-none"
                  placeholder="Case ID..."
                />
                <input
                  value={draftFilters.logId}
                  onChange={(event) => updateDraft('logId', event.target.value)}
                  className="theme-input min-w-[160px] border-none"
                  placeholder="Log ID..."
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

      <div className="flex flex-col gap-3">

        {loading ? (
          <div className="text-center py-16 text-sm text-[var(--text-muted)]">
            Loading logs...
          </div>
        ) : pageResult.items.length === 0 ? (
          <div className="text-center py-16 text-sm text-[var(--text-muted)]">
            No logs found.
          </div>
        ) : (
          pageResult.items.map((log) => {
            const isError = log.errorCode;

            return (
              <div
                key={log.id}
                onClick={() => void openDetail(log.id)}
                className={`
            flex items-center justify-between gap-4
            rounded-2xl px-4 py-3
            cursor-pointer transition
            ${statusClasses(log)}
          `}
              >
                {/* LEFT */}
                <div className="flex min-w-0 flex-col gap-1">

                  {/* Row 1 */}
                  <div className="flex items-center gap-2">
                    <LogStatusCell
                      errorCode={log.errorCode}
                      errorMessage={log.errorMessage}
                    />

                    <span className="font-semibold truncate">
                      {log.service}
                    </span>

                    <span className="text-xs text-[var(--text-muted)]">
                      #{log.id}
                    </span>
                  </div>

                  {/* Row 2 */}
                  <div className="flex flex-wrap gap-2 text-xs text-[var(--text-muted)]">
                    <span>Case: {log.caseId || '-'}</span>
                    <span>•</span>
                    <span>Target: {log.system || '-'}</span>
                  </div>
                </div>

                {/* RIGHT */}
                <div className="flex flex-col items-end text-xs text-[var(--text-muted)] shrink-0">
                  <span>{timeAgo(log.createdDate || '-')}</span>
                  <span className="opacity-70">
                    {formatDate(log.createdDate)}
                  </span>
                </div>
              </div>
            );
          })
        )}
      </div>

      {
    isSlideOverMounted ? (
      <div className="fixed inset-0 z-40">
        <button
          type="button"
          aria-label="Close log detail panel"
          onClick={closeSlideOver}
          className={`absolute inset-0 backdrop-blur-[1px] transition-all duration-200 ${isSlideOverVisible ? 'bg-slate-950/60 opacity-100' : 'bg-slate-950/0 opacity-0'
            }`}
        />
        <div
          className={`absolute right-0 top-0 h-full w-1/2 min-w-[640px] overflow-hidden border-l border-[var(--border-subtle)] bg-[var(--surface-card-strong)] shadow-2xl transition-all duration-300 ease-out ${isSlideOverVisible ? 'translate-x-0 opacity-100' : 'translate-x-10 opacity-0'
            }`}
        >
          <div className="theme-table-divider flex items-center justify-between p-4">
            <h2 className="theme-strong-text text-lg font-semibold uppercase tracking-[0.18em]">
              Log Detail
            </h2>
            <IconButton onClick={closeSlideOver} icon={navIcons.close} label="Close log detail panel" size="sm" />
          </div>
          <div className="h-[calc(100%-61px)] overflow-y-auto p-4 sm:p-6">
            <LogDetailPanel log={selectedLog} loading={detailLoading} />
          </div>
        </div>
      </div>
    ) : null
  }
    </div >
  );
}
