import { useLocation } from 'react-router-dom';
import { Card } from '../ui/Card';
import { useState } from 'react';

const pageTitles: Record<string, { title: string; subtitle: string }> = {
  '/': {
    title: 'Servers',
    subtitle: 'Monitor platform health and key operational metrics.',
  },
  '/services': {
    title: 'Services',
    subtitle: 'Manage integration services and configuration status.',
  },
  '/logs': {
    title: 'Logs',
    subtitle: 'Review execution history and service activity.',
  },
  '/email-templates': {
    title: 'Emails',
    subtitle: 'Manage template content and delivery settings.',
  },
  '/files': {
    title: 'Files Manager',
    subtitle: 'Browse and manage repository documents.',
  },
  '/users': {
    title: 'User administration',
    subtitle: 'Review account status, team assignments, and recent access across your workspace.',
  },
  '/metadata': {
    title: 'Metadata',
    subtitle: 'Maintain master data records and settings.',
  },
  '/database-tool': {
    title: 'Packages',
    subtitle: 'Maintain package definitions and database tools.',
  },
};

export function HeaderBar() {

  const location = useLocation();
  const heading = pageTitles[location.pathname] ?? pageTitles['/'];

  return (
    <header>
      <div className='theme-header'>
        <h2 className="text-xl font-bold">
          {heading.title}
        </h2>
        <p className="mt-2 max-w-2xl text-sm">
          {heading.subtitle}
        </p>
      </div>
    </header>
  );
}
