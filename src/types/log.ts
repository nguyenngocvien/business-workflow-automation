import type { LogResult } from '../api/connector';

export interface LogRecord extends Omit<LogResult, 'id' | 'statusCode'> {
  id: number;
  service: string;
  fromInput?: string;
  fromOutput?: string;
  toInput?: string;
  toOutput?: string;
  errorCode?: string;
  errorMessage?: string;
  stacktrace?: string;
  logCode?: string;
  caseId?: string;
  timing?: string;
  system?: string;
  createdDate?: string;
}

export interface LogSearchFilters {
  service: string;
  caseId: string;
  logId: string;
  from: string;
  to: string;
  status: '' | 'true' | 'false';
  page: number;
  size: number;
}

export interface LogPageResult {
  items: LogRecord[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}
