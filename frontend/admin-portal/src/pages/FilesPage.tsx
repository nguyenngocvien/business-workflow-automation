import { FormEvent, useEffect, useState } from 'react';
import { navIcons } from '../components/layout/navIcons';
import { FileDetailPanel } from '../components/files/FileDetailPanel';
import { FilePagination } from '../components/files/FilePagination';
import { Card } from '../components/ui/Card';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import {
  browseFiles,
  createFolder,
  fetchFileDetail,
  fetchFileOptions,
  fetchFolderDetail,
  searchFiles,
  uploadFiles,
} from '../services/fileManagerApi';
import type {
  ExplorerFilters,
  FileExplorerResponse,
  FilePageResult,
  FileRecord,
  FileSearchFilters,
  FolderRecord,
  NameValuePair,
} from '../types/fileManager';

const emptyPage: FilePageResult = {
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

const defaultSearch: FileSearchFilters = {
  classCode: '',
  documentId: '',
  documentName: '',
  processCode: '',
  docType: '',
  page: 0,
  size: 20,
  sort: 'cmis:lastModificationDate',
};

const defaultExplorer: ExplorerFilters = {
  path: '/',
  q: '',
  page: 0,
  size: 20,
  sort: 'cmis:lastModificationDate',
};

export function FilesPage() {
  const [showFolderSearch, setShowFolderSearch] = useState(false);
  const [showFileSearch, setShowFileSearch] = useState(false);
  const [mode, setMode] = useState<'browse' | 'search'>('browse');
  const [classes, setClasses] = useState<NameValuePair[]>([]);
  const [docTypes, setDocTypes] = useState<NameValuePair[]>([]);
  const [searchDraft, setSearchDraft] = useState<FileSearchFilters>(defaultSearch);
  const [searchFilters, setSearchFilters] = useState<FileSearchFilters>(defaultSearch);
  const [searchResult, setSearchResult] = useState<FilePageResult>(emptyPage);
  const [explorerFilters, setExplorerFilters] = useState<ExplorerFilters>(defaultExplorer);
  const [explorerData, setExplorerData] = useState<FileExplorerResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedFile, setSelectedFile] = useState<FileRecord | null>(null);
  const [selectedFolder, setSelectedFolder] = useState<FolderRecord | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
  const [uploadingFiles, setUploadingFiles] = useState<FileList | null>(null);
  const notify = useNotify();

  useEffect(() => {
    let active = true;

    void fetchFileOptions()
      .then((data) => {
        if (!active) {
          return;
        }
        setClasses(data.classes);
        setDocTypes(data.docTypes);
      })
      .catch((requestError) => {
        if (!active) {
          return;
        }
        notify.error(requestError instanceof Error ? requestError.message : 'Failed to load file options.');
      });

    return () => {
      active = false;
    };
  }, [notify]);

  useEffect(() => {
    let active = true;
    setLoading(true);

    const request =
      mode === 'browse' ? browseFiles(explorerFilters) : searchFiles(searchFilters);

    void request
      .then((data) => {
        if (!active) {
          return;
        }
        if (mode === 'browse') {
          setExplorerData(data as FileExplorerResponse);
        } else {
          setSearchResult(data as FilePageResult);
        }
      })
      .catch((requestError) => {
        if (!active) {
          return;
        }
        notify.error(requestError instanceof Error ? requestError.message : 'Failed to load files.');
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, [explorerFilters, mode, notify, searchFilters]);

  async function openFileDetail(id: string) {
    setDetailLoading(true);
    setSelectedFolder(null);
    try {
      setSelectedFile(await fetchFileDetail(id));
    } catch (requestError) {
      setSelectedFile(null);
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to load file detail.');
    } finally {
      setDetailLoading(false);
    }
  }

  async function openFolderDetail(id: string) {
    setDetailLoading(true);
    setSelectedFile(null);
    try {
      setSelectedFolder(await fetchFolderDetail(id));
    } catch (requestError) {
      setSelectedFolder(null);
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to load folder detail.');
    } finally {
      setDetailLoading(false);
    }
  }

  function submitSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSearchFilters({ ...searchDraft, page: 0 });
  }

  async function handleCreateFolder(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!explorerData?.currentFolderId || !newFolderName.trim()) {
      return;
    }
    try {
      const response = await createFolder(explorerData.currentFolderId, newFolderName.trim());
      notify.success(response.message || 'Folder created');
      setNewFolderName('');
      setExplorerFilters((current) => ({ ...current }));
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to create folder.');
    }
  }

  async function handleUpload(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!explorerData?.currentFolderId || !uploadingFiles?.length) {
      return;
    }
    try {
      const response = await uploadFiles(explorerData.currentFolderId, uploadingFiles);
      notify.success(response.message || 'Upload successful');
      setUploadingFiles(null);
      setExplorerFilters((current) => ({ ...current }));
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to upload files.');
    }
  }

  const activePage = mode === 'browse' ? explorerData?.files || emptyPage : searchResult;
  const showClassDependentFields = searchDraft.classCode.trim() !== '';

  return (
    <div className="flex h-full min-h-0 flex-col gap-3 overflow-hidden">
      <div className="flex min-h-0 flex-1 flex-col overflow-visible p-4 gap-4">
        <div className="flex h-full min-h-0 overflow-hidden">
          <div
            className={`flex flex-col gap-3 overflow-auto transition-all ${selectedFile || selectedFolder ? 'w-3/4 pr-3' : 'w-full'
              }`}
          >
            <div className='flex flex-col gap-2'>
              <div className="theme-card flex flex-col gap-4 rounded-xl p-4 shadow-sm">
                <div className="flex items-center justify-between border-b border-[var(--border-subtle)] pb-3">
                  <h3 className="text-lg font-semibold">Folders</h3>
                  <div className="flex items-center gap-2">
                    <div className="flex items-center rounded-2xl border border-[var(--border-subtle)] bg-[var(--surface-input)] px-2">
                      <input
                        placeholder="Search folders..."
                        className="theme-input border-none bg-transparent px-2 py-1.5 text-xs focus:shadow-none"
                        value={explorerFilters.q}
                        onChange={(e) =>
                          setExplorerFilters((c) => ({
                            ...c,
                            q: e.target.value,
                            page: 0,
                          }))
                        }
                        onKeyDown={(e) => {
                          if (e.key === 'Enter') {
                            setExplorerFilters((c) => ({
                              ...c,
                              page: 0,
                            }));
                          }
                        }}
                      />

                      {/* Clear button */}
                      {explorerFilters.q && (
                        <button
                          type="button"
                          onClick={() =>
                            setExplorerFilters((c) => ({
                              ...c,
                              q: '',
                              page: 0,
                            }))
                          }
                          className="px-1 text-xs text-slate-400 hover:text-slate-200"
                        >
                          ✕
                        </button>
                      )}

                      <IconButton
                        icon={navIcons.search}
                        label="Search folders"
                        size="sm"
                        className="!p-1.5"
                      />
                    </div>

                    {/* Create Folder */}
                    <IconButton
                      type="submit"
                      icon={navIcons.plus}
                      label="Create folder"
                      tone="primary"
                      size="sm"
                    />
                  </div>
                </div>

                {/* Folder horizontal list */}
                <div className="flex gap-3 overflow-x-auto pb-2 pt-2 bg-[var(--surface-muted)]/30 rounded-xl p-2">
                  {explorerData?.folders.map((folder) => (
                    <button
                      key={folder.id}
                      onDoubleClick={() =>
                        setExplorerFilters((c) => ({
                          ...c,
                          path: folder.path,
                          page: 0,
                        }))
                      }
                      onClick={() => void openFolderDetail(folder.id)}
                      className="
                        min-w-[180px] shrink-0
                        rounded-2xl
                        border border-[var(--border-subtle)]
                        bg-[var(--surface-card)]
                        px-4 py-3
                        text-left
                        transition
                        hover:bg-[var(--surface-input)]
                        hover:border-[rgba(var(--color-info),0.3)]
                      "
                    >
                      <div className="flex flex-col gap-1">
                        <span className="font-semibold truncate">{folder.name}</span>
                        <span className="text-xs text-slate-400">Folder</span>
                      </div>
                    </button>
                  ))}
                </div>
              </div>
              <div className="theme-card flex flex-col gap-4 rounded-xl p-4 shadow-sm">
                <div className='flex flex-col gap-4 border-b border-[var(--border-subtle)] pb-3'>
                  <div className="flex items-center justify-between">
                    <h3 className="text-lg font-semibold">Recent Files</h3>
                    <div className='flex gap-2'>
                      <IconButton type="submit" icon={navIcons.upload} label="Upload files" tone="primary" size="sm" />
                      <IconButton
                        onClick={() => setShowFileSearch((prev) => !prev)}
                        icon={navIcons.search}
                        label="Search files"
                        tone="success"
                        size="sm"
                      />
                    </div>

                  </div>
                  {/* Search (toggle) */}
                  {showFileSearch && (
                    <form className="grid gap-3 md:grid-cols-3" onSubmit={submitSearch}>
                      <input
                        placeholder="Document ID"
                        className="theme-input rounded-2xl px-3 py-2 text-xs"
                        value={searchDraft.documentId}
                        onChange={(e) =>
                          setSearchDraft((c) => ({ ...c, documentId: e.target.value }))
                        }
                      />

                      <input
                        placeholder="Document Name"
                        className="theme-input rounded-2xl px-3 py-2 text-xs"
                        value={searchDraft.documentName}
                        onChange={(e) =>
                          setSearchDraft((c) => ({ ...c, documentName: e.target.value }))
                        }
                      />

                      <select
                        className="theme-input rounded-2xl px-3 py-2 text-xs"
                        value={searchDraft.classCode}
                        onChange={(e) =>
                          setSearchDraft((c) => ({ ...c, classCode: e.target.value }))
                        }
                      >
                        <option value="">All Classes</option>
                        {classes.map((c) => (
                          <option key={c.value} value={c.value}>
                            {c.name}
                          </option>
                        ))}
                      </select>

                      <div className="md:col-span-3 flex gap-2">
                        <IconButton type="submit" icon={navIcons.search} label="Search" size="sm" />
                        <IconButton
                          icon={navIcons.close}
                          label="Clear"
                          size="sm"
                          onClick={() => {
                            setSearchDraft(defaultSearch);
                            setSearchFilters(defaultSearch);
                          }}
                        />
                      </div>
                    </form>
                  )}
                </div>
                <div className="flex flex-col gap-3">

                  {loading ? (
                    <div className="text-center py-10 text-sm text-[var(--text-muted)]">
                      Loading...
                    </div>
                  ) : activePage.items.length === 0 ? (
                    <div className="text-center py-10 text-sm text-[var(--text-muted)]">
                      No files found
                    </div>
                  ) : (
                    activePage.items.map((file) => (
                      <div
                        key={file.id}
                        className="
                          flex items-center justify-between gap-4
                          rounded-2xl
                          border border-[var(--border-subtle)]
                          bg-[var(--surface-card)]
                          px-4 py-3
                          transition
                          hover:bg-[var(--surface-input)]
                          hover:border-[rgba(var(--color-info),0.3)]
                        "
                      >
                        {/* LEFT: Info */}
                        <div className="flex min-w-0 flex-col gap-1">
                          <span className="font-semibold truncate">
                            {file.name}
                          </span>

                          <div className="flex flex-wrap items-center gap-2 text-xs text-[var(--text-muted)]">
                            <span>{file.className || '-'}</span>
                            <span>•</span>
                            <span>{file.createdBy || '-'}</span>
                            <span>•</span>
                            <span>{file.createdAt || '-'}</span>
                          </div>
                        </div>

                        {/* RIGHT: Actions */}
                        <div className="flex items-center gap-2 shrink-0">
                          <a
                            href={`/e-connector/files/${file.id}/content`}
                            className="
                              text-xs
                              text-[rgb(var(--color-info))]
                              hover:underline
                            "
                          >
                            Download
                          </a>

                          <IconButton
                            onClick={() => void openFileDetail(file.id)}
                            icon={navIcons.info}
                            label="Info"
                            size="sm"
                          />
                        </div>
                      </div>
                    ))
                  )}
                </div>

                {/* Pagination */}
                <FilePagination
                  page={activePage}
                  onPageChange={(page) =>
                    setSearchFilters((c) => ({ ...c, page }))
                  }
                  onPageSizeChange={(size) =>
                    setSearchFilters((c) => ({ ...c, size, page: 0 }))
                  }
                />
              </div>
            </div>
          </div>

          {(selectedFile || selectedFolder) && (
            <div className="w-1/4 min-w-[320px] border-l bg-white overflow-hidden">
              <FileDetailPanel
                file={selectedFile}
                folder={selectedFolder}
                loading={detailLoading}
                onClose={() => {
                  setSelectedFile(null);
                  setSelectedFolder(null);
                }}
              />
            </div>
          )
          }
        </div >
      </div>
    </div>
  );
}
