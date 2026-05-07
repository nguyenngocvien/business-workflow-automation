import { navIcons } from '../layout/navIcons';
import { Card } from '../ui/Card';
import { IconButton } from '../ui/IconButton';
import type { FileRecord, FolderRecord } from '../../types/fileManager';

type FileDetailPanelProps = {
  file: FileRecord | null;
  folder: FolderRecord | null;
  loading: boolean;
  onClose: () => void;
};

export function FileDetailPanel({ file, folder, loading, onClose }: FileDetailPanelProps) {
  const isFolder = Boolean(folder);

  return (
    <Card className="h-full overflow-auto bg-slate-950 text-white">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-semibold uppercase tracking-[0.25em] text-cyan-300">Details</p>
          <h3 className="mt-2 text-2xl font-bold">
            {loading ? 'Loading...' : isFolder ? folder?.name : file?.name || 'Select an item'}
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
          <div>ID: {file.id}</div>
          <div>Size: {file.size} KB</div>
          <div>Type: {file.contentType || '-'}</div>
          <a
            href={`/e-connector/files/${file.id}/content`}
            className="inline-flex rounded-2xl bg-brand-500 px-4 py-3 font-semibold text-white"
          >
            Download
          </a>
        </div>
      ) : folder ? (
        <div className="mt-6 space-y-4 text-sm text-slate-300">
          <div>Path: {folder.path}</div>
          <div>Created at: {folder.createdAt || '-'}</div>
          <div>
            Children: {(folder.folders?.length || 0) + (folder.files?.length || 0)}
          </div>
        </div>
      ) : (
        <div className="theme-soft mt-6 rounded-2xl px-4 py-8 text-sm">
          Choose a folder or file to view detail.
        </div>
      )}
    </Card>
  );
}
