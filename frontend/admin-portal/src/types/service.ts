export type ServiceSearchFilters = {
  app: string;
  service: string;
  system: string;
  active: '' | 'true' | 'false';
  page: number;
  size: number;
};

export type ServiceType = '' | 'API' | 'EMAIL' | 'SQL' | 'GENDOC' | 'DECISION';

export type ApiAuthType = '' | 'NONE' | 'BASIC' | 'API_KEY' | 'TOKEN' | 'OAUTH2';

export type ApiHeader = Record<string, string>;

export type ApiConfig = {
  apiType?: '' | 'REST' | 'SOAP' | null;
  url?: string | null;
  method?: '' | 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | null;
  timeout?: number | null;
  authType?: ApiAuthType | null;
  username?: string | null;
  password?: string | null;
  token?: string | null;
  apiKeyHeader?: string | null;
  apiKeyValue?: string | null;
  oauth2ClientId?: string | null;
  oauth2ClientSecret?: string | null;
  oauth2TokenUrl?: string | null;
  headers?: ApiHeader[] | null;
  requestTemplate?: string | null;
};

export type SmtpConfig = {
  host?: string | null;
  port?: number | null;
  username?: string | null;
  password?: string | null;
  auth?: boolean | null;
  ssl?: boolean | null;
  debug?: boolean | null;
  fromAddress?: string | null;
};

export type SqlExecuteType = '' | 'PROCEDURE' | 'QUERY' | 'UPDATE';

export type SqlParamMode = 'IN' | 'OUT' | 'INOUT';

export type SqlParam = {
  paramIndex?: number | null;
  name?: string | null;
  mode?: SqlParamMode | null;
  sqlType?: string | null;
  required?: boolean;
  inputMapping?: string | null;
  defaultValue?: unknown;
  value?: unknown;
  outputMapping?: string | null;
};

export type SqlConfig = {
  dataSourceName?: string | null;
  sqlExecuteType?: SqlExecuteType | null;
  sqlStatement?: string | null;
  schema?: string | null;
  packageName?: string | null;
  procedureName?: string | null;
  params?: SqlParam[] | null;
};

export type ServiceConfig = {
  id?: number | null;
  appName?: string | null;
  serviceName?: string | null;
  serviceType?: ServiceType | null;
  systemName?: string | null;
  version?: string | null;
  description?: string | null;
  status?: boolean | null;
  logOn?: boolean | null;
  detailJson?: string | null;
  apiConfig?: ApiConfig | null;
  smtpConfig?: SmtpConfig | null;
  sqlConfig?: SqlConfig | null;
};

export type ServiceFormOptions = {
  datasources: Array<{
    label: string | null;
    datasourceName: string | null;
    url?: string | null;
  }>;
  sqlTypes: string[];
};

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
