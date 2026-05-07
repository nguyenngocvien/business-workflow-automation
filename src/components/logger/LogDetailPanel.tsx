import { Card } from '../ui/Card';
import { LogPayloadSection } from './LogPayloadSection';
import { isLogSuccess } from './LogStatusCell';
import type { LogRecord } from '../../types/log';

type LogDetailPanelProps = {
  log: LogRecord | null;
  loading: boolean;
};

function MetaRow({ label, value }: { label: string; value?: string | number | null }) {
  return (
    <div className="theme-soft grid gap-1 rounded-2xl px-4 py-3">
      <span className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">{label}</span>
      <span className="theme-strong-text text-sm">{value || '-'}</span>
    </div>
  );
}

export function LogDetailPanel({ log, loading }: LogDetailPanelProps) {
  if (!log && !loading) {
    return (
      <Card className="bg-slate-950 text-white">
        <h3 className="mt-3 text-2xl font-bold">Select a log row</h3>
        <p className="mt-3 text-sm leading-7 text-slate-300">
          The React detail panel mirrors the Thymeleaf modal. Choose a log entry to inspect request and response
          payloads.
        </p>
      </Card>
    );
  }

  const success = isLogSuccess(log?.errorCode);

  return (
    <Card className="sticky top-6">
      <div>
        <h3 className="mt-1 text-2xl font-bold text-slate-950">{loading ? 'Loading...' : `Log #${log?.id}`}</h3>
        <p className={`mt-2 text-sm font-semibold ${success ? 'text-emerald-600' : 'text-rose-600'}`}>
          {loading ? 'Fetching log detail.' : log?.errorMessage || 'No message'}
        </p>
      </div>

      {loading ? (
        <div className="theme-soft mt-8 rounded-3xl px-6 py-10 text-center text-sm">
          Loading log detail...
        </div>
      ) : (
        <div className="mt-8 space-y-4">
          <div className="grid gap-3 md:grid-cols-2">
            <MetaRow label="Case ID" value={log?.caseId} />
            <MetaRow label="Service" value={log?.service} />
            <MetaRow label="System" value={log?.system} />
            <MetaRow label="Timing" value={log?.timing} />
            <MetaRow label="Error code" value={log?.errorCode} />
            <MetaRow label="Created date" value={log?.createdDate} />
          </div>

          <LogPayloadSection title="FROM Input" content={log?.fromInput} accentClass="text-emerald-400" />
          <LogPayloadSection title="FROM Output" content={log?.fromOutput} accentClass="text-cyan-400" />
          <LogPayloadSection title="TO Input" content={log?.toInput} accentClass="text-amber-400" />
          <LogPayloadSection title="TO Output" content={log?.toOutput} accentClass="text-blue-400" />
          {!success ? (
            <LogPayloadSection title="Stacktrace" content={log?.stacktrace} accentClass="text-rose-400" />
          ) : null}
        </div>
      )}
    </Card>
  );
}
