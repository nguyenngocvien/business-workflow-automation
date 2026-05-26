import { navIcons } from '../layout/navIcons';
import { IconButton } from '../ui/IconButton';
import type { PageFileResult } from '../../api/document';

type FilePaginationProps = {
  page: PageFileResult;
  onPageChange: (page: number) => void;
  onPageSizeChange?: (size: number) => void;
};

export function FilePagination({ page, onPageChange, onPageSizeChange }: FilePaginationProps) {
  const pageNumber = page.number ?? page.pageable?.pageNumber ?? 0;
  const pageSize = page.size ?? page.pageable?.pageSize ?? 0;
  const totalPages = page.totalPages ?? 0;
  const hasPrevious = Boolean(page.first === false && pageNumber > 0);
  const hasNext = Boolean(page.last === false && pageNumber + 1 < totalPages);

  return (
    <div className="theme-pagination-bar sticky bottom-0 z-20 mt-auto flex flex-col gap-4 px-6 py-3 backdrop-blur lg:flex-row lg:items-center lg:justify-between">
      <div className="text-sm text-slate-500">
        Page <span className="font-semibold text-slate-900">{pageNumber + 1}</span> of{' '}
        <span>{Math.max(totalPages, 1)}</span> | <span>{page.totalElements ?? 0}</span> items
      </div>

      <div className="flex items-center gap-3">
        {onPageSizeChange ? (
          <select
            value={pageSize}
            onChange={(event) => onPageSizeChange(Number(event.target.value))}
            className="theme-input rounded-xl px-2.5 py-1.5 text-xs"
            aria-label="Page size"
          >
            <option value={20}>20</option>
            <option value={50}>50</option>
            <option value={100}>100</option>
          </select>
        ) : null}

        <div className="flex items-center gap-1">
          <IconButton
            onClick={() => onPageChange(0)}
            disabled={!hasPrevious}
            icon={navIcons.chevronsLeft}
            label="First page"
            size="sm"
          />
          <IconButton
            onClick={() => onPageChange(Math.max(0, pageNumber - 1))}
            disabled={!hasPrevious}
            icon={navIcons.chevronLeft}
            label="Previous page"
            size="sm"
          />
          <span className="rounded-xl bg-slate-900 px-3 py-2 text-sm font-semibold text-white">
            {pageNumber + 1}
          </span>
          <IconButton
            onClick={() => onPageChange(pageNumber + 1)}
            disabled={!hasNext}
            icon={navIcons.chevronRight}
            label="Next page"
            size="sm"
          />
        </div>
      </div>
    </div>
  );
}
