import { Navigate, Route, Routes } from 'react-router-dom';
import { AppShell } from './components/layout/AppShell';
import { useAuth } from './hooks/useAuth';
import { DashboardPage } from './pages/DashboardPage';
import { EmailTemplatesPage } from './pages/EmailTemplatesPage';
import { FilesPage } from './pages/FilesPage';
import { LoginPage } from './pages/LoginPage';
import { LogsPage } from './pages/LogsPage';
import { MasterDataPage } from './pages/MetadataPage';
import { ServicesPage } from './pages/ServicesPage';
import { UsersPage } from './pages/UsersPage';

function ProtectedRoutes() {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <AppShell>
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/services" element={<ServicesPage />} />
        <Route path="/logs" element={<LogsPage />} />
        <Route path="/email-templates" element={<EmailTemplatesPage />} />
        <Route path="/files" element={<FilesPage />} />
        <Route path="/users" element={<UsersPage />} />
        <Route path="/metadata" element={<MasterDataPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AppShell>
  );
}

export default function App() {
  const { isAuthenticated } = useAuth();

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
