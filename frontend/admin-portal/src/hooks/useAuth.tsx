import {
  createContext,
  useContext,
  useEffect,
  useState,
  type PropsWithChildren,
} from 'react';
import { login as loginRequest } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { setUnauthorizedHandler } from './authHandler';

type AuthUser = {
  name: string;
  email: string;
};

type AuthContextValue = {
  isAuthenticated: boolean;
  user: AuthUser | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);
const STORAGE_KEY = 'admin-dashboard-auth';

export function AuthProvider({ children }: PropsWithChildren) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const storedValue = window.localStorage.getItem(STORAGE_KEY);

    if (storedValue) {
      setUser(JSON.parse(storedValue) as AuthUser);
    }
  }, []);

  useEffect(() => {
    setUnauthorizedHandler(() => {
      setUser(null);
      window.localStorage.removeItem(STORAGE_KEY);
      navigate("/login", { replace: true });
    });
  }, [navigate]);

  const value: AuthContextValue = {
    isAuthenticated: Boolean(user),
    user,
    async login(email, password) {
      const response = await loginRequest(email, password);
      setUser(response.user);
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(response.user));
    },
    logout() {
      setUser(null);
      window.localStorage.removeItem(STORAGE_KEY);
    },
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }

  return context;
}
