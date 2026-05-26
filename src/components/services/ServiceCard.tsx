import { motion } from 'framer-motion';
import type { ServiceResult } from '../../api/connector';
import { navIcons } from '../layout/navIcons';
import { IconButton } from '../ui/IconButton';
import { ServiceStatusBadge } from './ServiceStatusBadge';
import type { ServiceConfig } from '../../types/service';

type ServiceCardModel = ServiceConfig & Partial<Omit<ServiceResult, 'serviceType'>>;

type ServiceCardProps = {
  service: ServiceCardModel;
  index: number;
  onView: (service: ServiceCardModel) => void;
  onEdit: (service: ServiceCardModel) => void;
  onViewLogs: (service: ServiceCardModel) => void;
  onToggleActive: (service: ServiceCardModel) => void;
  onToggleLog: (service: ServiceCardModel) => void;
};

type InfoRowProps = {
  label: string;
  value?: string | number | null;
};

function InfoRow({ label, value }: InfoRowProps) {
  return (
    <div className="flex items-start justify-between gap-3 text-sm">
      <span className="theme-muted-text">{label}</span>
      <span className="theme-strong-text text-right">{value ?? '-'}</span>
    </div>
  );
}

export function ServiceCard({
  service,
  index,
  onView,
  onEdit,
  onViewLogs,
  onToggleActive,
  onToggleLog,
}: ServiceCardProps) {
  const isActive = Boolean(service.active ?? service.status);
  const isLoggingEnabled = Boolean(service.logEnable ?? service.logOn);
  const serviceType = service.serviceType ?? '';
  const appLabel = service.appId ?? service.appName ?? 'Unknown app';
  const serviceLabel = service.serviceName ?? service.serviceCode ?? 'Untitled Service';
  const versionLabel = service.serviceVersion ?? service.version ?? '-';
  const description = service.description ?? service.configJson ?? 'No description provided.';

  return (
    <motion.article
      initial={{ opacity: 0, scale: 0.98, y: 10 }}
      animate={{ opacity: 1, scale: 1, y: 0 }}
      transition={{ duration: 0.22, delay: Math.min(index * 0.03, 0.18) }}
      whileHover={{ y: -2, scale: 1.01 }}
      className="rounded-2xl border border-black/5 bg-white/70 p-4 shadow-sm backdrop-blur transition-all hover:-translate-y-0.5 hover:shadow-md dark:border-white/10 dark:bg-slate-900/60"
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="theme-muted-text text-xs uppercase tracking-[0.18em]">Service</p>
          <h3 className="mt-1 truncate text-lg font-semibold theme-strong-text">{serviceLabel}</h3>
          <p className="theme-muted-text mt-1 text-sm">
            {appLabel} / {service.systemName ?? service.serviceCode ?? 'Unknown system'}
          </p>
        </div>

        <ServiceStatusBadge active={isActive} activeLabel="Active" inactiveLabel="Inactive" />
      </div>

      <div className="mt-4 grid gap-2">
        <InfoRow label="ID" value={service.id} />
        <InfoRow label="Application" value={service.appId ?? service.appName} />
        <InfoRow label="System" value={service.serviceCode ?? service.systemName} />
        <InfoRow label="Version" value={versionLabel} />
        <InfoRow label="Type" value={serviceType} />
      </div>

      <div className="mt-4 flex flex-wrap items-center gap-2">
        <ServiceStatusBadge
          active={isLoggingEnabled}
          activeLabel="Log On"
          inactiveLabel="Log Off"
          activeTone="bg-sky-50 text-sky-600 dark:bg-sky-500/15 dark:text-sky-300"
          inactiveTone="bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300"
        />
      </div>

      {description ? (
        <p className="theme-muted-text mt-4 line-clamp-2 text-sm">
          {description}
        </p>
      ) : null}

      <div className="mt-5 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => onView(service)}
            className="rounded-xl border border-[var(--border-subtle)] px-3 py-2 text-sm font-medium theme-strong-text transition hover:bg-[var(--surface-input)]"
          >
            View
          </button>
          <button
            type="button"
            onClick={() => onEdit(service)}
            className="rounded-xl border border-[var(--border-subtle)] px-3 py-2 text-sm font-medium theme-strong-text transition hover:bg-[var(--surface-input)]"
          >
            Edit
          </button>
          <button
            type="button"
            disabled
            aria-disabled="true"
            className="cursor-not-allowed rounded-xl border border-[var(--border-subtle)] px-3 py-2 text-sm font-medium theme-muted-text opacity-60"
            title="Delete is not available yet"
          >
            Delete
          </button>
        </div>

        <div className="flex items-center gap-2">
          <IconButton
            onClick={() => onViewLogs(service)}
            icon={navIcons.logs}
            label={`View logs for ${serviceLabel}`}
            size="sm"
          />
          <IconButton
            onClick={() => onToggleActive(service)}
            icon={navIcons.power}
            label={`${isActive ? 'Disable' : 'Enable'} ${serviceLabel}`}
            size="sm"
            tone="warning"
          />
          <IconButton
            onClick={() => onToggleLog(service)}
            icon={navIcons.sliders}
            label={`${isLoggingEnabled ? 'Disable' : 'Enable'} logs for ${serviceLabel}`}
            size="sm"
            tone="neutral"
          />
        </div>
      </div>
    </motion.article>
  );
}
