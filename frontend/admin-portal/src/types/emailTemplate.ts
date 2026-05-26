import type { EmailTemplateResult } from '../api/connector';

export interface EmailTemplateRecord extends Omit<EmailTemplateResult, 'templateType' | 'templateCode' | 'title' | 'content'> {
  templateType: string;
  templateCode: string;
  title: string;
  content: string;
  processCode: string;
  active: boolean;
  version?: string;
}

export interface EmailTemplateSearchFilters {
  keyword: string;
  processCode: string;
  templateType: string;
  active: '' | 'true' | 'false';
  page: number;
  size: number;
}

export interface EmailTemplatePageResult {
  items: EmailTemplateRecord[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}
