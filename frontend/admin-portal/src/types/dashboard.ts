export type Metric = {
  id: string;
  label: string;
  value: string;
  change: string;
  trend: 'up' | 'down';
  description: string;
};

export type NavItem = {
  label: string;
  path: string;
  icon: string;
  external?: boolean;
};

export type ChartPoint = {
  label: string;
  value: number;
};

export type User = {
  id: number;
  name: string;
  email: string;
  role: string;
  team: string;
  status: 'Active' | 'Pending' | 'Offline';
  lastLogin: string;
};
