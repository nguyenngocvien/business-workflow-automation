const sections = [
  'api/identity',
  'api/document',
  'api/workflow',
  'api/connector',
  'services',
  'hooks',
  'pages',
  'components',
  'auth',
];

export default function App() {
  return (
    <main className="min-h-screen bg-slate-950 text-slate-100">
      <div className="mx-auto flex min-h-screen w-full max-w-6xl flex-col justify-center px-6 py-12">
        <div className="max-w-3xl rounded-3xl border border-white/10 bg-white/5 p-8 shadow-2xl shadow-cyan-950/20 backdrop-blur">
          <p className="text-sm font-medium uppercase tracking-[0.3em] text-cyan-300">
            Vite React Starter
          </p>
          <h1 className="mt-4 text-4xl font-semibold tracking-tight sm:text-6xl">
            React + TypeScript + Tailwind + Orval
          </h1>
          <p className="mt-4 max-w-2xl text-base leading-7 text-slate-300 sm:text-lg">
            This project is scaffolded for a frontend app that consumes multiple
            OpenAPI services and generates typed axios clients into the API
            folders below.
          </p>

          <div className="mt-8 grid gap-3 sm:grid-cols-2">
            {sections.map((section) => (
              <div
                key={section}
                className="rounded-2xl border border-white/10 bg-slate-900/70 px-4 py-3 text-sm text-slate-200"
              >
                {section}
              </div>
            ))}
          </div>
        </div>
      </div>
    </main>
  );
}

