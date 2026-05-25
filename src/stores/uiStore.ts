import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

export type ThemeMode = 'dark' | 'light';
export type NotificationTone = 'success' | 'error' | 'info';

export type NotificationItem = {
  id: number;
  message: string;
  tone: NotificationTone;
};

type UiStoreState = {
  theme: ThemeMode;
  sidebarCollapsed: boolean;
  notifications: NotificationItem[];
  setTheme: (theme: ThemeMode) => void;
  toggleTheme: () => void;
  setSidebarCollapsed: (collapsed: boolean) => void;
  toggleSidebarCollapsed: () => void;
  showNotification: (message: string, tone?: NotificationTone) => void;
  dismissNotification: (id: number) => void;
  clearNotifications: () => void;
};

export const UI_STORAGE_KEY = 'admin-dashboard-ui';

let nextNotificationId = 1;

export const useUiStore = create<UiStoreState>()(
  persist(
    (set) => ({
      theme: 'dark',
      sidebarCollapsed: false,
      notifications: [],
      setTheme: (theme) => set({ theme }),
      toggleTheme: () =>
        set((state) => ({
          theme: state.theme === 'dark' ? 'light' : 'dark',
        })),
      setSidebarCollapsed: (sidebarCollapsed) => set({ sidebarCollapsed }),
      toggleSidebarCollapsed: () =>
        set((state) => ({
          sidebarCollapsed: !state.sidebarCollapsed,
        })),
      showNotification: (message, tone = 'info') =>
        set((state) => ({
          notifications: [...state.notifications, { id: nextNotificationId++, message, tone }],
        })),
      dismissNotification: (id) =>
        set((state) => ({
          notifications: state.notifications.filter((item) => item.id !== id),
        })),
      clearNotifications: () => set({ notifications: [] }),
    }),
    {
      name: UI_STORAGE_KEY,
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        theme: state.theme,
        sidebarCollapsed: state.sidebarCollapsed,
      }),
    },
  ),
);
