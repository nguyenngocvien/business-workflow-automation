import { useEffect, useMemo, useRef, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getEConnectorAPI, type EmailTemplateResult } from '../api/connector';
import { EmailTemplateCard } from '../components/email/EmailTemplateCard';
import { EmailTemplateEditor } from '../components/email/EmailTemplateEditor';
import { navIcons } from '../components/layout/navIcons';
import { IconButton } from '../components/ui/IconButton';
import { SearchToolbar } from '../components/ui/SearchToolbar';
import { useNotify } from '../components/ui/NotificationProvider';
import { DEFAULT_CACHE_TIME, DEFAULT_STALE_TIME } from '../lib/queryClient';
import type {
  EmailTemplatePageResult,
  EmailTemplateRecord,
  EmailTemplateSearchFilters,
} from '../types/emailTemplate';

const connectorApi = getEConnectorAPI();
const EMAIL_TEMPLATE_PAGE_SIZE = 20;

const defaultFilters: EmailTemplateSearchFilters = {
  keyword: '',
  processCode: '',
  templateType: '',
  active: '',
  page: 0,
  size: EMAIL_TEMPLATE_PAGE_SIZE,
};

const emptyPage: EmailTemplatePageResult = {
  items: [],
  totalElements: 0,
  pageNumber: 0,
  pageSize: EMAIL_TEMPLATE_PAGE_SIZE,
  totalPages: 0,
  first: true,
  last: true,
  hasNext: false,
  hasPrevious: false,
};

function mapTemplateResult(result: EmailTemplateResult): EmailTemplateRecord {
  return {
    id: result.id,
    processCode: result.appId ?? '',
    templateType: result.templateType ?? '',
    templateCode: result.templateCode ?? '',
    title: result.title ?? '',
    content: result.content ?? '',
    active: Boolean(result.status),
    version: result.updatedAt ?? result.createdAt,
    createdBy: result.createdBy,
    updatedBy: result.updatedBy,
  };
}

function toCreatePayload(template: EmailTemplateRecord) {
  return {
    appId: template.processCode.trim(),
    templateType: template.templateType.trim(),
    templateCode: template.templateCode.trim(),
    title: template.title.trim(),
    content: template.content,
    status: template.active,
    createdBy: template.createdBy?.trim() || undefined,
  };
}

function toUpdatePayload(template: EmailTemplateRecord) {
  return {
    templateType: template.templateType.trim(),
    title: template.title.trim(),
    content: template.content,
    status: template.active,
    updatedBy: template.updatedBy?.trim() || undefined,
  };
}

function matchesFilters(template: EmailTemplateRecord, filters: EmailTemplateSearchFilters) {
  const keyword = filters.keyword.trim().toLowerCase();
  const processCode = filters.processCode.trim().toLowerCase();
  const templateType = filters.templateType.trim().toLowerCase();

  const matchesKeyword =
    !keyword ||
    [template.templateCode, template.title, template.processCode, template.templateType]
      .filter(Boolean)
      .some((value) => value.toLowerCase().includes(keyword));
  const matchesProcessCode = !processCode || template.processCode.toLowerCase().includes(processCode);
  const matchesTemplateType = !templateType || template.templateType.toLowerCase().includes(templateType);
  const matchesActive =
    !filters.active ||
    (filters.active === 'true' && template.active) ||
    (filters.active === 'false' && !template.active);

  return matchesKeyword && matchesProcessCode && matchesTemplateType && matchesActive;
}

