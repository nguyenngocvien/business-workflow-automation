import type { ConnectionResult } from '../api/connector';

export interface ConnectionRecord extends Omit<ConnectionResult, 'connectionType' | 'config'> {
  connectionType: string;
  configText: string;
  active: boolean;
}

export interface ConnectionSearchFilters {
  keyword: string;
  connectionType: string;
  active: '' | 'true' | 'false';
  page: number;
  size: number;
}

export interface ConnectionPageResult {
  items: ConnectionRecord[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}
