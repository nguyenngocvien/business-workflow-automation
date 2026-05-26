import type { ServiceResult } from '../api/connector';

export type ServiceType = '' | 'API' | 'EMAIL' | 'SQL' | 'GENDOC' | 'DECISION';

export type PageResult<T> = {
  items: T[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
};

export type SqlParamMode = 'IN' | 'OUT' | 'INOUT';

export type ApiConfig = {
  apiType?: string;
  method?: string;
  url?: string;
  authType?: string;
  username?: string;
  password?: string;
  token?: string;
  apiKeyHeader?: string;
  apiKeyValue?: string;
  oauth2ClientId?: string;
  oauth2ClientSecret?: string;
  oauth2TokenUrl?: string;
  timeout?: number;
  requestTemplate?: string;
};

export type SmtpConfig = {
  host?: string;
  port?: number;
  username?: string;
  password?: string;
  auth?: boolean;
  ssl?: boolean;
  debug?: boolean;
  fromAddress?: string;
};

export type SqlConfig = {
  dataSourceName?: string;
  sqlExecuteType?: string;
  schema?: string;
  packageName?: string;
  procedureName?: string;
  sqlStatement?: string;
  params?: SqlParam[];
};

export type SqlParam = {
  paramIndex?: number;
  name?: string;
  mode?: SqlParamMode;
  sqlType?: string;
  inputMapping?: string;
  outputMapping?: string;
  defaultValue?: string | number | boolean | null;
  required?: boolean;
};

export type ServiceConfig = Omit<ServiceResult, 'serviceType' | 'config'> & {
  serviceType?: ServiceType;
  appName?: string | null;
  systemName?: string | null;
  version?: string | null;
  description?: string | null;
  status?: boolean | null;
  logOn?: boolean | null;
  detailJson?: string | null;
  createdBy?: string | null;
  updatedBy?: string | null;
  apiConfig?: ApiConfig;
  smtpConfig?: SmtpConfig;
  sqlConfig?: SqlConfig;
};

export type ServiceSearchFilters = {
  app: string;
  service: string;
  system: string;
  active: '' | 'true' | 'false';
  page: number;
  size: number;
};

export type ServiceFormOptions = {
  datasources: Array<{
    label: string;
    datasourceName: string | null;
    url: string | null;
  }>;
  sqlTypes: string[];
};
