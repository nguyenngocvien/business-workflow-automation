import { api } from './api';

export type DatasourceHealth = {
  name: string;
  url: string;
  connected: boolean;
  message?: string | null;
};

export type DatasourceCatalogItem = {
  datasourceName?: string | null;
  url?: string | null;
};

export type ExternalServerHealth = {
  name: string;
  host: string;
  port: number;
  ssl: boolean;
  connected: boolean;
  message?: string | null;
};

export type CmisHealth = {
  name: string;
  url: string;
  connected: boolean;
  message?: string | null;
};

export type CmisRepositoryInfo = {
  name: string;
  url: string;
  bindingType: string;
  repositoryIndex: number;
};

export async function fetchDatasourceCatalog() {
  const { data } = await api.get<DatasourceCatalogItem[]>('/datasources');
  return data;
}

export async function fetchDatasourceHealths() {
  const { data } = await api.get<DatasourceHealth[]>('/datasources/health');
  return data;
}

export async function fetchDatasourceHealth(name: string) {
  const { data } = await api.get<DatasourceHealth>(`/datasources/${encodeURIComponent(name)}/health`);
  return data;
}

export async function fetchExternalServerConfigs() {
  const { data } = await api.get<ExternalServerHealth[]>('/servers');
  return data;
}

export async function fetchExternalServerHealth(name: string) {
  const { data } = await api.get<ExternalServerHealth>(`/servers/${encodeURIComponent(name)}/health`);
  return data;
}

export async function fetchCmisRepositories() {
  const { data } = await api.get<CmisRepositoryInfo[]>('/cmis');
  return data;
}

export async function fetchCmisHealth(name: string) {
  const { data } = await api.get<CmisHealth>(`/cmis/${encodeURIComponent(name)}/health`);
  return data;
}
