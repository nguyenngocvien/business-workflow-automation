import { useState } from 'react';
import { navIcons } from '../layout/navIcons';
import { IconButton } from '../ui/IconButton';

type LogPayloadSectionProps = {
  title: string;
  content?: string;
  accentClass: string;
};

export function LogPayloadSection({ title, content, accentClass }: LogPayloadSectionProps) {
  const [open, setOpen] = useState(true);

  async function copy() {
    await navigator.clipboard.writeText(content || '');
  }

  return (
    <div className="theme-surface rounded-2xl">
      <div className="flex items-center justify-between px-4 py-3">
        <button
          type="button"
          onClick={() => setOpen((current) => !current)}
          className={`text-sm font-bold ${accentClass}`}
        >
          {title}
        </button>
        <IconButton onClick={() => void copy()} icon={navIcons.logs} label={`Copy ${title}`} size="sm" />
      </div>

      {open ? (
        <pre className={`theme-code-panel overflow-x-auto rounded-b-2xl p-4 text-xs leading-6 ${accentClass}`}>
          {content || 'No content'}
        </pre>
      ) : null}
    </div>
  );
}
