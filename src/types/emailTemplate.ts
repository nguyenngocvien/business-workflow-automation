export type EmailTemplateSearchFilters = {
  keyword: string;
  processCode: string;
  templateType: string;
  active: '' | 'true' | 'false';
  page: number;
  size: number;
};

export type EmailTemplateRecord = {
  id?: number;
  processCode: string;
  templateType: string;
  templateCode: string;
  title: string;
  content: string;
  active: boolean;
  version?: string;
  createdBy?: string;
  updatedBy?: string;
};

export type EmailTemplatePageResult = {
  items: EmailTemplateRecord[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
};
