import type { ServiceResult } from '../../api/connector';
import { Card } from '../ui/Card';
import type { ServiceConfig } from '../../types/service';

type ServiceDetailModel = ServiceConfig & Partial<Omit<ServiceResult, 'serviceType'>>;

type ServiceDetailPanelProps = {
  service: ServiceDetailModel | null;
  loading: boolean;
};

function DetailRow({ label, value }: { label: string; value?: string | number | null }) {
  return (
    <div className="theme-soft grid gap-1 rounded-2xl px-4 py-3">
      <span className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">{label}</span>
      <span className="theme-strong-text text-sm">{value ?? '-'}</span>
    </div>
  );
}

export function ServiceDetailPanel({ service, loading }: ServiceDetailPanelProps) {
  if (!service && !loading) {
    return (
      <Card className="bg-slate-950 text-white">
        <h3 className="mt-3 text-2xl font-bold">Select a service</h3>
      </Card>
    );
  }

  const isActive = Boolean(service?.active ?? service?.status);
  const isLoggingEnabled = Boolean(service?.logEnable ?? service?.logOn);
  const serviceType = service?.serviceType ?? '-';
  const version = service?.serviceVersion ?? service?.version ?? '-';
  const description = service?.description ?? 'No description provided.';

  return (
    <div className="sticky top-6 p-6">
      <div>
        <h3 className="mt-1 text-2xl font-bold text-white">
          {loading ? 'Loading...' : service?.serviceName ?? service?.serviceCode ?? 'Service detail'}
        </h3>
      </div>

      {loading ? (
        <div className="theme-soft mt-8 rounded-3xl px-6 py-10 text-center text-sm">
          Loading configuration...
        </div>
      ) : (
        <div className="mt-8 space-y-6">
          <div className="grid gap-3 md:grid-cols-2">
            <DetailRow label="ID" value={service?.id} />
            <DetailRow label="Application" value={service?.appId ?? service?.appName} />
            <DetailRow label="System" value={service?.serviceCode ?? service?.systemName} />
            <DetailRow label="Type" value={serviceType} />
            <DetailRow label="Version" value={version} />
            <DetailRow label="Active" value={isActive ? 'Yes' : 'No'} />
            <DetailRow label="Logging" value={isLoggingEnabled ? 'Enabled' : 'Disabled'} />
            <DetailRow label="Created by" value={service?.createdBy} />
            <DetailRow label="Updated by" value={service?.updatedBy} />
          </div>

          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Description</p>
            <p className="theme-soft mt-3 rounded-2xl px-4 py-4 text-sm leading-7">
              {description}
            </p>
          </div>

          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Raw configuration</p>
            <pre className="theme-code-panel mt-3 overflow-x-auto rounded-2xl p-4 text-xs leading-6">
              {service?.detailJson ?? service?.configJson ?? JSON.stringify(service, null, 2)}
            </pre>
          </div>
        </div>
      )}
    </div>
  );
}
