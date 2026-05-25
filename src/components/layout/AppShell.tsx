import type { PropsWithChildren } from 'react';
import { useEffect } from 'react';
import { HeaderBar } from './HeaderBar';
import { Sidebar } from './Sidebar';
import { useUiStore } from '../../stores/uiStore';

export function AppShell({ children }: PropsWithChildren) {
  const theme = useUiStore((state) => state.theme);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
  }, [theme]);

  return (
    <div className="h-screen overflow-hidden">
      <div className="mx-auto flex h-screen overflow-hidden">
        <Sidebar />
        <main className={`flex min-h-0 flex-1 flex-col overflow-hidden p-2 transition-colors sm:p-2 ${theme === 'dark' ? 'bg-slate-950/50' : 'bg-white/90'
          }`}>
          <HeaderBar />
          <div className="min-h-0 flex-1 overflow-hidden">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}
