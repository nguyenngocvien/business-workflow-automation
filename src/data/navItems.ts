export interface NavItem {
  label: string;
  path: string;
  icon: string;
  external?: boolean;
}

export const navigationItems: NavItem[] = [
  { label: 'Dashboard', path: '/', icon: 'grid' },
  { label: 'Connections', path: '/connections', icon: 'connections' },
  { label: 'Services', path: '/services', icon: 'sliders' },
  { label: 'Logs', path: '/logs', icon: 'logs' },
  { label: 'Emails', path: '/email-templates', icon: 'mail' },
  { label: 'Files', path: '/files', icon: 'folder' },
  { label: 'Users', path: '/users', icon: 'users' },
  { label: 'Metadata', path: '/metadata', icon: 'database' },
  { label: 'Packages', path: '/database-tool', icon: 'box' },
  { label: 'Swagger', path: '/e-connector/swagger-ui.html', icon: 'braces', external: true },
];
