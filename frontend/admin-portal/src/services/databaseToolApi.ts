import { api } from './api';
import type { DatabasePackageState, DatasourceRecord } from '../types/databaseTool';

type SaveResponse = {
  message?: string;
};

export async function fetchDatasources() {
  const { data } = await api.get<DatasourceRecord[]>('/database-tool/datasources');
  return data;
}

export async function fetchDatabasePackages(datasource: string, pkg: string) {
  const { data } = await api.get<DatabasePackageState>('/database-tool/packages', {
    params: {
      datasource: datasource || undefined,
      pkg: pkg || undefined,
    },
  });
  return data;
}

export async function saveDatabasePackage(payload: {
  datasource: string;
  pkg: string;
  packageDefinition: string;
  packageBody: string;
}) {
  const { data } = await api.post<SaveResponse>('/database-tool/save', payload);
  return data;
}
