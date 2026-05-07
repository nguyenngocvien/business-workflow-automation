import { useEffect, useRef, useState } from 'react';
import { navIcons } from '../components/layout/navIcons';
import { EmailTemplateCard } from '../components/email/EmailTemplateCard';
import { EmailTemplateEditor } from '../components/email/EmailTemplateEditor';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import { SearchToolbar } from '../components/ui/SearchToolbar';
import {
  fetchEmailTemplate,
  fetchEmailTemplates,
  fetchNewEmailTemplate,
  saveEmailTemplate,
  updateEmailTemplateStatus,
} from '../services/emailTemplateApi';
import type {
  EmailTemplatePageResult,
  EmailTemplateRecord,
  EmailTemplateSearchFilters,
} from '../types/emailTemplate';

const defaultFilters: EmailTemplateSearchFilters = {
  keyword: '',
  processCode: '',
  templateType: '',
  active: '',
  page: 0,
  size: 20,
};

const emptyPage: EmailTemplatePageResult = {
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

export function EmailTemplatesPage() {
  const slideOverTimeoutRef = useRef<number | null>(null);
  const [draftFilters, setDraftFilters] = useState<EmailTemplateSearchFilters>(defaultFilters);
  const [filters, setFilters] = useState<EmailTemplateSearchFilters>(defaultFilters);
  const [pageResult, setPageResult] = useState<EmailTemplatePageResult>(emptyPage);
  const [loading, setLoading] = useState(true);
  const [selectedTemplate, setSelectedTemplate] = useState<EmailTemplateRecord | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [readOnly, setReadOnly] = useState(true);
  const [isSlideOverMounted, setIsSlideOverMounted] = useState(false);
  const [isSlideOverVisible, setIsSlideOverVisible] = useState(false);
  const [isToolbarExpanded, setIsToolbarExpanded] = useState(false);
  const notify = useNotify();

  useEffect(() => {
    let active = true;
    setLoading(true);

    void fetchEmailTemplates(filters)
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
        notify.error(requestError instanceof Error ? requestError.message : 'Failed to load email templates.');
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

  function updateDraft<K extends keyof EmailTemplateSearchFilters>(key: K, value: EmailTemplateSearchFilters[K]) {
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
    setDraftFilters((current) => ({
      ...current,
      size,
      page: 0,
    }));
    setFilters((current) => ({
      ...current,
      size,
      page: 0,
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
      setSelectedTemplate(null);
      setDetailLoading(false);
      setSaving(false);
      setReadOnly(true);
      slideOverTimeoutRef.current = null;
    }, 280);
  }

  async function openTemplate(id: number) {
    setDetailLoading(true);
    setReadOnly(true);
    openSlideOver();
    try {
      setSelectedTemplate(await fetchEmailTemplate(id));
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to load email template.');
      closeSlideOver();
    } finally {
      setDetailLoading(false);
    }
  }

  async function openNewTemplate() {
    setDetailLoading(true);
    setReadOnly(false);
    openSlideOver();
    try {
      setSelectedTemplate(await fetchNewEmailTemplate());
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to load email template.');
      closeSlideOver();
    } finally {
      setDetailLoading(false);
    }
  }

  async function handleSave() {
    if (!selectedTemplate) {
      return;
    }

    setSaving(true);
    try {
      await saveEmailTemplate(selectedTemplate);
      notify.success('Email template saved.');
      setFilters((current) => ({ ...current }));
      if (selectedTemplate.id) {
        setReadOnly(true);
      } else {
        closeSlideOver();
      }
    } catch (saveError) {
      notify.error(saveError instanceof Error ? saveError.message : 'Failed to save template.');
    } finally {
      setSaving(false);
    }
  }

  async function toggleStatus(template: EmailTemplateRecord) {
    if (!template.id) {
      return;
    }

    try {
      await updateEmailTemplateStatus(template.id, !template.active);
      notify.success(`${template.templateCode} ${template.active ? 'disabled' : 'enabled'}.`);
      setFilters((current) => ({ ...current }));
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to update template status.');
    }
  }

  const visiblePages: number[] = [];
  const startPage = Math.max(0, pageResult.pageNumber - 2);
  const endPage = Math.min(Math.max(pageResult.totalPages - 1, 0), pageResult.pageNumber + 2);

  for (let page = startPage; page <= endPage; page += 1) {
    visiblePages.push(page);
  }

  return (
    <div className="flex h-full min-h-0 flex-col gap-3 overflow-hidden">
      <div className="flex min-h-0 flex-1 flex-col overflow-visible p-4 gap-4">
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
                  onKeyDown={(event) => {
                    if (event.key === 'Enter') {
                      event.preventDefault();
                      submitSearch();
                    }
                  }}
                  className="theme-input w-full px-2 py-2 text-sm border-none"
                  placeholder="Template Code / Title"
                />
              }
              advanced={
                <>
                  <input
                    value={draftFilters.processCode}
                    onChange={(event) => updateDraft('processCode', event.target.value)}
                    className="theme-input min-w-[180px] border-none"
                    placeholder="Process Code"
                  />
                  <input
                    value={draftFilters.templateType}
                    onChange={(event) => updateDraft('templateType', event.target.value)}
                    className="theme-input min-w-[180px] border-none"
                    placeholder="Template Type (EMAIL)"
                  />
                  <select
                    value={draftFilters.active}
                    onChange={(event) => updateDraft('active', event.target.value as EmailTemplateSearchFilters['active'])}
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
            <IconButton onClick={() => void openNewTemplate()} icon={navIcons.plus} label="Add email template" tone="success" size="sm" />
          </div>
        </div>
        <div className="min-h-0 flex-1 overflow-y-auto pr-1">
          {loading ? (
            <div className="flex min-h-[320px] items-center justify-center text-sm theme-muted-text">
              Loading email templates...
            </div>
          ) : pageResult.items.length === 0 ? (
            <div className="flex min-h-[320px] items-center justify-center text-sm theme-muted-text">
              No email templates found.
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {pageResult.items.map((template, index) => (
                <EmailTemplateCard
                  key={template.id ?? template.templateCode}
                  template={template}
                  index={index}
                  onView={(id) => void openTemplate(id)}
                  onToggleStatus={(item) => void toggleStatus(item)}
                />
              ))}
            </div>
          )}
        </div>

        <div className="theme-pagination-bar sticky bottom-0 z-20 mt-auto flex flex-col gap-4 px-6 py-3 backdrop-blur lg:flex-row lg:items-center lg:justify-between">
          <div className="theme-muted-text text-sm">
            Page <span className="theme-strong-text font-semibold">{pageResult.pageNumber + 1}</span> /{' '}
            <span>{Math.max(pageResult.totalPages, 1)}</span> | <span>{pageResult.totalElements}</span> items
          </div>

          <div className="flex flex-wrap items-center gap-3">
            {/* <select
              value={filters.size}
              onChange={(event) => changePageSize(Number(event.target.value))}
              className="theme-input rounded-xl px-3 py-2 text-sm"
            >
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select> */}

            <div className="flex items-center gap-1">
              <IconButton onClick={() => changePage(0)} disabled={pageResult.first} icon={navIcons.chevronsLeft} label="First page" size="sm" />
              <IconButton onClick={() => changePage(pageResult.pageNumber - 1)} disabled={!pageResult.hasPrevious} icon={navIcons.chevronLeft} label="Previous page" size="sm" />
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
            aria-label="Close email template panel"
            onClick={closeSlideOver}
            className={`absolute inset-0 backdrop-blur-[1px] transition-all duration-200 ${
              isSlideOverVisible ? 'bg-slate-950/60 opacity-100' : 'bg-slate-950/0 opacity-0'
            }`}
          />
          <div
            className={`absolute right-0 top-0 h-full w-1/2 min-w-[640px] overflow-hidden border-l border-[var(--border-subtle)] bg-[var(--surface-card-strong)] shadow-2xl transition-all duration-300 ease-out ${
              isSlideOverVisible ? 'translate-x-0 opacity-100' : 'translate-x-10 opacity-0'
            }`}
          >
            <div className="theme-table-divider flex items-center justify-between p-4">
              <h2 className="theme-strong-text text-lg font-semibold uppercase tracking-[0.18em]">
                Email Template
              </h2>
              <IconButton onClick={closeSlideOver} icon={navIcons.close} label="Close email template panel" size="sm" />
            </div>
            <div className="h-[calc(100%-61px)] overflow-y-auto p-4">
              <EmailTemplateEditor
                template={selectedTemplate}
                loading={detailLoading}
                saving={saving}
                onChange={setSelectedTemplate}
                onSave={() => void handleSave()}
                onEnableEdit={() => setReadOnly(false)}
                readOnly={readOnly}
              />
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
