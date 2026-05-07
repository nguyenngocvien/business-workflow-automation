import { Card } from '../ui/Card';
import type { ChartPoint } from '../../types/dashboard';

type PerformanceChartProps = {
  data: ChartPoint[];
};

export function PerformanceChart({ data }: PerformanceChartProps) {
  const safeData = data.length > 1 ? data : [{ label: 'Jan', value: 0 }, { label: 'Feb', value: 0 }];
  const width = 560;
  const height = 220;
  const maxValue = Math.max(...safeData.map((point) => point.value));
  const minValue = Math.min(...safeData.map((point) => point.value));
  const spread = Math.max(maxValue - minValue, 1);

  const points = safeData
    .map((point, index) => {
      const x = (index / (safeData.length - 1)) * width;
      const y = height - ((point.value - minValue) / spread) * (height - 28) - 14;
      return `${x},${y}`;
    })
    .join(' ');

  const area = `${points} ${width},${height} 0,${height}`;

  return (
    <Card className="overflow-hidden">
      <div className="mb-6 flex items-center justify-between gap-3">
        <div>
          <h2 className="theme-strong-text text-lg font-semibold">Revenue performance</h2>
          <p className="theme-muted-text text-sm">Growth trend over the last 7 months</p>
        </div>
        <div className="rounded-full bg-emerald-50 px-3 py-1 text-sm font-semibold text-emerald-600">
          +18.2%
        </div>
      </div>

      <div className="theme-code-panel rounded-3xl px-4 py-6">
        <svg viewBox={`0 0 ${width} ${height}`} className="h-56 w-full" preserveAspectRatio="none">
          <defs>
            <linearGradient id="chart-fill" x1="0" x2="0" y1="0" y2="1">
              <stop offset="0%" stopColor="rgba(59,130,246,0.45)" />
              <stop offset="100%" stopColor="rgba(59,130,246,0)" />
            </linearGradient>
          </defs>
          {safeData.map((point, index) => {
            const x = (index / (safeData.length - 1)) * width;
            return (
              <line
                key={point.label}
                x1={x}
                x2={x}
                y1="10"
                y2={height}
                stroke="rgba(148,163,184,0.18)"
                strokeDasharray="4 8"
              />
            );
          })}
          <polygon points={area} fill="url(#chart-fill)" />
          <polyline
            fill="none"
            points={points}
            stroke="#38bdf8"
            strokeWidth="4"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
        <div className="mt-4 grid grid-cols-7 gap-2 text-center text-xs text-slate-400">
          {safeData.map((point) => (
            <span key={point.label}>{point.label}</span>
          ))}
        </div>
      </div>
    </Card>
  );
}
