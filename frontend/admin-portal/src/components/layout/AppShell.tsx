import type { PropsWithChildren } from 'react';
import { useEffect, useState } from 'react';
import { HeaderBar } from './HeaderBar';
import { MobileNav } from './MobileNav';
import { Sidebar } from './Sidebar';

type ThemeMode = 'dark' | 'light';

export function AppShell({ children }: PropsWithChildren) {
  const [theme, setTheme] = useState<ThemeMode>(() => {
    const stored = window.localStorage.getItem('admin-dashboard-theme');
    return stored === 'light' ? 'light' : 'dark';
  });
  const [sidebarCollapsed, setSidebarCollapsed] = useState(() => window.localStorage.getItem('admin-dashboard-sidebar') === 'collapsed');

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    window.localStorage.setItem('admin-dashboard-theme', theme);
  }, [theme]);

  useEffect(() => {
    window.localStorage.setItem('admin-dashboard-sidebar', sidebarCollapsed ? 'collapsed' : 'expanded');
  }, [sidebarCollapsed]);

  return (
    <div className="h-screen overflow-hidden">
      <div className="mx-auto flex h-screen overflow-hidden">
        <Sidebar
          theme={theme}
          collapsed={sidebarCollapsed}
          onToggleCollapsed={() => setSidebarCollapsed((current) => !current)}
          onToggleTheme={() => setTheme((current) => (current === 'dark' ? 'light' : 'dark'))}
        />
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
