import { Card } from '../ui/Card';
import type { Metric } from '../../types/dashboard';

type MetricCardProps = {
  metric: Metric;
};

const trendClasses: Record<Metric['trend'], string> = {
  up: 'text-emerald-600',
  down: 'text-rose-600',
  flat: 'text-slate-500',
};

export function MetricCard({ metric }: MetricCardProps) {
  return (
    <Card className="overflow-hidden">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.25em] text-cyan-600">{metric.label}</p>
          <h3 className="mt-3 text-3xl font-bold text-slate-950">{metric.value}</h3>
        </div>
        <span className={`text-sm font-semibold ${trendClasses[metric.trend]}`}>{metric.change}</span>
      </div>

      <p className="mt-4 text-sm leading-6 text-slate-500">{metric.description}</p>
    </Card>
  );
}
