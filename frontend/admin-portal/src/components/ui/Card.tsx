import type { PropsWithChildren } from 'react';
import { cx } from '../../lib/utils';

type CardProps = PropsWithChildren<{
  className?: string;
}>;

export function Card({ children, className }: CardProps) {
  return (
    <section className={cx('theme-card rounded-xl p-4 shadow-panel', className)}>
      {children}
    </section>
  );
}
