import { useEffect } from 'react';
import { navIcons } from '../components/layout/navIcons';
import { useLocation } from 'react-router-dom';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import { useAuth } from '../hooks/useAuth';

export function LoginPage() {
  const { login } = useAuth();
  const location = useLocation();
  const notify = useNotify();
  const state = location.state as
    | { from?: { pathname?: string; search?: string; hash?: string }; error?: string }
    | null;
  const from = state?.from;
  const authError = state?.error;
  const redirectTo = `${from?.pathname ?? '/'}${from?.search ?? ''}${from?.hash ?? ''}` || '/';

  useEffect(() => {
    if (authError) {
      notify.error(authError);
      return;
    }

    void login(redirectTo).catch((submitError) => {
      notify.error(submitError instanceof Error ? submitError.message : 'Unable to start Keycloak login.');
    });
  }, [authError, login, notify, redirectTo]);

  function handleRetry() {
    void login(redirectTo).catch((submitError) => {
      notify.error(submitError instanceof Error ? submitError.message : 'Unable to start Keycloak login.');
    });
  }

  return (
    <div className="flex min-h-screen items-center justify-center px-4 py-12">
      <div className="grid w-full max-w-5xl overflow-hidden rounded-[2rem] bg-white shadow-panel lg:grid-cols-[1.1fr_0.9fr]">
        <section className="hidden bg-slate-950 p-10 text-white lg:block">
          <p className="text-sm font-semibold uppercase tracking-[0.3em] text-cyan-300">Admin Dashboard</p>
          <h1 className="mt-6 max-w-md text-5xl font-bold leading-tight">
            Run your operation from one clean control surface.
          </h1>
          <p className="mt-6 max-w-lg text-base text-slate-300">
            Review performance, team activity, and critical metrics in a responsive SaaS-style workspace.
          </p>
        </section>

        <section className="p-8 sm:p-10">
          <p className="text-sm font-semibold uppercase tracking-[0.3em] text-brand-500">Welcome back</p>
          <h2 className="mt-4 text-3xl font-bold tracking-tight text-slate-950">Redirecting to Keycloak</h2>
          <p className="mt-3 text-sm text-slate-600">
            You&apos;ll sign in with your identity provider, then return here with a JWT access token attached to API requests.
          </p>
          {authError ? (
            <div className="mt-5 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
              {authError}
            </div>
          ) : null}

          <div className="mt-10 space-y-5">
            <div className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-600">
              If the browser does not redirect automatically, use the button below.
            </div>
            <div className="flex justify-end">
              <IconButton
                type="button"
                onClick={handleRetry}
                icon={navIcons.arrowRight}
                label="Continue with Keycloak"
                tone="primary"
                size="sm"
              />
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
