import type { UserResponse } from '../api/identity';

export type MetricTrend = 'up' | 'down' | 'flat';

export interface Metric {
  id: string;
  label: string;
  value: string;
  change: string;
  trend: MetricTrend;
  description: string;
}

export type UserStatus = 'Active' | 'Pending' | 'Offline';

export type User = Omit<UserResponse, 'status'> & {
  name: string;
  email: string;
  role: string;
  team: string;
  status: UserStatus;
  lastLogin: string;
};
