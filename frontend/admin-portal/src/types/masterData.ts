export type MasterDataRecord = {
  id?: number;
  code: string;
  name: string;
  groupCode: string;
  active: boolean;
};

export type MasterDataSearchFilters = {
  code: string;
  name: string;
  groupCode: string;
  active: '' | 'true' | 'false';
};
