import { useEffect, useState } from 'react';
import { User } from '../../types/dashboard';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { cx } from '../../lib/utils';
import { IconButton } from '../ui/IconButton';
import { navIcons } from '../layout/navIcons';

const statusClasses: Record<User['status'], string> = {
  Active: 'bg-emerald-50 text-emerald-600',
  Pending: 'bg-amber-50 text-amber-600',
  Offline: 'theme-soft',
};

type UsersTableProps = {
  users: User[];
};

export function UsersTable({ users }: UsersTableProps) {
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize, setPageSize] = useState(20);

  const totalPages = Math.max(Math.ceil(users.length / pageSize), 1);
  const currentPage = Math.min(pageNumber, totalPages - 1);
  const startIndex = currentPage * pageSize;
  const pagedUsers = users.slice(startIndex, startIndex + pageSize);
  const hasPrevious = currentPage > 0;
  const hasNext = currentPage < totalPages - 1;
  const startPage = Math.max(0, currentPage - 2);
  const endPage = Math.min(totalPages - 1, currentPage + 2);
  const visiblePages = Array.from({ length: endPage - startPage + 1 }, (_, index) => startPage + index);

  useEffect(() => {
    if (pageNumber > totalPages - 1) {
      setPageNumber(Math.max(totalPages - 1, 0));
    }
  }, [pageNumber, totalPages]);

  function changePage(nextPage: number) {
    setPageNumber(Math.max(0, Math.min(nextPage, totalPages - 1)));
  }

  function changePageSize(nextPageSize: number) {
    setPageSize(nextPageSize);
    setPageNumber(0);
  }

  return (
    <div className="flex h-full min-h-0 flex-col overflow-hidden p-0">
      <div className="theme-table-divider flex items-center justify-between border-b px-6 py-5">
        <div>
          <h2 className="theme-strong-text text-lg font-semibold">Team members</h2>
          <p className="theme-muted-text text-sm">Recent access and account status</p>
        </div>
        <Button variant="secondary" className="rounded-full px-4 py-2 text-sm">
          Export
        </Button>
      </div>

      <div className="min-h-0 flex-1 overflow-auto">
        <table className="theme-table-divider min-w-full divide-y text-left">
          <thead className="theme-table-head text-xs uppercase tracking-[0.18em]">
            <tr>
              <th className="px-6 py-4">User</th>
              <th className="px-6 py-4">Role</th>
              <th className="px-6 py-4">Team</th>
              <th className="px-6 py-4">Status</th>
              <th className="px-6 py-4">Last login</th>
            </tr>
          </thead>
          <tbody className="theme-table-body theme-table-divider divide-y">
            {pagedUsers.map((user) => (
              <tr key={user.id} className="theme-table-row">
                <td className="px-6 py-4">
                  <div className="flex items-center gap-3">
                    <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-brand-50 font-bold text-brand-600">
                      {user.name
                        .split(' ')
                        .map((part) => part[0])
                        .join('')}
                    </div>
                    <div>
                      <p className="theme-strong-text font-semibold">{user.name}</p>
                      <p className="theme-muted-text text-sm">{user.email}</p>
                    </div>
                  </div>
                </td>
                <td className="theme-muted-text px-6 py-4 text-sm">{user.role}</td>
                <td className="theme-muted-text px-6 py-4 text-sm">{user.team}</td>
                <td className="px-6 py-4">
                  <span className={cx('rounded-full px-3 py-1 text-xs font-semibold', statusClasses[user.status])}>
                    {user.status}
                  </span>
                </td>
                <td className="theme-muted-text px-6 py-4 text-sm">{user.lastLogin}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="theme-pagination-bar sticky bottom-0 z-20 mt-auto flex flex-col gap-4 px-6 py-3 backdrop-blur lg:flex-row lg:items-center lg:justify-between">
        <div className="theme-muted-text text-sm">
          Page <span className="theme-strong-text font-semibold">{currentPage + 1}</span> / <span>{totalPages}</span> | <span>{users.length}</span> items
        </div>

        <div className="flex flex-wrap items-center gap-3">
          {/* <select
            value={pageSize}
            onChange={(event) => changePageSize(Number(event.target.value))}
            className="theme-input rounded-xl px-3 py-2 text-sm"
          >
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
          </select> */}

          <div className="flex items-center gap-1">
            <IconButton onClick={() => changePage(0)} disabled={!hasPrevious} icon={navIcons.chevronsLeft} label="First page" size="sm" />
            <IconButton onClick={() => changePage(currentPage - 1)} disabled={!hasPrevious} icon={navIcons.chevronLeft} label="Previous page" size="sm" />
            {visiblePages.map((page) => (
              <button
                type="button"
                key={page}
                onClick={() => changePage(page)}
                className={`rounded-xl px-3 py-2 text-sm font-semibold ${
                  page === currentPage ? 'theme-table-page-active' : 'theme-input'
                }`}
              >
                {page + 1}
              </button>
            ))}
            <IconButton onClick={() => changePage(currentPage + 1)} disabled={!hasNext} icon={navIcons.chevronRight} label="Next page" size="sm" />
            <IconButton onClick={() => changePage(totalPages - 1)} disabled={!hasNext} icon={navIcons.chevronsRight} label="Last page" size="sm" />
          </div>
        </div>
      </div>
    </div>
  );
}
