import { api } from './api';
import type { LogPageResult, LogRecord, LogSearchFilters } from '../types/log';

type LogDetailResponse = {
  log: LogRecord | null;
};

export async function fetchLogs(filters: LogSearchFilters) {
  const { data } = await api.get<LogPageResult>('/logs', {
    params: {
      service: filters.service || undefined,
      caseId: filters.caseId || undefined,
      logId: filters.logId || undefined,
      from: filters.from || undefined,
      to: filters.to || undefined,
      status: filters.status || undefined,
      page: filters.page,
      size: filters.size,
    },
  });

  return data;
}

export async function fetchLogDetail(id: number) {
  const { data } = await api.get<LogDetailResponse>(`/logs/${id}`);
  return data.log;
}
