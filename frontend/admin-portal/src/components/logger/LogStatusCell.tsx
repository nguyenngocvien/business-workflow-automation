import { cx } from '../../lib/utils';

type LogStatusCellProps = {
  errorCode?: string;
  errorMessage?: string;
};

function isSuccess(errorCode?: string) {
  return errorCode === '0' || errorCode === '00' || errorCode === 'S';
}

export function LogStatusCell({ errorCode, errorMessage }: LogStatusCellProps) {
  const success = isSuccess(errorCode);

  return (
    <div>
      <div className={cx('text-sm font-semibold', success ? 'text-emerald-600' : 'text-rose-600')}>
        {errorCode || '-'}
      </div>
      <div className="max-w-[240px] truncate text-xs text-slate-500" title={errorMessage || ''}>
        {errorMessage || '-'}
      </div>
    </div>
  );
}

export function isLogSuccess(errorCode?: string) {
  return isSuccess(errorCode);
}
