import { motion } from 'framer-motion';
import { navIcons } from '../layout/navIcons';
import { IconButton } from '../ui/IconButton';
import { EmailTemplateStatusBadge } from './EmailTemplateStatusBadge';
import type { EmailTemplateRecord } from '../../types/emailTemplate';

type EmailTemplateCardProps = {
  template: EmailTemplateRecord;
  index: number;
  onView: (id: number) => void;
  onToggleStatus: (template: EmailTemplateRecord) => void;
};

type DetailRowProps = {
  label: string;
  value?: string | null;
};

function DetailRow({ label, value }: DetailRowProps) {
  return (
    <div className="flex items-start justify-between gap-3 text-sm">
      <span className="theme-muted-text">{label}</span>
      <span className="theme-strong-text text-right">{value || '-'}</span>
    </div>
  );
}

export function EmailTemplateCard({
  template,
  index,
  onView,
  onToggleStatus,
}: EmailTemplateCardProps) {
  return (
    <motion.article
      initial={{ opacity: 0, scale: 0.98, y: 10 }}
      animate={{ opacity: 1, scale: 1, y: 0 }}
      transition={{ duration: 0.22, delay: Math.min(index * 0.03, 0.18) }}
      className="rounded-2xl border border-black/5 bg-white/70 p-4 shadow-sm backdrop-blur-sm transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md dark:border-white/10 dark:bg-slate-900/60"
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="theme-muted-text text-xs uppercase tracking-[0.18em]">
            {template.templateType || 'Template'}
          </p>
          <h3 className="mt-1 truncate text-lg font-semibold text-sky-600" title={template.templateCode}>
            {template.templateCode}
          </h3>
        </div>

        <EmailTemplateStatusBadge active={template.active} />
      </div>

      <div className="mt-4">
        <p className="theme-muted-text text-xs uppercase tracking-[0.18em]">
          Title
        </p>
        <p className="theme-strong-text mt-1 truncate text-sm font-medium" title={template.title}>
          {template.title}
        </p>
      </div>

      <div className="mt-4 grid gap-2">
        <DetailRow label="Process Code" value={template.processCode} />
        <DetailRow label="Template Type" value={template.templateType} />
      </div>

      <div className="mt-5 flex items-center justify-between gap-3">
        <div className="theme-muted-text text-xs uppercase tracking-[0.18em]">
          Actions
        </div>

        <div className="flex items-center gap-2">
          {template.id ? (
            <IconButton
              onClick={() => onView(template.id!)}
              icon={navIcons.info}
              label={`View details for ${template.templateCode}`}
              size="sm"
            />
          ) : null}
          {template.id ? (
            <IconButton
              onClick={() => onToggleStatus(template)}
              icon={navIcons.power}
              label={`${template.active ? 'Disable' : 'Enable'} ${template.templateCode}`}
              size="sm"
              tone={template.active ? 'warning' : 'success'}
            />
          ) : null}
        </div>
      </div>
    </motion.article>
  );
}
