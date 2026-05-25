import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

export type AuthUser = {
  name: string;
  email: string;
};

export type AuthSession = {
  user: AuthUser;
  accessToken: string;
  refreshToken?: string;
  idToken?: string;
  expiresAt?: number;
  tokenType?: string;
};

type AuthStoreState = {
  session: AuthSession | null;
  isHydrated: boolean;
  setHydrated: (isHydrated: boolean) => void;
  setSession: (session: AuthSession) => void;
  clearSession: () => void;
};

export const AUTH_STORAGE_KEY = 'admin-dashboard-auth';

export const useAuthStore = create<AuthStoreState>()(
  persist(
    (set) => ({
      session: null,
      isHydrated: false,
      setHydrated: (isHydrated) => set({ isHydrated }),
      setSession: (session) => set({ session }),
      clearSession: () => set({ session: null }),
    }),
    {
      name: AUTH_STORAGE_KEY,
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({ session: state.session }),
      onRehydrateStorage: () => (state, error) => {
        if (!error) {
          state?.setHydrated(true);
        }
      },
    },
  ),
);

export function getStoredSession() {
  return useAuthStore.getState().session;
}

export function getStoredAccessToken() {
  return useAuthStore.getState().session?.accessToken ?? null;
}

export function getStoredAuthUser() {
  return useAuthStore.getState().session?.user ?? null;
}

export function setStoredSession(session: AuthSession) {
  useAuthStore.getState().setSession(session);
}

export function clearStoredSession() {
  useAuthStore.getState().clearSession();
}
