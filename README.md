# User Dashboard

Starter frontend scaffold for a Vite + React + TypeScript + Tailwind app with Orval-generated API clients.

## Setup

```bash
npm install
npm run dev
```

## Generate API clients

Make sure your backend OpenAPI docs are available, then run:

```bash
npm run gen:api
```

This will generate clients into:

- `src/api/identity/index.ts`
- `src/api/document/index.ts`
- `src/api/workflow/index.ts`
- `src/api/connector/index.ts`

## Environment

Create a `.env` file if you want to override the API base URL used by `src/services/http.ts`:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

