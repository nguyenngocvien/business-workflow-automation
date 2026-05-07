import { api } from './api';
import type {
  ExplorerFilters,
  FileExplorerResponse,
  FilePageResult,
  FileRecord,
  FileSearchFilters,
  FolderRecord,
  NameValuePair,
} from '../types/fileManager';

type FileOptionsResponse = {
  classes: NameValuePair[];
  docTypes: NameValuePair[];
};

type MessageResponse = {
  id?: string;
  name?: string;
  path?: string;
  uploaded?: number;
  message?: string;
};

export async function fetchFileOptions() {
  const { data } = await api.get<FileOptionsResponse>('/files/options');
  return data;
}

export async function searchFiles(filters: FileSearchFilters) {
  const { data } = await api.get<FilePageResult>('/files/search', {
    params: {
      classCode: filters.classCode || undefined,
      documentId: filters.documentId || undefined,
      documentName: filters.documentName || undefined,
      processCode: filters.processCode || undefined,
      docType: filters.docType || undefined,
      page: filters.page,
      size: filters.size,
      sort: filters.sort,
    },
  });
  return data;
}

export async function browseFiles(filters: ExplorerFilters) {
  const { data } = await api.get<FileExplorerResponse>('/files/explorer', {
    params: {
      path: filters.path || '/',
      q: filters.q || undefined,
      page: filters.page,
      size: filters.size,
      sort: filters.sort,
    },
  });
  return data;
}

export async function fetchFileDetail(id: string) {
  const { data } = await api.get<FileRecord>(`/files/${id}`);
  return data;
}

export async function fetchFolderDetail(id: string) {
  const { data } = await api.get<FolderRecord>(`/files/folders/${id}`);
  return data;
}

export async function createFolder(parentId: string, name: string) {
  const form = new URLSearchParams();
  form.set('parentId', parentId);
  form.set('name', name);

  const { data } = await api.post<MessageResponse>('/files/folders', form, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  });
  return data;
}

export async function uploadFiles(folderId: string, files: FileList | File[]) {
  const form = new FormData();
  Array.from(files).forEach((file) => form.append('files', file));

  const { data } = await api.post<MessageResponse>(`/files/folders/${folderId}/upload`, form);
  return data;
}
