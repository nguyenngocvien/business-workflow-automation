import type { ChartPoint, Metric, NavItem, User } from '../types/dashboard';

export const navigationItems: NavItem[] = [
  { label: 'Dashboard', path: '/', icon: 'grid' },
  { label: 'Services', path: '/services', icon: 'sliders' },
  { label: 'Logs', path: '/logs', icon: 'logs' },
  { label: 'Emails', path: '/email-templates', icon: 'mail' },
  { label: 'Files', path: '/files', icon: 'folder' },
  { label: 'Users', path: '/users', icon: 'users' },
  { label: 'Metadata', path: '/metadata', icon: 'database' },
  { label: 'Packages', path: '/database-tool', icon: 'box' },
  { label: 'Swagger', path: '/e-connector/swagger-ui.html', icon: 'braces', external: true },
];
