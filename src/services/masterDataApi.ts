import { api } from './api';
import type { MasterDataRecord, MasterDataSearchFilters } from '../types/masterData';

export async function fetchMasterData(filters: MasterDataSearchFilters) {
  const { data } = await api.get<MasterDataRecord[]>('/metadata', {
    params: {
      code: filters.code || undefined,
      name: filters.name || undefined,
      groupCode: filters.groupCode || undefined,
      active: filters.active || undefined,
    },
  });
  return data;
}

export async function createMasterData(payload: MasterDataRecord) {
  const { data } = await api.post<MasterDataRecord>('/metadata', payload);
  return data;
}

export async function updateMasterData(payload: MasterDataRecord) {
  const { data } = await api.put<MasterDataRecord>(`/metadata/${payload.id}`, payload);
  return data;
}

export async function deleteMasterData(id: number) {
  await api.delete(`/metadata/${id}`);
}

export async function updateMasterDataActive(id: number, active: boolean) {
  await api.patch(`/metadata/${id}/active`, undefined, {
    params: { active },
  });
}
