import { type PropsWithChildren, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { setUnauthorizedHandler } from './authHandler';
import {
  buildKeycloakLogoutUrl,
  clearAuthenticatedState,
  clearKeycloakLoginArtifacts,
  completeKeycloakLoginFromCallback,
  getStoredSession,
  setPostLoginRedirect,
  startKeycloakLogin,
} from '../lib/keycloak';
import { useAuthStore } from '../stores/authStore';

type AuthUser = {
  name: string;
  email: string;
};

type AuthContextValue = {
  isAuthenticated: boolean;
  isReady: boolean;
  user: AuthUser | null;
  login: (redirectTo?: string) => Promise<void>;
  handleKeycloakCallback: (searchParams: URLSearchParams) => Promise<string>;
  logout: () => void;
};

export function AuthProvider({ children }: PropsWithChildren) {
  const navigate = useNavigate();
  const clearSession = useAuthStore((state) => state.clearSession);

  useEffect(() => {
    setUnauthorizedHandler(() => {
      clearSession();
      clearAuthenticatedState();
      navigate('/login', { replace: true });
    });
  }, [clearSession, navigate]);

  return <>{children}</>;
}

export function useAuth(): AuthContextValue {
  const navigate = useNavigate();
  const session = useAuthStore((state) => state.session);
  const isReady = useAuthStore((state) => state.isHydrated);
  const clearSession = useAuthStore((state) => state.clearSession);

  return {
    isAuthenticated: Boolean(session),
    isReady,
    user: session?.user ?? null,
    async login(redirectTo = '/') {
      setPostLoginRedirect(redirectTo);
      await startKeycloakLogin();
    },
    async handleKeycloakCallback(searchParams) {
      const { redirectTo } = await completeKeycloakLoginFromCallback(searchParams);
      clearKeycloakLoginArtifacts();
      return redirectTo;
    },
    logout() {
      const currentSession = getStoredSession();
      clearSession();
      clearAuthenticatedState();

      try {
        window.location.assign(buildKeycloakLogoutUrl(currentSession?.idToken));
      } catch {
        navigate('/login', { replace: true });
      }
    },
  };
}
