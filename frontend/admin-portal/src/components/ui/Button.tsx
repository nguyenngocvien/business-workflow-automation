import type { ButtonHTMLAttributes, PropsWithChildren } from 'react';
import { cx } from '../../lib/utils';

type ButtonProps = PropsWithChildren<
  ButtonHTMLAttributes<HTMLButtonElement> & {
    variant?: 'primary' | 'secondary';
    fullWidth?: boolean;
  }
>;

export function Button({
  children,
  className,
  variant = 'primary',
  fullWidth,
  ...props
}: ButtonProps) {
  return (
    <button
      className={cx(
        'inline-flex items-center justify-center rounded-2xl px-4 py-3 text-sm font-semibold transition duration-200',
        'focus:outline-none focus:ring-2 focus:ring-brand-500/40 disabled:cursor-not-allowed disabled:opacity-60',
        variant === 'primary' &&
          'bg-brand-500 text-white shadow-lg shadow-brand-500/25 hover:bg-brand-600',
        variant === 'secondary' &&
          'theme-surface hover:opacity-90',
        fullWidth && 'w-full',
        className,
      )}
      {...props}
    >
      {children}
    </button>
  );
}
