import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export function AuthCallbackPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { handleKeycloakCallback } = useAuth();
  const [message, setMessage] = useState('Completing sign-in...');

  useEffect(() => {
    let mounted = true;

    void handleKeycloakCallback(searchParams)
      .then((redirectTo) => {
        if (!mounted) {
          return;
        }

        navigate(redirectTo, { replace: true });
      })
      .catch((error) => {
        if (!mounted) {
          return;
        }

        const errorMessage = error instanceof Error ? error.message : 'Authentication failed.';
        setMessage(errorMessage);
        navigate('/login', { replace: true, state: { error: errorMessage } });
      });

    return () => {
      mounted = false;
    };
  }, [handleKeycloakCallback, navigate, searchParams]);

  return (
    <div className="flex min-h-screen items-center justify-center px-4 py-12">
      <div className="w-full max-w-md rounded-[2rem] bg-white p-8 text-center shadow-panel">
        <p className="text-sm font-semibold uppercase tracking-[0.3em] text-brand-500">Keycloak</p>
        <h1 className="mt-4 text-2xl font-bold tracking-tight text-slate-950">{message}</h1>
      </div>
    </div>
  );
}
