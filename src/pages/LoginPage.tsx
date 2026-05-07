import { FormEvent, useState } from 'react';
import { navIcons } from '../components/layout/navIcons';
import { useNavigate } from 'react-router-dom';
import { IconButton } from '../components/ui/IconButton';
import { useNotify } from '../components/ui/NotificationProvider';
import { useAuth } from '../hooks/useAuth';

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [username, setUername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const notify = useNotify();

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);

    try {
      await login(username, password);
      navigate('/', { replace: true });
    } catch (submitError) {
      notify.error(submitError instanceof Error ? submitError.message : 'Login failed.');
    } finally {
      setLoading(false);
    }
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
          <h2 className="mt-4 text-3xl font-bold tracking-tight text-slate-950">Sign in to continue</h2>

          <form className="mt-10 space-y-5" onSubmit={handleSubmit}>
            <label className="block">
              <span className="mb-2 block text-sm font-semibold text-slate-700">Email</span>
              <input
                type="text"
                value={username}
                onChange={(event) => setUername(event.target.value)}
                className="theme-input w-full rounded-2xl px-3 py-2 text-sm outline-none transition focus:border-brand-500 focus:ring-4 focus:ring-brand-500/10"
                placeholder="admin@company.com"
              />
            </label>

            <label className="block">
              <span className="mb-2 block text-sm font-semibold text-slate-700">Password</span>
              <input
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                className="theme-input w-full rounded-2xl px-3 py-2 text-sm outline-none transition focus:border-brand-500 focus:ring-4 focus:ring-brand-500/10"
                placeholder="password"
              />
            </label>
            <div className="flex justify-end">
              <IconButton
                type="submit"
                disabled={loading}
                icon={navIcons.arrowRight}
                label={loading ? 'Signing in' : 'Sign in'}
                tone="primary"
                size="sm"
              />
            </div>
          </form>
        </section>
      </div>
    </div>
  );
}
