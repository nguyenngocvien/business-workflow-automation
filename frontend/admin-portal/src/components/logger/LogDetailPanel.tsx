import { Card } from '../ui/Card';
import type { LogResult } from '../../api/connector';
import { LogPayloadSection } from './LogPayloadSection';

type LogDetailPanelProps = {
  log: LogResult | null;
  loading: boolean;
};

function MetaRow({ label, value }: { label: string; value?: string | number | null }) {
  return (
    <div className="theme-soft grid gap-1 rounded-2xl px-4 py-3">
      <span className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">{label}</span>
      <span className="theme-strong-text text-sm">{value ?? '-'}</span>
    </div>
  );
}

function isSuccessfulLog(statusCode?: number) {
  return typeof statusCode !== 'number' || statusCode === 0 || (statusCode >= 200 && statusCode < 400);
}

function formatDuration(durationMs?: number) {
  if (typeof durationMs !== 'number') {
    return '-';
  }

  return `${durationMs} ms`;
}

export function LogDetailPanel({ log, loading }: LogDetailPanelProps) {
  if (!log && !loading) {
    return (
      <Card className="bg-slate-950 text-white">
        <h3 className="mt-3 text-2xl font-bold">Select a log row</h3>
        <p className="mt-3 text-sm leading-7 text-slate-300">
          The detail panel mirrors the generated connector API. Choose a log entry to inspect request, response, and
          transport metadata.
        </p>
      </Card>
    );
  }

  const success = isSuccessfulLog(log?.statusCode);

  return (
    <Card className="sticky top-6">
      <div>
        <h3 className="mt-1 text-2xl font-bold text-slate-950">
          {loading ? 'Loading...' : `Log #${log?.id ?? '-'}`}
        </h3>
        <p className={`mt-2 text-sm font-semibold ${success ? 'text-emerald-600' : 'text-rose-600'}`}>
          {loading
            ? 'Fetching log detail.'
            : log?.errorMessage || (log?.statusCode !== undefined ? `Status ${log.statusCode}` : 'No message')}
        </p>
      </div>

      {loading ? (
        <div className="theme-soft mt-8 rounded-3xl px-6 py-10 text-center text-sm">
          Loading log detail...
        </div>
      ) : (
        <div className="mt-8 space-y-4">
          <div className="grid gap-3 md:grid-cols-2">
            <MetaRow label="ID" value={log?.id} />
            <MetaRow label="Service ID" value={log?.serviceId} />
            <MetaRow label="Trace ID" value={log?.traceId} />
            <MetaRow label="Correlation ID" value={log?.correlationId} />
            <MetaRow label="Status code" value={log?.statusCode} />
            <MetaRow label="Duration" value={formatDuration(log?.durationMs)} />
            <MetaRow label="Request time" value={log?.requestTime} />
            <MetaRow label="Response time" value={log?.responseTime} />
            <MetaRow label="Created at" value={log?.createdAt} />
          </div>

          <LogPayloadSection title="Request Headers" content={log?.requestHeaders} accentClass="text-emerald-400" />
          <LogPayloadSection title="Request Body" content={log?.requestBody} accentClass="text-cyan-400" />
          <LogPayloadSection
            title="Request After Transform"
            content={log?.requestAfterTransform}
            accentClass="text-amber-400"
          />
          <LogPayloadSection title="Response Body" content={log?.responseBody} accentClass="text-blue-400" />
          <LogPayloadSection
            title="Response After Transform"
            content={log?.responseAfterTransform}
            accentClass="text-violet-400"
          />
          {!success ? <LogPayloadSection title="Stacktrace" content={log?.stacktrace} accentClass="text-rose-400" /> : null}
        </div>
      )}
    </Card>
  );
}
