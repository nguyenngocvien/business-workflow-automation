export type DatasourceRecord = {
  label: string;
  datasourceName: string;
  url?: string;
};

export type DatabasePackageState = {
  datasource: string;
  pkg: string;
  packages: string[];
  packageDefinition: string;
  packageBody: string;
};
