import { cx } from '../../lib/utils';

type ServiceStatusBadgeProps = {
  active: boolean;
  activeLabel: string;
  inactiveLabel: string;
  activeTone?: string;
  inactiveTone?: string;
};

export function ServiceStatusBadge({
  active,
  activeLabel,
  inactiveLabel,
  activeTone = 'bg-emerald-50 text-emerald-600',
  inactiveTone = 'theme-soft',
}: ServiceStatusBadgeProps) {
  return (
    <span
      className={cx(
        'inline-flex items-center justify-center rounded-full px-3 py-1 text-xs font-semibold',
        active ? activeTone : inactiveTone,
      )}
    >
      {active ? activeLabel : inactiveLabel}
    </span>
  );
}
