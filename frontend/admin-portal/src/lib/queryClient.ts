import { QueryClient } from '@tanstack/react-query';

export const DEFAULT_STALE_TIME = 60 * 1000;
export const DEFAULT_CACHE_TIME = 10 * 60 * 1000;

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: DEFAULT_STALE_TIME,
      gcTime: DEFAULT_CACHE_TIME,
      refetchOnWindowFocus: false,
      refetchOnReconnect: false,
      retry: 1,
    },
  },
});
