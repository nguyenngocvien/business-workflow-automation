type HealthItem = {
  name: string;
  subtitle: string;
  icon: 'database' | 'cloud' | 'folder';
  connected: boolean | null;
  message?: string;
};

type HealthSectionProps = {
  title: string;
  items: HealthItem[];
  loading?: boolean;
  loadingLabel?: string;
};

function Icon({ icon }: { icon: HealthItem['icon'] }) {
  if (icon === 'database') {
    return (
      <svg viewBox="0 0 24 24" className="h-5 w-5 text-cyan-400">
        <path
          d="M12 5c-4.418 0-8 1.343-8 3s3.582 3 8 3 8-1.343 8-3-3.582-3-8-3Zm-8 7c0 1.657 3.582 3 8 3s8-1.343 8-3M4 16c0 1.657 3.582 3 8 3s8-1.343 8-3"
          stroke="currentColor"
          strokeWidth="2"
          fill="none"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
    );
  }

  if (icon === 'cloud') {
    return (
      <svg viewBox="0 0 24 24" className="h-5 w-5 text-cyan-400">
        <path
          d="M7 18a4 4 0 1 1 .8-7.92A5 5 0 0 1 18 11a3 3 0 1 1 0 6H7Z"
          stroke="currentColor"
          strokeWidth="2"
          fill="none"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
    );
  }

  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5 text-cyan-400">
      <path
        d="M3 7.5A2.5 2.5 0 0 1 5.5 5H10l2 2h6.5A2.5 2.5 0 0 1 21 9.5v7a2.5 2.5 0 0 1-2.5 2.5h-13A2.5 2.5 0 0 1 3 16.5v-9Z"
        stroke="currentColor"
        strokeWidth="2"
        fill="none"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function StatusBadge({ connected }: { connected: boolean | null }) {
  if (connected === null) {
    return (
      <span className="theme-muted-text flex items-center gap-1 text-xs">
        <span className="h-3 w-3 animate-pulse rounded-full bg-slate-500" />
        <span>Checking...</span>
      </span>
    );
  }

  return <span className="text-xl">{connected ? '🟢' : '🔴'}</span>;
}

export function HealthSection({
  title,
  items,
  loading = false,
  loadingLabel = 'Loading...',
}: HealthSectionProps) {
  return (
    <div className="theme-card rounded-2xl border-none p-4 shadow-sm backdrop-blur-sm">
      {/* Header */}
      <div className="mb-4 flex items-center justify-between">
        <h6 className="text-xs font-semibold uppercase tracking-wider theme-muted-text">
          {title}
        </h6>

        <span className="text-xs theme-muted-text">
          {items.length} items
        </span>
      </div>

      {/* Divider */}
      <div className="mb-3 h-px bg-[var(--border-subtle)]" />

      {/* List */}
      <div className="space-y-3">
        {items.map((item) => {
          const isError = !item.connected;

          return (
            <div
              key={item.name}
              className={`
                group flex items-start gap-3 rounded-xl border p-4 transition-all duration-200
                bg-[var(--surface-card-soft)]
                border-transparent
                hover:shadow-md
                hover:border-[var(--border-subtle)]
              `}
            >
              {/* Icon */}
              <div
                className={`
                  flex h-10 w-10 items-center justify-center rounded-full
                  ${isError
                    ? "bg-[rgba(var(--color-error),0.12)] text-[rgb(var(--color-error))]"
                    : "bg-[rgba(var(--color-success),0.12)] text-[rgb(var(--color-success))]"
                  }
                `}
              >
                <Icon icon={item.icon} />
              </div>

              {/* Content */}
              <div className="min-w-0 flex-1">
                <div className="font-semibold theme-strong-text">
                  {item.name}
                </div>

                <div className="truncate text-sm theme-muted-text">
                  {item.subtitle}
                </div>

                {!item.connected && item.message && (
                  <div className="mt-1 text-xs text-[rgb(var(--color-error))]">
                    {item.message}
                  </div>
                )}
              </div>

              {/* Status */}
              <StatusBadge connected={item.connected} />
            </div>
          );
        })}
      </div>
    </div>
  );
}
