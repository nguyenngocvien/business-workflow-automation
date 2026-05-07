import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';

type NotificationTone = 'success' | 'error' | 'info';

type NotificationItem = {
  id: number;
  message: string;
  tone: NotificationTone;
};

type NotificationContextValue = {
  show: (message: string, tone?: NotificationTone) => void;
  success: (message: string) => void;
  error: (message: string) => void;
  info: (message: string) => void;
};

const NotificationContext = createContext<NotificationContextValue | null>(null);
const AUTO_DISMISS_MS = 4000;

const toneClasses = {
  success: `
    bg-[rgba(var(--color-success),0.12)]
    border-[rgba(var(--color-success),0.15)]
    text-[rgb(var(--color-success))]
  `,
  error: `
    bg-[rgba(var(--color-error),0.12)]
    border-[rgba(var(--color-error),0.06)]
    text-[rgb(var(--color-error))]
  `,
  warning: `
    bg-[rgba(var(--color-warning),0.12)]
    border-[rgba(var(--color-warning),0.06)]
    text-[rgb(var(--color-warning))]
  `,
  info: `
    bg-[rgba(var(--color-info),0.12)]
    border-[rgba(var(--color-info),0.06)]
    text-[rgb(var(--color-info))]
  `,
};

const toneIcons: Record<NotificationTone, ReactNode> = {
  success: (
    <path
      d="M20 7 9 18l-5-5"
      stroke="currentColor"
      strokeWidth="2"
      fill="none"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  ),
  error: (
    <path
      d="M15 9 9 15M9 9l6 6M12 3.75a8.25 8.25 0 1 1 0 16.5 8.25 8.25 0 0 1 0-16.5Z"
      stroke="currentColor"
      strokeWidth="2"
      fill="none"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  ),
  info: (
    <path
      d="M12 8h.01M11 12h1v4h1M12 3.75a8.25 8.25 0 1 1 0 16.5 8.25 8.25 0 0 1 0-16.5Z"
      stroke="currentColor"
      strokeWidth="2"
      fill="none"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  ),
};

export function NotificationProvider({ children }: { children: ReactNode }) {
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const nextIdRef = useRef(1);

  const dismiss = useCallback((id: number) => {
    setNotifications((current) => current.filter((item) => item.id !== id));
  }, []);

  const show = useCallback((message: string, tone: NotificationTone = 'info') => {
    const id = nextIdRef.current;
    nextIdRef.current += 1;
    setNotifications((current) => [...current, { id, message, tone }]);
  }, []);

  useEffect(() => {
    if (notifications.length === 0) {
      return undefined;
    }

    const timers = notifications.map((item) =>
      window.setTimeout(() => {
        dismiss(item.id);
      }, AUTO_DISMISS_MS),
    );

    return () => {
      timers.forEach((timer) => window.clearTimeout(timer));
    };
  }, [dismiss, notifications]);

  const value = useMemo<NotificationContextValue>(
    () => ({
      show,
      success: (message: string) => show(message, 'success'),
      error: (message: string) => show(message, 'error'),
      info: (message: string) => show(message, 'info'),
    }),
    [show],
  );

  return (
    <NotificationContext.Provider value={value}>
      {children}
      <div className="pointer-events-none fixed top-4 right-4 z-[100] flex w-[min(360px,calc(100vw-2rem))] flex-col gap-3">
        {notifications.map((item) => (
          <div
            key={item.id}
            className={`
              pointer-events-auto flex items-center gap-3
              rounded-2xl px-4 py-3
              border backdrop-blur-md
              shadow-[0_8px_32px_rgba(0,0,0,0.45)]
              bg-[var(--surface-card)]
              ${toneClasses[item.tone]}
            `}
            role="status"
            aria-live="polite"
          >
            <svg viewBox="0 0 24 24" className="mt-0.5 h-5 w-5 shrink-0 opacity-90">
              {toneIcons[item.tone]}
            </svg>
            <p className="min-w-0 flex-1 text-sm font-medium leading-snug">{item.message}</p>
            <button
              type="button"
              onClick={() => dismiss(item.id)}
              className="rounded-full p-1 transition hover:bg-[var(--surface-muted)]"
              aria-label="Dismiss notification"
            >
              <svg viewBox="0 0 24 24" className="mt-0.5 h-5 w-5 shrink-0 opacity-90">
                <path
                  d="M18 6 6 18M6 6l12 12"
                  stroke="currentColor"
                  strokeWidth="2"
                  fill="none"
                  strokeLinecap="round"
                />
              </svg>
            </button>
          </div>
        ))}
      </div>
    </NotificationContext.Provider>
  );
}

export function useNotify() {
  const context = useContext(NotificationContext);

  if (!context) {
    throw new Error('useNotify must be used within a NotificationProvider.');
  }

  return context;
}
