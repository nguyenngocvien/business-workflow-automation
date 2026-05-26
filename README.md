# admin-dashboard

Modern SaaS-style React admin dashboard scaffold built with Vite, TypeScript, Tailwind CSS, React Router, and Axios.

## Features

- Login page with local auth state
- Dashboard with metric cards
- Sidebar navigation and header bar
- Users table
- Reusable SVG chart widget
- Example Axios API service with dummy data
- Responsive card-based layout

## Folder structure

```text
admin-dashboard/
  index.html
  package.json
  postcss.config.js
  tailwind.config.js
  tsconfig.app.json
  tsconfig.json
  tsconfig.node.json
  vite.config.ts
  .env
  src/
    App.tsx
    index.css
    main.tsx
    assets/
    components/
      MetricCard.tsx
      UsersTable.tsx
      charts/
        PerformanceChart.tsx
      layout/
        AppShell.tsx
        HeaderBar.tsx
        MobileNav.tsx
        Sidebar.tsx
      ui/
        Button.tsx
        Card.tsx
    data/
      dashboard.ts
    hooks/
      useAuth.tsx
    lib/
      utils.ts
    pages/
      DashboardPage.tsx
      LoginPage.tsx
      ReportsPage.tsx
      UsersPage.tsx
    services/
      api.ts
    types/
      dashboard.ts
```

## Run

```bash
cd admin-dashboard
npm install
npm run dev
```

If PowerShell blocks `npm` on your machine, use:

```bash
npm.cmd install
npm.cmd run dev
```

For a production build:

```bash
npm run build
npm run preview
```

## API wiring

- The app reads these Vite env variables:
- `VITE_API_BASE_URL` for the Axios API base URL.
- `VITE_KEYCLOAK_URL`, `VITE_KEYCLOAK_REALM`, and `VITE_KEYCLOAK_CLIENT_ID` for Keycloak login.
- `VITE_KEYCLOAK_REDIRECT_URI` and `VITE_KEYCLOAK_LOGOUT_REDIRECT_URI` are optional overrides.
- Example `.env`:

```env
VITE_API_BASE_URL=http://localhost:3000
VITE_KEYCLOAK_URL=https://localhost:8180
VITE_KEYCLOAK_REALM=baw-dev
VITE_KEYCLOAK_CLIENT_ID=admin-portal
VITE_KEYCLOAK_REDIRECT_URI=http://localhost:5173/auth/callback
VITE_KEYCLOAK_LOGOUT_REDIRECT_URI=http://localhost:5173/login
```

- Update `src/services/api.ts` to replace the mocked async functions with real Axios calls.
- Demo login accepts any non-empty email and password.
