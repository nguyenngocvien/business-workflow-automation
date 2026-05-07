import { FormEvent, useEffect, useState } from 'react';
import { navIcons } from '../components/layout/navIcons';
import { MasterDataEditor } from '../components/metadata/MasterDataEditor';
import { Card } from '../components/ui/Card';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import {
  createMasterData,
  deleteMasterData,
  fetchMasterData,
  updateMasterData,
  updateMasterDataActive,
} from '../services/masterDataApi';
import type { MasterDataRecord, MasterDataSearchFilters } from '../types/masterData';

const defaultFilters: MasterDataSearchFilters = {
  code: '',
  name: '',
  groupCode: '',
  active: '',
};

const emptyItem: MasterDataRecord = {
  code: '',
  name: '',
  groupCode: '',
  active: true,
};

export function MasterDataPage() {
  const [draftFilters, setDraftFilters] = useState<MasterDataSearchFilters>(defaultFilters);
  const [filters, setFilters] = useState<MasterDataSearchFilters>(defaultFilters);
  const [items, setItems] = useState<MasterDataRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [selectedItem, setSelectedItem] = useState<MasterDataRecord | null>(null);
  const notify = useNotify();

  useEffect(() => {
    let active = true;
    setLoading(true);

    void fetchMasterData(filters)
      .then((response) => {
        if (active) {
          setItems(response);
        }
      })
      .catch((requestError) => {
        if (active) {
          notify.error(requestError instanceof Error ? requestError.message : 'Failed to load master data.');
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
  }, [filters, notify]);


  useEffect(() => {
    if (selectedItem) {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, [selectedItem]);

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setSelectedItem(null);
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, []);

  function submitSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFilters(draftFilters);
  }

  async function handleSave() {
    if (!selectedItem) {
      return;
    }

    setSaving(true);
    try {
      if (selectedItem.id) {
        await updateMasterData(selectedItem);
        notify.success('Master data updated.');
      } else {
        await createMasterData(selectedItem);
        notify.success('Master data created.');
      }
      setSelectedItem(null);
      setFilters((current) => ({ ...current }));
    } catch (saveError) {
      notify.error(saveError instanceof Error ? saveError.message : 'Failed to save master data.');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(id: number) {
    try {
      await deleteMasterData(id);
      notify.success('Master data deleted.');
      setFilters((current) => ({ ...current }));
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to delete master data.');
    }
  }

  async function handleToggleActive(item: MasterDataRecord) {
    if (!item.id) {
      return;
    }
    try {
      await updateMasterDataActive(item.id, !item.active);
      notify.success(`${item.code} ${item.active ? 'disabled' : 'enabled'}.`);
      setFilters((current) => ({ ...current }));
    } catch (requestError) {
      notify.error(requestError instanceof Error ? requestError.message : 'Failed to update master data status.');
    }
  }

  return (
    <div className="p-4 flex gap-3 h-full min-h-0 flex-col overflow-hidden">
      <form className="py-4 grid gap-3 lg:grid-cols-[repeat(4,minmax(0,1fr))_auto_auto]" onSubmit={submitSearch}>
        <input
          value={draftFilters.code}
          onChange={(event) => setDraftFilters((current) => ({ ...current, code: event.target.value }))}
          className="theme-input"
          placeholder="Code"
        />
        <input
          value={draftFilters.name}
          onChange={(event) => setDraftFilters((current) => ({ ...current, name: event.target.value }))}
          className="theme-input"
          placeholder="Name"
        />
        <input
          value={draftFilters.groupCode}
          onChange={(event) => setDraftFilters((current) => ({ ...current, groupCode: event.target.value }))}
          className="theme-input"
          placeholder="Group Code"
        />
        <select
          value={draftFilters.active}
          onChange={(event) =>
            setDraftFilters((current) => ({
              ...current,
              active: event.target.value as MasterDataSearchFilters['active'],
            }))
          }
          className="theme-input"
        >
          <option value="">All</option>
          <option value="true">Active</option>
          <option value="false">Inactive</option>
        </select>
        <IconButton type="submit" icon={navIcons.search} label="Search master data" tone="primary" size="sm" />
        <IconButton onClick={() => setSelectedItem({ ...emptyItem })} icon={navIcons.plus} label="Add master data" tone="success" size="sm" />
      </form>

      <div
        className={`grid min-h-0 flex-1 gap-3 overflow-hidden ${selectedItem
          ? 'xl:grid-cols-[minmax(0,1.45fr)_minmax(360px,0.9fr)]'
          : 'grid-cols-1'
          }`}
      >
        <div className="flex min-h-0 flex-1 flex-col overflow-hidden p-0">
          <div className="overflow-x-auto">
            <table className="theme-table-divider min-w-full divide-y">
              <thead className="theme-table-head text-left text-xs uppercase tracking-[0.18em]">
                <tr>
                  <th className="px-6 py-4">ID</th>
                  <th className="px-6 py-4">Code</th>
                  <th className="px-6 py-4">Name</th>
                  <th className="px-6 py-4">Group Code</th>
                  <th className="px-6 py-4 text-center">Active</th>
                  <th className="px-6 py-4 text-center">Action</th>
                </tr>
              </thead>
              <tbody className="theme-table-body theme-table-divider divide-y">
                {loading ? (
                  <tr>
                    <td colSpan={6} className="px-6 py-16 text-center text-sm text-slate-500">
                      Loading master data...
                    </td>
                  </tr>
                ) : items.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="px-6 py-16 text-center text-sm text-slate-500">
                      No master data found.
                    </td>
                  </tr>
                ) : (
                  items.map((item) => (
                    <tr key={item.id} className="theme-table-row">
                      <td className="theme-muted-text px-6 py-4 text-sm">{item.id}</td>
                      <td className="px-6 py-4 text-sm font-semibold text-sky-600">{item.code}</td>
                      <td className="theme-muted-text px-6 py-4 text-sm">{item.name}</td>
                      <td className="theme-muted-text px-6 py-4 text-sm">{item.groupCode}</td>
                      <td className="px-6 py-4 text-center">
                        <span
                          className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${item.active ? 'bg-emerald-50 text-emerald-600' : 'bg-slate-100 text-slate-500'
                            }`}
                        >
                          {item.active ? 'ACTIVE' : 'INACTIVE'}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex flex-wrap justify-center gap-2">
                          <IconButton onClick={() => setSelectedItem({ ...item })} icon={navIcons.edit} label={`Edit ${item.code}`} size="sm" />
                          <IconButton onClick={() => void handleToggleActive(item)} icon={navIcons.power} label={`${item.active ? 'Disable' : 'Enable'} ${item.code}`} size="sm" tone="warning" />
                          {item.id ? (
                            <IconButton onClick={() => void handleDelete(item.id!)} icon={navIcons.trash} label={`Delete ${item.code}`} size="sm" tone="danger" />
                          ) : null}
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        {selectedItem && (
          <MasterDataEditor
            item={selectedItem}
            onChange={setSelectedItem}
            onSave={() => void handleSave()}
            onCancel={() => setSelectedItem(null)}
            saving={saving}
          />
        )}
      </div>
    </div>
  );
}
