import { NavLink } from 'react-router-dom';
import { navigationItems } from '../../data/dashboard';
import { cx } from '../../lib/utils';
import { navIcons } from './navIcons';

type MobileNavProps = {
  theme: 'dark' | 'light';
};

export function MobileNav({ theme }: MobileNavProps) {
  return (
    <div className={cx('rounded-[2rem] p-2 shadow-panel lg:hidden', theme === 'dark' ? 'bg-slate-950 text-white' : 'bg-white text-slate-900')}>
      <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
        {navigationItems.map((item) =>
          item.external ? (
            <a
              key={item.path}
              href={item.path}
              target="_blank"
              rel="noreferrer"
              className={cx(
                'flex items-center justify-center gap-2 rounded-2xl px-3 py-3 text-sm font-semibold transition',
                theme === 'dark'
                  ? 'text-slate-300 hover:bg-white/10 hover:text-white'
                  : 'text-slate-500 hover:bg-slate-100 hover:text-slate-950',
              )}
            >
              <svg viewBox="0 0 24 24" className="h-5 w-5">
                {navIcons[item.icon]}
              </svg>
              <span>{item.label}</span>
            </a>
          ) : (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.path === '/'}
              className={({ isActive }) =>
                cx(
                  'flex items-center justify-center gap-2 rounded-2xl px-3 py-3 text-sm font-semibold transition',
                  isActive
                    ? theme === 'dark'
                      ? 'bg-white text-slate-950'
                      : 'bg-slate-950 text-white'
                    : theme === 'dark'
                      ? 'text-slate-300 hover:bg-white/10 hover:text-white'
                      : 'text-slate-500 hover:bg-slate-100 hover:text-slate-950',
                )
              }
            >
              <svg viewBox="0 0 24 24" className="h-5 w-5">
                {navIcons[item.icon]}
              </svg>
              <span>{item.label}</span>
            </NavLink>
          ),
        )}
      </div>
    </div>
  );
}
