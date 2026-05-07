export type NameValuePair = {
  value: string;
  name: string;
};

export type FileRecord = {
  id: string;
  name: string;
  className?: string;
  path?: string;
  parentFolderId?: string;
  size: number;
  contentType?: string;
  createdAt?: string;
  createdBy?: string;
};

export type FolderRecord = {
  id: string;
  name: string;
  path: string;
  createdAt?: string;
  folders?: FolderRecord[];
  files?: FileRecord[];
};

export type Breadcrumb = {
  id?: string;
  name: string;
  path: string;
};

export type FilePageResult = {
  items: FileRecord[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
};

export type FileSearchFilters = {
  classCode: string;
  documentId: string;
  documentName: string;
  processCode: string;
  docType: string;
  page: number;
  size: number;
  sort: string;
};

export type ExplorerFilters = {
  path: string;
  q: string;
  page: number;
  size: number;
  sort: string;
};

export type FileExplorerResponse = {
  currentFolderId: string;
  currentPath: string;
  query?: string;
  breadcrumbs: Breadcrumb[];
  folders: FolderRecord[];
  files: FilePageResult;
};
