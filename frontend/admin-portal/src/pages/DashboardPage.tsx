import { useEffect, useState } from 'react';
import { HealthSection } from '../components/dashboard/HealthSection';
import { useNotify } from '../components/ui/NotificationProvider';
import {
  fetchCmisHealth,
  fetchCmisRepositories,
  fetchDatasourceCatalog,
  fetchDatasourceHealth,
  fetchDatasourceHealths,
  fetchExternalServerConfigs,
  fetchExternalServerHealth,
  type CmisHealth,
  type CmisRepositoryInfo,
  type DatasourceCatalogItem,
  type DatasourceHealth,
  type ExternalServerHealth,
} from '../services/serverHealthApi';

type HealthCard = {
  name: string;
  subtitle: string;
  icon: 'database' | 'cloud' | 'folder';
  connected: boolean | null;
  message?: string;
};

function mapDatasourceCatalogItem(item: DatasourceCatalogItem, index: number): HealthCard {
  const name = item.datasourceName ?? `DATASOURCE-${index + 1}`;
  const subtitle = item.url ?? name;

  return {
    name,
    subtitle,
    icon: 'database',
    connected: null,
  };
}

function mapDatasourceHealth(item: DatasourceHealth): HealthCard {
  return {
    name: item.name,
    subtitle: item.url,
    icon: 'database',
    connected: item.connected,
    message: item.message ?? undefined,
  };
}

function mapExternalServerConfig(item: ExternalServerHealth): HealthCard {
  return {
    name: item.name,
    subtitle: `${item.ssl ? 'https' : 'http'}://${item.host}:${item.port}`,
    icon: 'cloud',
    connected: null,
  };
}

function mapExternalServerHealth(item: ExternalServerHealth): HealthCard {
  return {
    name: item.name,
    subtitle: `${item.ssl ? 'https' : 'http'}://${item.host}:${item.port}`,
    icon: 'cloud',
    connected: item.connected,
    message: item.message ?? undefined,
  };
}

function mapCmisRepository(item: CmisRepositoryInfo): HealthCard {
  return {
    name: item.name,
    subtitle: item.url,
    icon: 'folder',
    connected: null,
  };
}

function mapCmisHealth(item: CmisHealth): HealthCard {
  return {
    name: item.name,
    subtitle: item.url,
    icon: 'folder',
    connected: item.connected,
    message: item.message ?? undefined,
  };
}

export function DashboardPage() {
  const [datasources, setDatasources] = useState<HealthCard[]>([]);
  const [servers, setServers] = useState<HealthCard[]>([]);
  const [cmisRepositories, setCmisRepositories] = useState<HealthCard[]>([]);
  const [isLoadingDatasources, setIsLoadingDatasources] = useState(true);
  const [isLoadingServers, setIsLoadingServers] = useState(true);
  const [isLoadingCmisRepositories, setIsLoadingCmisRepositories] = useState(true);
  const notify = useNotify();

  useEffect(() => {
    let active = true;
    setIsLoadingDatasources(true);
    setIsLoadingServers(true);
    setIsLoadingCmisRepositories(true);

    void Promise.allSettled([
      (async () => {
        try {
          const [datasourceCatalog, datasourceHealthCatalog] = await Promise.all([
            fetchDatasourceCatalog().catch(() => null),
            fetchDatasourceHealths().catch(() => [] as DatasourceHealth[]),
          ]);

          if (!active) {
            return;
          }

          if (datasourceCatalog && datasourceCatalog.length > 0) {
            setDatasources(datasourceCatalog.map(mapDatasourceCatalogItem));
          } else {
            setDatasources(datasourceHealthCatalog.map((item) => ({
              ...mapDatasourceHealth(item),
              connected: null,
              message: undefined,
            })));
          }

          const results = await Promise.all(
            datasourceHealthCatalog.map((item) =>
              fetchDatasourceHealth(item.name)
                .then((health) => ({ ok: true as const, health }))
                .catch(() => ({ ok: false as const, name: item.name })),
            ),
          );

          if (!active) {
            return;
          }

          setDatasources((current) =>
            current.map((card) => {
              const result = results.find((item) => ('health' in item ? item.health.name : item.name) === card.name);

              if (!result || !result.ok) {
                return card;
              }

              return mapDatasourceHealth(result.health);
            }),
          );
        } catch (requestError) {
          if (!active) {
            return;
          }

          notify.error(requestError instanceof Error ? requestError.message : 'Failed to load datasource configuration.');
          setDatasources([]);
        } finally {
          if (active) {
            setIsLoadingDatasources(false);
          }
        }
      })(),
      (async () => {
        try {
          const serverConfigs = await fetchExternalServerConfigs();

          if (!active) {
            return;
          }

          setServers(serverConfigs.map(mapExternalServerConfig));

          const results = await Promise.all(
            serverConfigs.map((item) =>
              fetchExternalServerHealth(item.name)
                .then((health) => ({ ok: true as const, health }))
                .catch(() => ({ ok: false as const, name: item.name })),
            ),
          );

          if (!active) {
            return;
          }

          setServers((current) =>
            current.map((card) => {
              const result = results.find((item) => ('health' in item ? item.health.name : item.name) === card.name);

              if (!result || !result.ok) {
                return card;
              }

              return mapExternalServerHealth(result.health);
            }),
          );
        } catch (requestError) {
          if (!active) {
            return;
          }

          notify.error(requestError instanceof Error ? requestError.message : 'Failed to load server configuration.');
          setServers([]);
        } finally {
          if (active) {
            setIsLoadingServers(false);
          }
        }
      })(),
      (async () => {
        try {
          const cmisConfigs = await fetchCmisRepositories().catch(() => [] as CmisRepositoryInfo[]);

          if (!active) {
            return;
          }

          setCmisRepositories(cmisConfigs.map(mapCmisRepository));

          const results = await Promise.all(
            cmisConfigs.map((item) =>
              fetchCmisHealth(item.name)
                .then((health) => ({ ok: true as const, health }))
                .catch(() => ({ ok: false as const, name: item.name })),
            ),
          );

          if (!active) {
            return;
          }

          setCmisRepositories((current) =>
            current.map((card) => {
              const result = results.find((item) => ('health' in item ? item.health.name : item.name) === card.name);

              if (!result || !result.ok) {
                return {
                  ...card,
                  connected: false,
                  message: 'Health check failed',
                };
              }

              return mapCmisHealth(result.health);
            }),
          );
        } catch (requestError) {
          if (!active) {
            return;
          }

          notify.error(requestError instanceof Error ? requestError.message : 'Failed to load CMIS repositories.');
          setCmisRepositories([]);
        } finally {
          if (active) {
            setIsLoadingCmisRepositories(false);
          }
        }
      })(),
    ]);

    return () => {
      active = false;
    };
  }, [notify]);

  return (
    <div className="flex h-full min-h-0 flex-col overflow-hidden gap-3">
      <div className="mt-2 grid min-h-0 flex-1 gap-3 overflow-auto md:grid-cols-3">
        <HealthSection
          title="Datasources"
          items={datasources}
          loading={isLoadingDatasources}
        />
        <HealthSection
          title="Servers"
          items={servers}
          loading={isLoadingServers}
        />
        <HealthSection
          title="CMIS Repositories"
          items={cmisRepositories}
          loading={isLoadingCmisRepositories}
        />
      </div>
    </div>
  );
}
