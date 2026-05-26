import { navIcons } from '../layout/navIcons';
import { Card } from '../ui/Card';
import { IconButton } from '../ui/IconButton';
import type { FileResult } from '../../api/document';

type FileDetailPanelProps = {
  file: FileResult | null;
  loading: boolean;
  onClose: () => void;
};

function formatFileSize(size?: number) {
  if (typeof size !== 'number') {
    return '-';
  }

  return `${Math.max(size, 0)} KB`;
}

export function FileDetailPanel({ file, loading, onClose }: FileDetailPanelProps) {
  return (
    <Card className="h-full overflow-auto bg-slate-950 text-white">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-semibold uppercase tracking-[0.25em] text-cyan-300">Details</p>
          <h3 className="mt-2 text-2xl font-bold">
            {loading ? 'Loading...' : file?.fileName || 'Select a file'}
          </h3>
        </div>
        <IconButton
          onClick={onClose}
          icon={navIcons.close}
          label="Close details"
          size="sm"
          className="border-white/10 bg-transparent text-slate-300 hover:bg-white/10"
        />
      </div>

      {loading ? (
        <div className="theme-soft mt-6 rounded-2xl px-4 py-8 text-sm">Loading detail...</div>
      ) : file ? (
        <div className="mt-6 space-y-4 text-sm text-slate-300">
          <div>
            <span className="text-slate-400">ID:</span> {file.id ?? '-'}
          </div>
          <div>
            <span className="text-slate-400">File name:</span> {file.fileName ?? '-'}
          </div>
          <div>
            <span className="text-slate-400">Size:</span> {formatFileSize(file.size)}
          </div>
          <div>
            <span className="text-slate-400">Content type:</span> {file.contentType || '-'}
          </div>
          <div>
            <span className="text-slate-400">Created at:</span> {file.createdAt || '-'}
          </div>
        </div>
      ) : (
        <div className="theme-soft mt-6 rounded-2xl px-4 py-8 text-sm">
          Choose a file to view detail.
        </div>
      )}
    </Card>
  );
}
