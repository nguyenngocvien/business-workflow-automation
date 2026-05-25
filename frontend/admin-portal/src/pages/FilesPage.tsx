import { FormEvent, useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  getEDocumentAPI,
  type FileResult,
  type PageFileResult,
} from '../api/document';
import { navIcons } from '../components/layout/navIcons';
import { Card } from '../components/ui/Card';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import { DEFAULT_CACHE_TIME, DEFAULT_STALE_TIME } from '../lib/queryClient';

type FileViewRow = {
  id: number;
  name: string;
  contentType: string;
  sizeLabel: string;
  createdAt: string;
};

const documentApi = getEDocumentAPI();

const FILE_PAGE_SIZE = 20;

function toFileViewRow(file: FileResult): FileViewRow {
  return {
    id: file.id ?? 0,
    name: file.fileName ?? `File ${file.id ?? ''}`.trim(),
    contentType: file.contentType ?? '-',
    sizeLabel: typeof file.size === 'number' ? `${Math.max(file.size, 0)} KB` : '-',
    createdAt: file.createdAt ?? '-',
  };
}

function toPage(result: PageFileResult): {
  items: FileViewRow[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
} {
  const pageNumber = result.number ?? 0;
  const pageSize = result.size ?? FILE_PAGE_SIZE;
  const totalPages = result.totalPages ?? 0;
  const items = (result.content ?? []).map(toFileViewRow);

  return {
    items,
    totalElements: result.totalElements ?? 0,
    pageNumber,
    pageSize,
    totalPages,
    first: Boolean(result.first),
    last: Boolean(result.last),
    hasNext: !result.last && (result.pageable?.pageNumber ?? pageNumber) + 1 < totalPages,
    hasPrevious: !result.first && pageNumber > 0,
  };
}

export function FilesPage() {
  const notify = useNotify();
  const queryClient = useQueryClient();

  const [page, setPage] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFileId, setSelectedFileId] = useState<number | null>(null);
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploadCategoryCode, setUploadCategoryCode] = useState('');
  const [newCategoryCode, setNewCategoryCode] = useState('');
  const [newCategoryName, setNewCategoryName] = useState('');

  const categoriesQuery = useQuery({
    queryKey: ['document', 'file-categories'] as const,
    queryFn: () => documentApi.getAll(),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const attributesQuery = useQuery({
    queryKey: ['document', 'file-attributes'] as const,
    queryFn: () => documentApi.getAll1(),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const filesQuery = useQuery({
    queryKey: ['document', 'files', page] as const,
    queryFn: () => documentApi.list({ pageable: { page, size: FILE_PAGE_SIZE, sort: ['cmis:lastModificationDate,desc'] } }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const selectedFileQuery = useQuery({
    queryKey: ['document', 'file', selectedFileId] as const,
    queryFn: () => documentApi.get(selectedFileId!),
    enabled: selectedFileId !== null,
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const createCategoryMutation = useMutation({
    mutationFn: (payload: { code: string; name: string }) =>
      documentApi.create1({
        code: payload.code,
        name: payload.name,
      }),
    onSuccess: async () => {
      setNewCategoryCode('');
      setNewCategoryName('');
      await queryClient.invalidateQueries({ queryKey: ['document', 'file-categories'] });
      notify.success('File category created.');
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to create file category.');
    },
  });

  const uploadMutation = useMutation({
    mutationFn: async (file: File) => {
      const presigned = await documentApi.presignedUpload({
        fileName: file.name,
        contentType: file.type || 'application/octet-stream',
        categoryCode: uploadCategoryCode || undefined,
      });

      const uploadUrl = presigned.uploadUrl;
      const objectKey = presigned.objectKey;

      if (!uploadUrl || !objectKey) {
        throw new Error('The upload service did not return a usable presigned URL.');
      }

      const uploadResponse = await fetch(uploadUrl, {
        method: 'PUT',
        headers: {
          'Content-Type': file.type || 'application/octet-stream',
        },
        body: file,
      });

      if (!uploadResponse.ok) {
        throw new Error('Failed to upload file to storage.');
      }

      return documentApi.completeUpload({
        objectKey,
        fileName: file.name,
        size: file.size,
        contentType: file.type || 'application/octet-stream',
        categoryCode: uploadCategoryCode || undefined,
      });
    },
    onSuccess: async () => {
      setUploadFile(null);
      await queryClient.invalidateQueries({ queryKey: ['document', 'files'] });
      notify.success('File uploaded successfully.');
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to upload file.');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => documentApi._delete(id),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['document', 'files'] }),
        queryClient.invalidateQueries({ queryKey: ['document', 'file'] }),
      ]);
      setSelectedFileId(null);
      notify.success('File deleted.');
    },
    onError: (error) => {
      notify.error(error instanceof Error ? error.message : 'Failed to delete file.');
    },
  });

  useEffect(() => {
    if (!filesQuery.error) {
      return;
    }

    notify.error(filesQuery.error instanceof Error ? filesQuery.error.message : 'Failed to load files.');
  }, [filesQuery.error, notify]);

  useEffect(() => {
    if (!categoriesQuery.error) {
      return;
    }

    notify.error(categoriesQuery.error instanceof Error ? categoriesQuery.error.message : 'Failed to load file categories.');
  }, [categoriesQuery.error, notify]);

  useEffect(() => {
    if (!attributesQuery.error) {
      return;
    }

    notify.error(attributesQuery.error instanceof Error ? attributesQuery.error.message : 'Failed to load file attributes.');
  }, [attributesQuery.error, notify]);

  const categories = categoriesQuery.data ?? [];
  const attributes = attributesQuery.data ?? [];

  const pageData = useMemo(() => {
    const normalized = filesQuery.data ? toPage(filesQuery.data) : null;

    if (!normalized) {
      return null;
    }

    if (!searchTerm.trim()) {
      return normalized;
    }

    const term = searchTerm.trim().toLowerCase();
    return {
      ...normalized,
      items: normalized.items.filter((item) =>
        [item.name, item.contentType, item.createdAt].some((value) => value.toLowerCase().includes(term)),
      ),
    };
  }, [filesQuery.data, searchTerm]);

  async function handleUpload(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!uploadFile) {
      notify.error('Choose a file to upload.');
      return;
    }

    await uploadMutation.mutateAsync(uploadFile);
  }

  async function handleCreateCategory(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const code = newCategoryCode.trim();
    const name = newCategoryName.trim();

    if (!code || !name) {
      notify.error('Category code and name are required.');
      return;
    }

    await createCategoryMutation.mutateAsync({ code, name });
  }

  const selectedFile = selectedFileQuery.data;

  return (
    <div className="flex h-full min-h-0 flex-col gap-4 overflow-hidden">
      <div className="grid min-h-0 flex-1 gap-4 lg:grid-cols-[320px_minmax(0,1fr)_320px]">
        <Card className="flex min-h-0 flex-col overflow-hidden">
          <div className="border-b border-[var(--border-subtle)] px-4 py-4">
            <p className="text-xs font-semibold uppercase tracking-[0.25em] text-cyan-600">Document store</p>
            <h2 className="mt-2 text-lg font-semibold">Categories</h2>
            <p className="mt-1 text-sm text-slate-500">Manage file categories exposed by the document API.</p>
          </div>

          <div className="flex-1 overflow-auto px-4 py-4">
            <form className="space-y-3" onSubmit={handleCreateCategory}>
              <input
                className="theme-input w-full rounded-xl px-3 py-2 text-sm"
                placeholder="Category code"
                value={newCategoryCode}
                onChange={(event) => setNewCategoryCode(event.target.value)}
              />
              <input
                className="theme-input w-full rounded-xl px-3 py-2 text-sm"
                placeholder="Category name"
                value={newCategoryName}
                onChange={(event) => setNewCategoryName(event.target.value)}
              />
              <IconButton
                type="submit"
                icon={navIcons.plus}
                label={createCategoryMutation.isPending ? 'Creating' : 'Create category'}
                tone="primary"
                size="sm"
                disabled={createCategoryMutation.isPending}
                className="w-full justify-center"
              />
            </form>

            <div className="mt-6 space-y-2">
              {categoriesQuery.isLoading ? (
                <div className="rounded-2xl border border-dashed border-[var(--border-subtle)] px-4 py-6 text-sm text-slate-500">
                  Loading categories...
                </div>
              ) : categories.length === 0 ? (
                <div className="rounded-2xl border border-dashed border-[var(--border-subtle)] px-4 py-6 text-sm text-slate-500">
                  No file categories yet.
                </div>
              ) : (
                categories.map((category) => (
                  <button
                    type="button"
                    key={`${category.id ?? category.code ?? category.name}`}
                    className="flex w-full items-start justify-between rounded-2xl border border-[var(--border-subtle)] px-4 py-3 text-left transition hover:bg-[var(--surface-input)]"
                    onClick={() => setUploadCategoryCode(category.code ?? '')}
                  >
                    <div>
                      <p className="font-semibold">{category.name ?? category.code ?? 'Category'}</p>
                      <p className="text-xs text-slate-500">{category.code ?? '-'}</p>
                    </div>
                    {uploadCategoryCode === category.code ? (
                      <span className="rounded-full bg-emerald-50 px-2 py-1 text-[11px] font-semibold text-emerald-700">
                        Selected
                      </span>
                    ) : null}
                  </button>
                ))
              )}
            </div>
          </div>
        </Card>

        <Card className="flex min-h-0 flex-col overflow-hidden">
          <div className="border-b border-[var(--border-subtle)] px-4 py-4">
            <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
              <div>
                <p className="text-xs font-semibold uppercase tracking-[0.25em] text-brand-500">Files</p>
                <h2 className="mt-2 text-lg font-semibold">Recent uploads</h2>
                <p className="mt-1 text-sm text-slate-500">Browse paginated files from the generated Orval client.</p>
              </div>

              <div className="flex flex-col gap-2 sm:flex-row">
                <label className="flex items-center gap-2 rounded-2xl border border-[var(--border-subtle)] px-3 py-2">
                  <svg viewBox="0 0 24 24" className="h-4 w-4 text-slate-400">
                    {navIcons.search}
                  </svg>
                  <input
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                    placeholder="Search files"
                    className="bg-transparent text-sm outline-none"
                  />
                </label>
                <IconButton
                  type="button"
                  icon={navIcons.upload}
                  label="Refresh"
                  tone="neutral"
                  size="sm"
                  onClick={() => void filesQuery.refetch()}
                />
              </div>
            </div>
          </div>

          <div className="flex min-h-0 flex-1 flex-col overflow-hidden">
            <div className="flex-1 overflow-auto p-4">
              {filesQuery.isLoading ? (
                <div className="flex min-h-[280px] items-center justify-center text-sm text-slate-500">
                  Loading files...
                </div>
              ) : pageData && pageData.items.length > 0 ? (
                <div className="space-y-3">
                  {pageData.items.map((file) => (
                    <div
                      key={file.id}
                      className="flex items-center justify-between gap-4 rounded-2xl border border-[var(--border-subtle)] bg-[var(--surface-card)] px-4 py-3 transition hover:bg-[var(--surface-input)]"
                    >
                      <div className="min-w-0">
                        <p className="truncate font-semibold">{file.name}</p>
                        <p className="text-xs text-slate-500">
                          {file.contentType} - {file.sizeLabel} - {file.createdAt}
                        </p>
                      </div>

                      <div className="flex items-center gap-2">
                        <button
                          type="button"
                          className="rounded-xl px-3 py-2 text-xs font-semibold text-brand-600 transition hover:bg-brand-50"
                          onClick={async () => {
                            const url = await documentApi.presignedDownload(file.id);
                            window.open(url, '_blank', 'noopener,noreferrer');
                          }}
                        >
                          Download
                        </button>
                        <IconButton
                          type="button"
                          onClick={() => setSelectedFileId(file.id)}
                          icon={navIcons.info}
                          label="Details"
                          size="sm"
                        />
                        <IconButton
                          type="button"
                          onClick={() => void deleteMutation.mutateAsync(file.id)}
                          icon={navIcons.trash}
                          label="Delete"
                          size="sm"
                        />
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="flex min-h-[280px] items-center justify-center text-sm text-slate-500">
                  No files found.
                </div>
              )}
            </div>

            {pageData ? (
              <div className="border-t border-[var(--border-subtle)] px-4 py-3">
                <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
                  <div className="text-sm text-slate-500">
                    Page <span className="font-semibold text-slate-900">{pageData.pageNumber + 1}</span> of{' '}
                    <span>{Math.max(pageData.totalPages, 1)}</span> - {pageData.totalElements} items
                  </div>
                  <div className="flex items-center gap-2">
                    <IconButton
                      onClick={() => setPage(0)}
                      disabled={!pageData.hasPrevious}
                      icon={navIcons.chevronsLeft}
                      label="First page"
                      size="sm"
                    />
                    <IconButton
                      onClick={() => setPage((current) => Math.max(0, current - 1))}
                      disabled={!pageData.hasPrevious}
                      icon={navIcons.chevronLeft}
                      label="Previous page"
                      size="sm"
                    />
                    <IconButton
                      onClick={() => setPage((current) => current + 1)}
                      disabled={!pageData.hasNext}
                      icon={navIcons.chevronRight}
                      label="Next page"
                      size="sm"
                    />
                  </div>
                </div>
              </div>
            ) : null}
          </div>
        </Card>

        <Card className="flex min-h-0 flex-col overflow-hidden bg-slate-950 text-white">
          <div className="border-b border-white/10 px-4 py-4">
            <p className="text-xs font-semibold uppercase tracking-[0.25em] text-cyan-300">Upload</p>
            <h2 className="mt-2 text-lg font-semibold">Presigned upload</h2>
            <p className="mt-1 text-sm text-slate-300">Send a file through the document API and upload it to storage.</p>
          </div>

          <div className="flex-1 overflow-auto px-4 py-4">
            <form className="space-y-4" onSubmit={handleUpload}>
              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-slate-200">File</span>
                <input
                  type="file"
                  className="theme-input w-full rounded-2xl px-3 py-2 text-sm text-white"
                  onChange={(event) => setUploadFile(event.target.files?.[0] ?? null)}
                />
              </label>

              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-slate-200">Category code</span>
                <input
                  value={uploadCategoryCode}
                  onChange={(event) => setUploadCategoryCode(event.target.value)}
                  className="theme-input w-full rounded-2xl px-3 py-2 text-sm text-white"
                  placeholder="Optional"
                />
              </label>

              <IconButton
                type="submit"
                icon={navIcons.upload}
                label={uploadMutation.isPending ? 'Uploading' : 'Upload file'}
                tone="primary"
                size="sm"
                disabled={uploadMutation.isPending}
                className="w-full justify-center"
              />
            </form>

            <div className="mt-6 rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-slate-300">
              <p className="font-semibold text-white">Attributes</p>
              <p className="mt-1 text-xs text-slate-400">Loaded from `/api/v1/file-attributes`.</p>
              <div className="mt-3 space-y-2">
                {attributesQuery.isLoading ? (
                  <div className="text-xs text-slate-400">Loading attributes...</div>
                ) : attributes.length === 0 ? (
                  <div className="text-xs text-slate-400">No attributes configured.</div>
                ) : (
                  attributes.slice(0, 6).map((attribute) => (
                    <div key={attribute.id ?? attribute.keyCode} className="flex items-center justify-between gap-3 text-xs">
                      <span className="truncate">{attribute.displayName ?? attribute.keyCode ?? 'Attribute'}</span>
                      <span className="text-slate-400">{attribute.dataType ?? '-'}</span>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        </Card>
      </div>

      {selectedFileQuery.data ? (
        <Card className="border border-[var(--border-subtle)]">
          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.25em] text-brand-500">Selected file</p>
              <h3 className="mt-2 text-xl font-semibold">{selectedFileQuery.data.fileName ?? `File ${selectedFileQuery.data.id}`}</h3>
            </div>
            <IconButton
              type="button"
              onClick={() => setSelectedFileId(null)}
              icon={navIcons.close}
              label="Close"
              size="sm"
            />
          </div>

          <div className="mt-4 grid gap-3 text-sm text-slate-600 sm:grid-cols-2 lg:grid-cols-4">
            <div>
              <p className="text-xs uppercase tracking-[0.2em] text-slate-400">ID</p>
              <p className="mt-1 font-semibold text-slate-900">{selectedFileQuery.data.id ?? '-'}</p>
            </div>
            <div>
              <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Content type</p>
              <p className="mt-1 font-semibold text-slate-900">{selectedFileQuery.data.contentType ?? '-'}</p>
            </div>
            <div>
              <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Size</p>
              <p className="mt-1 font-semibold text-slate-900">
                {typeof selectedFileQuery.data.size === 'number' ? `${selectedFileQuery.data.size} KB` : '-'}
              </p>
            </div>
            <div>
              <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Created</p>
              <p className="mt-1 font-semibold text-slate-900">{selectedFileQuery.data.createdAt ?? '-'}</p>
            </div>
          </div>
        </Card>
      ) : null}
    </div>
  );
}
