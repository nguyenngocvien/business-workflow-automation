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

- The generated Orval clients call service-scoped paths like `/connector/api/connection-definitions`, `/document/api/files`, `/identity/api/users`, and `/workflow/api/...`.
- `VITE_API_BASE_URL` should point at the API gateway origin, so in local development that is `http://localhost:8080`.
- The app also reads `VITE_KEYCLOAK_URL`, `VITE_KEYCLOAK_REALM`, and `VITE_KEYCLOAK_CLIENT_ID` for Keycloak login.
- `VITE_KEYCLOAK_REDIRECT_URI` and `VITE_KEYCLOAK_LOGOUT_REDIRECT_URI` are optional overrides. By default, the login flow returns to `/login` and completes there.
- Example `.env`:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_KEYCLOAK_URL=https://localhost:8180
VITE_KEYCLOAK_REALM=baw-dev
VITE_KEYCLOAK_CLIENT_ID=admin-portal
VITE_KEYCLOAK_REDIRECT_URI=http://localhost:5173/login
VITE_KEYCLOAK_LOGOUT_REDIRECT_URI=http://localhost:5173/login
```

- The shared Axios client is defined in [src/lib/http.ts](/D:/projects/admin-dashboard/src/lib/http.ts) and is the only place that needs the gateway base URL.
