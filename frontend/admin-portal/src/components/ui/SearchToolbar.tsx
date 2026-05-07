import { useState, type ReactNode } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import { navIcons } from '../layout/navIcons';

type SearchToolbarProps = {
  primary: ReactNode;
  advanced?: ReactNode;
  onSearch: () => void;
  className?: string;
  collapsedWidthClassName?: string;
  expandedWidthClassName?: string;
  onExpandedChange?: (expanded: boolean) => void;
};

export function SearchToolbar({
  primary,
  advanced,
  onSearch,
  className,
  collapsedWidthClassName = 'w-full max-w-md',
  expandedWidthClassName = 'w-full max-w-4xl',
  onExpandedChange,
}: SearchToolbarProps) {
  const [expanded, setExpanded] = useState(false);

  function toggleExpanded() {
    setExpanded((current) => {
      const next = !current;
      onExpandedChange?.(next);
      return next;
    });
  }

  return (
    <div
      className={`flex items-center transition-all duration-300 ${expanded ? expandedWidthClassName : collapsedWidthClassName} ${className ?? ''}`}
    >
      <div className="flex w-full flex-col gap-2">
        <div className="theme-elevated flex items-center gap-2 rounded-xl border border-[var(--border-subtle)] px-2 py-1 shadow-sm backdrop-blur-sm">
          <button
            type="button"
            onClick={toggleExpanded}
            className={`rounded-lg p-2 transition-colors ${expanded
              ? 'bg-[var(--surface-header)] text-[rgb(var(--color-header-accent))]'
              : 'theme-muted-text hover:bg-[var(--surface-input)] hover:text-[var(--text-strong)]'
              }`}
            aria-label="Toggle advanced filters"
          >
            <svg viewBox="0 0 24 24" className="h-5 w-5">
              {navIcons.filter}
            </svg>
          </button>

          <div className="flex flex-1 items-center gap-2 overflow-hidden">
            <div className="min-w-[180px] flex-1 theme-strong-text">{primary}</div>

            <AnimatePresence initial={false}>
              {expanded && advanced ? (
                <motion.div
                  initial={{ opacity: 0, width: 0 }}
                  animate={{ opacity: 1, width: 'auto' }}
                  exit={{ opacity: 0, width: 0 }}
                  transition={{ duration: 0.2 }}
                  className="flex items-center gap-2 overflow-hidden"
                >
                  {advanced}
                </motion.div>
              ) : null}
            </AnimatePresence>
          </div>

          <button
            type="button"
            onClick={onSearch}
            className="theme-muted-text rounded-lg p-2 transition-colors hover:bg-[var(--surface-input)] hover:text-[var(--text-strong)]"
            aria-label="Search"
          >
            <svg viewBox="0 0 24 24" className="h-5 w-5">
              {navIcons.search}
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
}
