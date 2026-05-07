import { useEffect, useState } from 'react';
import { PackageEditorCard } from '../components/database-tool/PackageEditorCard';
import { navIcons } from '../components/layout/navIcons';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import {
  fetchDatabasePackages,
  fetchDatasources,
  saveDatabasePackage,
} from '../services/databaseToolApi';
import type { DatasourceRecord } from '../types/databaseTool';

export function DatabaseToolPage() {
  const [datasources, setDatasources] = useState<DatasourceRecord[]>([]);
  const [datasource, setDatasource] = useState('');
  const [packages, setPackages] = useState<string[]>([]);
  const [pkg, setPkg] = useState('');
  const [packageDefinition, setPackageDefinition] = useState('');
  const [packageBody, setPackageBody] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const notify = useNotify();

  useEffect(() => {
    let active = true;

    void fetchDatasources()
      .then((response) => {
        if (!active) {
          return;
        }
        setDatasources(response);
      })
      .catch((requestError) => {
        if (!active) {
          return;
        }
        notify.error(requestError instanceof Error ? requestError.message : 'Failed to load datasources.');
      });

    return () => {
      active = false;
    };
  }, [notify]);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setPackages([]);
    setPackageDefinition('');
    setPackageBody('');

    void fetchDatabasePackages(datasource, '')
      .then((response) => {
        if (!active) {
          return;
        }
        setPackages(response.packages || []);
      })
      .catch((requestError) => {
        if (active) {
          notify.error(requestError instanceof Error ? requestError.message : 'Failed to load package data.');
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, [datasource, notify]);

  async function handleSearchPackage() {
    if (!datasource || !pkg) {
      setPackageDefinition('');
      setPackageBody('');
      return;
    }

    setLoading(true);
    try {
      const response = await fetchDatabasePackages(datasource, pkg);
      setPackageDefinition(response.packageDefinition || '');
      setPackageBody(response.packageBody || '');
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to load package data.');
    } finally {
      setLoading(false);
    }
  }

  async function handleSave() {
    setSaving(true);
    try {
      const response = await saveDatabasePackage({
        datasource,
        pkg,
        packageDefinition,
        packageBody,
      });
      notify.success(response.message || 'Package saved');
    } catch (saveError) {
      notify.error(saveError instanceof Error ? saveError.message : 'Failed to save package.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="p-4 flex h-full min-h-0 flex-col gap-4 overflow-hidden">
      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4 p-4">
        <label className="grid gap-2 text-sm">
          <select
            value={datasource}
            onChange={(event) => {
              setDatasource(event.target.value);
              setPkg('');
            }}
            className="theme-input"
          >
            <option value="">-- Select Datasource --</option>
            {datasources.map((item) => (
              <option key={item.datasourceName} value={item.datasourceName}>
                {item.label}
              </option>
            ))}
          </select>
        </label>

        <label className="grid gap-2 text-sm">
          <select
            value={pkg}
            onChange={(event) => {
              setPkg(event.target.value);
              setPackageDefinition('');
              setPackageBody('');
            }}
            className="theme-input"
            disabled={!datasource}
          >
            <option value="">-- Select package --</option>
            {packages.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>
        </label>
        <div className="flex items-end">
          <IconButton
            onClick={() => void handleSearchPackage()}
            icon={navIcons.search}
            label="Load package definition and package body"
            tone="primary"
            size="sm"
            disabled={!datasource || !pkg || loading}
            className="h-9 w-9 rounded-full"
          />
        </div>
      </div>
      <div className="grid min-h-0 flex-1 gap-2 overflow-y-auto pb-1 xl:auto-rows-fr xl:overflow-hidden xl:grid-cols-[minmax(320px,0.85fr)_minmax(0,1.35fr)]">
        <PackageEditorCard
          title="Package"
          value={packageDefinition}
          rows={22}
          onChange={setPackageDefinition}
          onSave={() => void handleSave()}
          disabled={saving || loading || !datasource}
        />

        <PackageEditorCard
          title="Package Body"
          value={packageBody}
          rows={22}
          onChange={setPackageBody}
          onSave={() => void handleSave()}
          disabled={saving || loading || !datasource}
        />
      </div>
    </div>
  );
}
