import type { ChartPoint, Metric, NavItem, User } from '../types/dashboard';
import { formatCompactNumber, formatCurrency } from '../lib/utils';

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

export const metrics: Metric[] = [
  {
    id: 'revenue',
    label: 'Monthly revenue',
    value: formatCurrency(128400),
    change: '+12.4%',
    trend: 'up',
    description: 'Compared to last month',
  },
  {
    id: 'users',
    label: 'Active users',
    value: formatCompactNumber(8421),
    change: '+8.1%',
    trend: 'up',
    description: 'Across all workspaces',
  },
  {
    id: 'conversion',
    label: 'Conversion rate',
    value: '6.8%',
    change: '+1.2%',
    trend: 'up',
    description: 'Trial to paid',
  },
  {
    id: 'tickets',
    label: 'Open tickets',
    value: '29',
    change: '-4.7%',
    trend: 'down',
    description: 'Resolved in the last 7 days',
  },
];

export const performanceSeries: ChartPoint[] = [
  { label: 'Jan', value: 28 },
  { label: 'Feb', value: 36 },
  { label: 'Mar', value: 33 },
  { label: 'Apr', value: 48 },
  { label: 'May', value: 52 },
  { label: 'Jun', value: 61 },
  { label: 'Jul', value: 72 },
];

export const users: User[] = [
  {
    id: 1,
    name: 'Ava Morgan',
    email: 'ava.morgan@acme.io',
    role: 'Admin',
    team: 'Operations',
    status: 'Active',
    lastLogin: '2 min ago',
  },
  {
    id: 2,
    name: 'Noah Carter',
    email: 'noah.carter@acme.io',
    role: 'Manager',
    team: 'Sales',
    status: 'Active',
    lastLogin: '18 min ago',
  },
  {
    id: 3,
    name: 'Mia Rivera',
    email: 'mia.rivera@acme.io',
    role: 'Analyst',
    team: 'Finance',
    status: 'Pending',
    lastLogin: 'Invited',
  },
  {
    id: 4,
    name: 'Liam Chen',
    email: 'liam.chen@acme.io',
    role: 'Support',
    team: 'Customer Success',
    status: 'Offline',
    lastLogin: 'Yesterday',
  },
  {
    id: 5,
    name: 'Sophia Patel',
    email: 'sophia.patel@acme.io',
    role: 'Editor',
    team: 'Marketing',
    status: 'Active',
    lastLogin: '5 min ago',
  },
];
