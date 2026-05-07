import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { cx } from '../../lib/utils';

type IconButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  icon: ReactNode;
  label: string;
  tone?: 'primary' | 'dark' | 'neutral' | 'danger' | 'warning' | 'success';
  size?: 'sm' | 'md';
};

const toneClasses: Record<NonNullable<IconButtonProps['tone']>, string> = {
  primary: 'bg-brand-500 text-white shadow-lg shadow-brand-500/20 hover:bg-brand-600',
  dark: 'bg-slate-900 text-white hover:bg-slate-800',
  neutral: 'theme-surface hover:opacity-90',
  danger: 'border border-rose-200 bg-rose-50 text-rose-700 hover:bg-rose-100',
  warning: 'border border-amber-200 bg-amber-50 text-amber-700 hover:bg-amber-100',
  success: 'border border-emerald-200 bg-emerald-50 text-emerald-700 hover:bg-emerald-100',
};

const sizeClasses: Record<NonNullable<IconButtonProps['size']>, string> = {
  sm: 'h-9 w-9 rounded-full',
  md: 'h-12 w-12 rounded-xl',
};

export function IconButton({
  icon,
  label,
  className,
  tone = 'neutral',
  size = 'md',
  type = 'button',
  ...props
}: IconButtonProps) {
  return (
    <button
      type={type}
      aria-label={label}
      title={label}
      className={cx(
        'p-2 inline-flex items-center justify-center transition disabled:cursor-not-allowed disabled:opacity-40',
        toneClasses[tone],
        sizeClasses[size],
        className,
      )}
      {...props}
    >
      <svg viewBox="0 0 24 24" className={size === 'sm' ? 'h-4.5 w-4.5' : 'h-5 w-5'}>
        {icon}
      </svg>
    </button>
  );
}
