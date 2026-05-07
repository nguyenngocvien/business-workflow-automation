import axios from 'axios';
import { metrics, performanceSeries, users } from '../data/dashboard';
import { handleUnauthorized } from '../hooks/authHandler';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  withCredentials: true,
});

api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      handleUnauthorized();
    }
    return Promise.reject(error);
  }
);

type BackendLoginResponse = {
  success: boolean;
  message: string;
  token?: string;
  user?: {
    username: string;
    authorities: string[];
  };
};

export async function login(email: string, password: string) {
  if (!email || !password) {
    throw new Error('Email and password are required.');
  }

  try {
    const { data } = await api.post<BackendLoginResponse>('/auth/login', {
      username: email,
      password,
    });

    if (!data.success || !data.user) {
      throw new Error(data.message || 'Invalid credentials');
    }

    return {
      token: data.token ?? '',
      user: {
        name: data.user.username,
        email,
        authorities: data.user.authorities,
      },
    };
  } catch (error) {
    if (axios.isAxiosError<BackendLoginResponse>(error)) {
      const message = error.response?.data?.message ?? 'Invalid credentials';
      throw new Error(message);
    }

    throw error;
  }
}

export async function fetchDashboardMetrics() {
  await new Promise((resolve) => setTimeout(resolve, 250));
  return metrics;
}

export async function fetchPerformanceSeries() {
  await new Promise((resolve) => setTimeout(resolve, 250));
  return performanceSeries;
}

export async function fetchUsers() {
  await new Promise((resolve) => setTimeout(resolve, 250));
  return users;
}

export { api };
