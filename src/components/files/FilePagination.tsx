import { navIcons } from '../layout/navIcons';
import { IconButton } from '../ui/IconButton';
import type { FilePageResult } from '../../types/fileManager';

type FilePaginationProps = {
  page: FilePageResult;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
};

export function FilePagination({ page, onPageChange, onPageSizeChange }: FilePaginationProps) {
  return (
    <div className="theme-pagination-bar sticky bottom-0 z-20 mt-auto flex flex-col gap-4 px-6 py-3 backdrop-blur lg:flex-row lg:items-center lg:justify-between">
      <div className="text-sm text-slate-500">
        Page <span className="font-semibold text-slate-900">{page.pageNumber + 1}</span> |{' '}
        <span>{page.items.length}</span> items
      </div>

      <div className="flex items-center gap-3">
        {/* <select
          value={page.pageSize}
          onChange={(event) => onPageSizeChange(Number(event.target.value))}
          className="theme-input rounded-xl px-2.5 py-1.5 text-xs"
        >
          <option value={20}>20</option>
          <option value={50}>50</option>
          <option value={100}>100</option>
        </select> */}

        <div className="flex items-center gap-1">
          <IconButton onClick={() => onPageChange(0)} disabled={!page.hasPrevious} icon={navIcons.chevronsLeft} label="First page" size="sm" />
          <IconButton onClick={() => onPageChange(Math.max(0, page.pageNumber - 1))} disabled={!page.hasPrevious} icon={navIcons.chevronLeft} label="Previous page" size="sm" />
          <span className="rounded-xl bg-slate-900 px-3 py-2 text-sm font-semibold text-white">
            {page.pageNumber + 1}
          </span>
          <IconButton onClick={() => onPageChange(page.pageNumber + 1)} disabled={!page.hasNext} icon={navIcons.chevronRight} label="Next page" size="sm" />
        </div>
      </div>
    </div>
  );
}
