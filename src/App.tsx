import { Navigate, Route, Routes } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import { AppShell } from './components/layout/AppShell';
import { useAuth } from './hooks/useAuth';
import { DashboardPage } from './pages/DashboardPage';
import { ConnectionsPage } from './pages/ConnectionsPage';
import { EmailTemplatesPage } from './pages/EmailTemplatesPage';
import { FilesPage } from './pages/FilesPage';
import { LoginPage } from './pages/LoginPage';
import { LogsPage } from './pages/LogsPage';
import { ServicesPage } from './pages/ServicesPage';
import { UsersPage } from './pages/UsersPage';

function ProtectedRoutes() {
  const { isAuthenticated, isReady } = useAuth();
  const location = useLocation();

  if (!isReady) {
    return null;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return (
    <AppShell>
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/connections" element={<ConnectionsPage />} />
        <Route path="/services" element={<ServicesPage />} />
        <Route path="/logs" element={<LogsPage />} />
        <Route path="/email-templates" element={<EmailTemplatesPage />} />
        <Route path="/files" element={<FilesPage />} />
        <Route path="/users" element={<UsersPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AppShell>
  );
}

export default function App() {
  const { isAuthenticated, isReady } = useAuth();

  if (!isReady) {
    return null;
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={isAuthenticated ? <Navigate to="/" replace /> : <LoginPage />}
      />
      <Route path="/*" element={<ProtectedRoutes />} />
    </Routes>
  );
}
