import { Card } from '../components/ui/Card';

export function ReportsPage() {
  return (
    <div className="grid h-full min-h-0 gap-3 overflow-hidden lg:grid-cols-3">
      <Card className="lg:col-span-2">
        <p className="text-sm font-semibold uppercase tracking-[0.25em] text-brand-500">Reporting</p>
        <h3 className="theme-strong-text mt-3 text-2xl font-bold">Performance snapshots</h3>
        <p className="theme-muted-text mt-3 text-sm">
          This route is ready for connected reporting views. Use the shared layout and API service to plug in live data.
        </p>
      </Card>

      <Card className="theme-code-panel">
        <p className="text-sm font-semibold uppercase tracking-[0.25em] text-cyan-300">Status</p>
        <h3 className="mt-3 text-2xl font-bold">3 exports scheduled</h3>
        <p className="mt-3 text-sm text-slate-300">Daily summaries are queued for finance, operations, and support leads.</p>
      </Card>
    </div>
  );
}
