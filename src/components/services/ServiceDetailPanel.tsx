import { Card } from '../ui/Card';
import type { ServiceConfig } from '../../types/service';

type ServiceDetailPanelProps = {
  service: ServiceConfig | null;
  loading: boolean;
};

function DetailRow({ label, value }: { label: string; value?: string | number | null }) {
  return (
    <div className="theme-soft grid gap-1 rounded-2xl px-4 py-3">
      <span className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">{label}</span>
      <span className="theme-strong-text text-sm">{value || '-'}</span>
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

  return (
    <div className="p-6 sticky top-6">
      <div>
        <h3 className="mt-1 text-2xl font-bold text-white">
          {loading ? 'Loading...' : service?.serviceName}
        </h3>
      </div>

      {loading ? (
        <div className="theme-soft mt-8 rounded-3xl px-6 py-10 text-center text-sm">
          Loading configuration...
        </div>
      ) : (
        <div className="mt-8 space-y-6">
          <div className="grid gap-3 md:grid-cols-2">
            <DetailRow label="Application" value={service?.appName} />
            <DetailRow label="System" value={service?.systemName} />
            <DetailRow label="Type" value={service?.serviceType} />
            <DetailRow label="Version" value={service?.version} />
          </div>

          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Description</p>
            <p className="theme-soft mt-3 rounded-2xl px-4 py-4 text-sm leading-7">
              {service?.description || 'No description provided.'}
            </p>
          </div>

          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Raw configuration</p>
            <pre className="theme-code-panel mt-3 overflow-x-auto rounded-2xl p-4 text-xs leading-6">
              {service?.detailJson || JSON.stringify(service, null, 2)}
            </pre>
          </div>
        </div>
      )}
    </div>
  );
}
