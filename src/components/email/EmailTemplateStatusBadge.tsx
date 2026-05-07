type EmailTemplateStatusBadgeProps = {
  active: boolean;
};

export function EmailTemplateStatusBadge({ active }: EmailTemplateStatusBadgeProps) {
  return (
    <span
      className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${
        active ? 'bg-emerald-50 text-emerald-600' : 'theme-soft'
      }`}
    >
      {active ? 'ACTIVE' : 'INACTIVE'}
    </span>
  );
}