export function EmailTemplatesPage() {
  const notify = useNotify();
  const queryClient = useQueryClient();
  const slideOverTimeoutRef = useRef<number | null>(null);

  const [draftFilters, setDraftFilters] = useState<EmailTemplateSearchFilters>(defaultFilters);
  const [filters, setFilters] = useState<EmailTemplateSearchFilters>(defaultFilters);
  const [page, setPage] = useState(0);
  const [selectedTemplateId, setSelectedTemplateId] = useState<number | null>(null);
  const [selectedTemplate, setSelectedTemplate] = useState<EmailTemplateRecord | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [readOnly, setReadOnly] = useState(true);
  const [isSlideOverMounted, setIsSlideOverMounted] = useState(false);
  const [isSlideOverVisible, setIsSlideOverVisible] = useState(false);
  const [isToolbarExpanded, setIsToolbarExpanded] = useState(false);

  const templatesQuery = useQuery({
    queryKey: ['connector', 'email-templates', page] as const,
    queryFn: () =>
      connectorApi.findAll5({
        page,
        size: EMAIL_TEMPLATE_PAGE_SIZE,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const selectedTemplateQuery = useQuery({
    queryKey: ['connector', 'email-template', selectedTemplateId] as const,
    queryFn: () => connectorApi.findById5(selectedTemplateId!),
    enabled: selectedTemplateId !== null,
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const createMutation = useMutation({
    mutationFn: (template: EmailTemplateRecord) => connectorApi.create5(toCreatePayload(template)),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['connector', 'email-templates'] });
      notify.success('Email template created.');
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to create email template.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, template }: { id: number; template: EmailTemplateRecord }) =>
      connectorApi.update5(id, toUpdatePayload(template)),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['connector', 'email-templates'] }),
        queryClient.invalidateQueries({ queryKey: ['connector', 'email-template'] }),
      ]);
      notify.success('Email template saved.');
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to save template.');
    },
  });

  const toggleStatusMutation = useMutation({
    mutationFn: async (template: EmailTemplateRecord) => {
      if (!template.id) {
        throw new Error('Template id is missing.');
      }

      return connectorApi.update5(template.id, {
        ...toUpdatePayload(template),
        status: !template.active,
      });
    },
    onSuccess: async (_result, template) => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['connector', 'email-templates'] }),
        queryClient.invalidateQueries({ queryKey: ['connector', 'email-template', template.id] }),
      ]);
      notify.success(`${template.templateCode} ${template.active ? 'disabled' : 'enabled'}.`);
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to update template status.');
    },
  });

  useEffect(() => {
    if (!templatesQuery.error) {
      return;
    }

    notify.error(templatesQuery.error instanceof Error ? templatesQuery.error.message : 'Failed to load email templates.');
  }, [notify, templatesQuery.error]);

  useEffect(() => {
    if (!selectedTemplateQuery.error) {
      return;
    }

    notify.error(
      selectedTemplateQuery.error instanceof Error
        ? selectedTemplateQuery.error.message
        : 'Failed to load email template.',
    );
    closeSlideOver();
  }, [notify, selectedTemplateQuery.error]);

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
    if (selectedTemplateId === null || !selectedTemplateQuery.data) {
      return;
    }

    setSelectedTemplate(mapTemplateResult(selectedTemplateQuery.data));
    setDetailLoading(false);
  }, [selectedTemplateId, selectedTemplateQuery.data]);

  const pageResult = useMemo<EmailTemplatePageResult>(() => {
    const source = templatesQuery.data;
    const mapped = (source?.content ?? []).map(mapTemplateResult).filter((template) => matchesFilters(template, filters));

    return {
      items: mapped,
      totalElements: source?.totalElements ?? 0,
      pageNumber: source?.number ?? 0,
      pageSize: source?.size ?? EMAIL_TEMPLATE_PAGE_SIZE,
      totalPages: source?.totalPages ?? 0,
      first: Boolean(source?.first),
      last: Boolean(source?.last),
      hasNext: !source?.last && (source?.number ?? 0) + 1 < (source?.totalPages ?? 0),
      hasPrevious: !source?.first && (source?.number ?? 0) > 0,
    };
  }, [filters, templatesQuery.data]);

  const visiblePages = useMemo(() => {
    const pages: number[] = [];
    const startPage = Math.max(0, pageResult.pageNumber - 2);
    const endPage = Math.min(Math.max(pageResult.totalPages - 1, 0), pageResult.pageNumber + 2);

    for (let currentPage = startPage; currentPage <= endPage; currentPage += 1) {
      pages.push(currentPage);
    }

    return pages;
  }, [pageResult.pageNumber, pageResult.totalPages]);

  function updateDraft<K extends keyof EmailTemplateSearchFilters>(key: K, value: EmailTemplateSearchFilters[K]) {
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
      size: EMAIL_TEMPLATE_PAGE_SIZE,
    });
  }

  function changePage(nextPage: number) {
    setPage(Math.max(0, nextPage));
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
      setSelectedTemplateId(null);
      setSelectedTemplate(null);
      setDetailLoading(false);
      setSaving(false);
      setReadOnly(true);
      slideOverTimeoutRef.current = null;
    }, 280);
  }

  async function openTemplate(id: number) {
    setSaving(false);
    setReadOnly(true);
    setDetailLoading(true);
    setSelectedTemplate(null);
    setSelectedTemplateId(id);
    openSlideOver();
  }

  async function openNewTemplate() {
    setSaving(false);
    setReadOnly(false);
    setDetailLoading(false);
    setSelectedTemplateId(null);
    setSelectedTemplate({
      processCode: draftFilters.processCode.trim(),
      templateType: '',
      templateCode: '',
      title: '',
      content: '',
      active: true,
    });
    openSlideOver();
  }

  async function handleSave() {
    if (!selectedTemplate) {
      return;
    }

    const payload = {
      ...selectedTemplate,
      processCode: selectedTemplate.processCode.trim(),
      templateType: selectedTemplate.templateType.trim(),
      templateCode: selectedTemplate.templateCode.trim(),
      title: selectedTemplate.title.trim(),
      content: selectedTemplate.content,
    };

    if (!payload.processCode || !payload.templateType || !payload.templateCode || !payload.title || !payload.content) {
      notify.error('Process code, template type, template code, title, and content are required.');
      return;
    }

    setSaving(true);

    try {
      if (payload.id) {
        await updateMutation.mutateAsync({ id: payload.id, template: payload });
        setReadOnly(true);
      } else {
        await createMutation.mutateAsync(payload);
        closeSlideOver();
      }
    } catch {
      // Mutation handlers already surface errors.
    } finally {
      setSaving(false);
    }
  }

  async function toggleStatus(template: EmailTemplateRecord) {
    await toggleStatusMutation.mutateAsync(template);
  }

  return (
    <div className="flex h-full min-h-0 flex-col gap-3 overflow-hidden">
      <div className="flex min-h-0 flex-1 flex-col overflow-visible gap-4 p-4">
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
                  className="theme-input w-full border-none px-2 py-2 text-sm"
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
                    placeholder="Template Type"
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
            <IconButton
              onClick={() => void openNewTemplate()}
              icon={navIcons.plus}
              label="Add email template"
              tone="success"
              size="sm"
            />
          </div>
        </div>

        <div className="min-h-0 flex-1 overflow-y-auto pr-1">
          {templatesQuery.isLoading ? (
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
                  onToggleStatus={(item) => void toggleStatus(item as EmailTemplateRecord)}
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
              {visiblePages.map((currentPage) => (
                <button
                  type="button"
                  key={currentPage}
                  onClick={() => changePage(currentPage)}
                  className={`rounded-xl px-3 py-2 text-sm font-semibold ${
                    currentPage === pageResult.pageNumber ? 'theme-table-page-active' : 'theme-input'
                  }`}
                >
                  {currentPage + 1}
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
                onChange={(template) => setSelectedTemplate(template as EmailTemplateRecord)}
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
