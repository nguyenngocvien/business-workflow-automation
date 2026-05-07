import { navIcons } from '../layout/navIcons';
import { Card } from '../ui/Card';
import { IconButton } from '../ui/IconButton';
import type { MasterDataRecord } from '../../types/masterData';

type MasterDataEditorProps = {
  item: MasterDataRecord | null;
  onChange: (item: MasterDataRecord) => void;
  onSave: () => void;
  onCancel: () => void;
  saving: boolean;
};

export function MasterDataEditor({
  item,
  onChange,
  onSave,
  onCancel,
  saving,
}: MasterDataEditorProps) {
  if (!item) {
    return (
      <Card className='transition-all duration-300'>
        <p className="text-sm font-semibold uppercase tracking-[0.25em] text-cyan-300">Master data</p>
        <h3 className="mt-3 text-2xl font-bold">Select or create an item</h3>
      </Card>
    );
  }

  return (
    <Card className="relative h-full overflow-auto">

      {/* Header */}
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3 className="mt-2 text-2xl font-bold">
            {item.id ? 'Edit Master Data' : 'Add Master Data'}
          </h3>
        </div>

        {/* chỉ giữ Cancel */}
        <IconButton
          onClick={onCancel}
          icon={navIcons.close}
          label="Cancel edit"
        />
      </div>

      {/* Form */}
      <div className="mt-8 grid gap-4 pb-16">
        <label className="grid gap-2 text-sm">
          <span>Group Code</span>
          <input
            value={item.groupCode}
            onChange={(event) =>
              onChange({ ...item, groupCode: event.target.value })
            }
            className="theme-input"
          />
        </label>
        <label className="grid gap-2 text-sm">
          <span>Code</span>
          <input
            value={item.code}
            onChange={(event) =>
              onChange({ ...item, code: event.target.value })
            }
            className="theme-input"
            required
          />
        </label>
        <label className="grid gap-2 text-sm">
          <span>Name</span>
          <input
            value={item.name}
            onChange={(event) =>
              onChange({ ...item, name: event.target.value })
            }
            className="theme-input"
            required
          />
        </label>

        <label className="theme-input flex items-center gap-3">
          <input
            type="checkbox"
            checked={item.active}
            onChange={(event) =>
              onChange({ ...item, active: event.target.checked })
            }
          />
          Active
        </label>
      </div>

      {/* Floating Save Button */}
      <button
        onClick={onSave}
        disabled={saving}
        className="
        absolute bottom-4 right-4
        flex items-center justify-center
        rounded-xl
        bg-[rgba(var(--color-success),0.15)]
        p-2
        transition
        hover:bg-[rgba(var(--color-success),0.25)]
        disabled:opacity-50
      "
        title="Save master data"
      >
        <svg className="h-5 w-5 text-[rgb(var(--color-success))]">
          {navIcons.save}
        </svg>
      </button>
    </Card>
  );
}
