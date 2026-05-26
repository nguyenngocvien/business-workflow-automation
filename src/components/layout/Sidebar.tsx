import { NavLink } from 'react-router-dom';
import { navigationItems } from '../../data/navItems';
import { useAuth } from '../../hooks/useAuth';
import { useUiStore } from '../../stores/uiStore';
import { cx } from '../../lib/utils';
import { navIcons } from './navIcons';

export function Sidebar() {
  const { user, logout } = useAuth();
  const theme = useUiStore((state) => state.theme);
  const collapsed = useUiStore((state) => state.sidebarCollapsed);
  const toggleCollapsed = useUiStore((state) => state.toggleSidebarCollapsed);
  const toggleTheme = useUiStore((state) => state.toggleTheme);
  const initials = (user?.name || 'AU')
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  return (
    <aside className={`hidden flex-col p-2 transition-all duration-300 lg:flex ${collapsed ? 'w-20' : 'w-64'
      } ${theme === 'dark' ? 'bg-slate-950/90 text-slate-100' : 'bg-slate-200/80 text-slate-900'
      }`}>
      <div className="mt-2">
        <div className={cx('flex items-center', collapsed ? 'justify-center' : 'gap-3')}>
          <button
            type="button"
            onClick={toggleCollapsed}
            className={cx(
              'flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl transition',
              theme === 'dark'
                ? 'text-slate-300 hover:bg-white/10 hover:text-white'
                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-950',
            )}
            aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            title={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            <svg viewBox="0 0 24 24" className="h-5 w-5">
              {navIcons.menu}
            </svg>
          </button>
          {!collapsed ? <h1 className="text-xl font-bold">e-connector</h1> : null}
        </div>
      </div>

      <nav className="mt-8 flex flex-1 flex-col gap-2">
        {navigationItems.map((item) =>
          item.external ? (
            <a
              key={item.path}
              href={item.path}
              target="_blank"
              rel="noreferrer"
              title={item.label}
              aria-label={item.label}
              className={cx(
                'flex items-center rounded-2xl px-4 py-4 text-sm font-semibold transition',
                collapsed ? 'justify-center' : 'gap-3',
                theme === 'dark'
                  ? 'text-slate-300 hover:bg-white/10 hover:text-white'
                  : 'text-slate-600 hover:bg-white hover:text-slate-950',
              )}
            >
              <svg viewBox="0 0 24 24" className="h-5 w-5">
                {navIcons[item.icon]}
              </svg>
              {!collapsed ? item.label : null}
            </a>
          ) : (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.path === '/'}
              title={item.label}
              aria-label={item.label}
              className={({ isActive }) =>
                cx(
                  'flex items-center rounded-2xl px-4 py-4 text-sm font-semibold transition',
                  collapsed ? 'justify-center' : 'gap-3',
                  isActive
                    ? theme === 'dark'
                      ? 'bg-slate-100/12 text-slate-50 ring-1 ring-white/10'
                      : 'bg-slate-900/8 text-slate-900 ring-1 ring-slate-300/80'
                    : theme === 'dark'
                      ? 'text-slate-300 hover:bg-white/10 hover:text-white'
                      : 'text-slate-600 hover:bg-white hover:text-slate-950',
                )
              }
            >
              <svg viewBox="0 0 24 24" className="h-5 w-5">
                {navIcons[item.icon]}</svg>
              {!collapsed ? item.label : null}
            </NavLink>
          ),
        )}
      </nav>
      <div className="mt-auto flex flex-col gap-2 pt-6">
        <button
          type="button"
          onClick={toggleTheme}
          title={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
          aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
          className={cx(
            'group relative overflow-hidden rounded-xl p-3 text-left transition',
            collapsed && 'px-2',
            theme === 'dark'
              ? 'border-white/10 bg-white/5 hover:bg-white/10'
              : 'border-slate-300 bg-white/80 hover:bg-white',
          )}
        >
          <div className={cx('flex items-center gap-3', collapsed ? 'justify-center' : 'justify-between')}>
            {!collapsed ? (
              <div>
                <p className={cx('text-[11px] font-semibold uppercase tracking-[0.24em]', theme === 'dark' ? 'text-cyan-300' : 'text-brand-500')}>
                  Interface
                </p>
                <p className={cx('mt-1 text-sm font-semibold', theme === 'dark' ? 'text-white' : 'text-slate-900')}>
                  {theme === 'dark' ? 'Dark mode' : 'Light mode'}
                </p>
              </div>
            ) : null}
            <div
              className={cx(
                'relative flex items-center rounded-full p-1 transition',
                collapsed ? 'h-11 w-11 justify-center' : 'h-12 w-24',
                theme === 'dark'
                  ? 'bg-slate-900 ring-1 ring-white/10'
                  : 'bg-slate-100 ring-1 ring-slate-300',
              )}
            >
              <div
                className={cx(
                  'absolute h-9 w-9 rounded-full transition-all',
                  theme === 'dark'
                    ? collapsed
                      ? 'left-1 top-1 bg-amber-300 shadow-[0_8px_20px_rgba(251,191,36,0.35)]'
                      : 'left-[calc(100%-2.5rem)] top-1.5 bg-cyan-400 shadow-[0_8px_20px_rgba(34,211,238,0.35)]'
                    : collapsed
                      ? 'left-1 top-1 bg-cyan-400 shadow-[0_8px_20px_rgba(34,211,238,0.35)]'
                      : 'left-1.5 top-1.5 bg-amber-300 shadow-[0_8px_20px_rgba(251,191,36,0.35)]'
                )}
              />
              <span className={cx('relative z-10 flex items-center', collapsed ? 'justify-center' : 'w-full justify-between px-2')}>
                {!collapsed ? (
                  <svg viewBox="0 0 24 24" className={cx('h-4 w-4', theme === 'dark' ? 'text-slate-500' : 'text-amber-900')}>
                    {navIcons.sun}
                  </svg>
                ) : null}
                {collapsed ? (
                  <svg viewBox="0 0 24 24" className={cx('h-4 w-4', theme === 'dark' ? 'text-slate-500' : 'text-amber-900')}>
                    {theme === 'dark' ? navIcons.sun : navIcons.moon}
                  </svg>
                ) : null}
                {!collapsed ? (
                  <svg viewBox="0 0 24 24" className={cx('h-4 w-4', theme === 'dark' ? 'text-slate-950' : 'text-slate-400')}>
                    {navIcons.moon}
                  </svg>
                ) : null}
              </span>
            </div>
          </div>
        </button>

        <div className={cx('flex rounded-xl p-3', collapsed ? 'justify-center' : 'items-center gap-3', theme === 'dark' ? 'bg-white/5' : 'bg-white/80')}>
          <div className={cx('flex h-10 w-10 items-center justify-center rounded-full text-sm font-bold', theme === 'dark' ? 'bg-cyan-400 text-slate-950' : 'bg-slate-900 text-white')}>
            {initials}
          </div>

          {!collapsed ? (
            <div className="min-w-0">
              <p className={cx('truncate text-sm font-semibold', theme === 'dark' ? 'text-white' : 'text-slate-900')}>
                {user?.name || 'Admin User'}
              </p>
              <p className={cx('truncate text-xs', theme === 'dark' ? 'text-slate-400' : 'text-slate-500')}>
                {user?.email || 'admin@company.com'}
              </p>
            </div>
          ) : null}
        </div>

        <button
          type="button"
          onClick={logout}
          title="Logout"
          aria-label="Logout"
          className={cx(
            'flex items-center rounded-2xl bg-rose-500 px-4 py-3 text-sm font-semibold text-white transition hover:bg-rose-600',
            collapsed ? 'justify-center' : 'gap-3',
          )}
        >
          <svg viewBox="0 0 24 24" className="h-5 w-5">
            {navIcons.logout}
          </svg>
          {!collapsed ? 'Logout' : null}
        </button>
      </div>
    </aside>
  );
}
