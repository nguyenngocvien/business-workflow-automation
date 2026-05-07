import { Metric } from '../../types/dashboard';
import { Card } from '../ui/Card';

type MetricCardProps = {
  metric: Metric;
};

export function MetricCard({ metric }: MetricCardProps) {
  const changeColor = metric.trend === 'up' ? 'text-emerald-600' : 'text-amber-600';

  return (
    <Card className="relative overflow-hidden">
      <div className="absolute inset-x-6 top-0 h-1 rounded-full bg-gradient-to-r from-brand-500 via-cyan-400 to-emerald-400" />
      <p className="theme-muted-text text-sm font-medium">{metric.label}</p>
      <div className="mt-4 flex items-end justify-between gap-4">
        <div>
          <p className="theme-strong-text text-3xl font-bold tracking-tight">{metric.value}</p>
          <p className="theme-muted-text mt-2 text-sm">{metric.description}</p>
        </div>
        <span className={`theme-soft rounded-full px-3 py-1 text-sm font-semibold ${changeColor}`}>
          {metric.change}
        </span>
      </div>
    </Card>
  );
}
