import { navIcons } from '../layout/navIcons';
import { IconButton } from '../ui/IconButton';

type PackageEditorCardProps = {
  title: string;
  value: string;
  rows: number;
  onChange: (value: string) => void;
  onSave: () => void;
  disabled?: boolean;
};

export function PackageEditorCard({
  title,
  value,
  rows,
  onChange,
  onSave,
  disabled,
}: PackageEditorCardProps) {
  return (
    <div className="flex h-full min-h-[320px] flex-col overflow-hidden xl:min-h-0">
      <label className="theme-strong-text text-sm font-semibold">{title}</label>

      {/* Wrapper để đặt absolute */}
      <div className="relative mt-2 flex-1">
        <textarea
          value={value}
          onChange={(event) => onChange(event.target.value)}
          rows={rows}
          className="theme-code-editor h-full w-full resize-none overflow-auto rounded-xl px-4 py-3 pr-12 font-mono text-sm leading-6"
          placeholder={
            title === 'Package'
              ? 'CREATE OR REPLACE PACKAGE ...'
              : 'CREATE OR REPLACE PACKAGE BODY ...'
          }
        />

        {/* Save button (floating) */}
        <button
          onClick={onSave}
          disabled={disabled}
          className="cursor-pointer absolute bottom-2 right-2 flex items-center justify-center rounded-lg bg-[rgba(var(--color-info),0.15)] p-2 transition hover:bg-[rgba(var(--color-info),0.25)] disabled:opacity-50"
          title={`Save ${title}`}
        >
          <svg className="h-5 w-5 text-[rgb(var(--color-info))]">
            {navIcons.save}
          </svg>
        </button>
      </div>
    </div>
  );
}
