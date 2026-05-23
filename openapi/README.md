# OpenAPI specs

Place the exported OpenAPI documents for each service in the matching folder:

- `openapi/identity/openapi.json`
- `openapi/document/openapi.json`
- `openapi/workflow/openapi.json`
- `openapi/connector/openapi.json`

`orval.config.js` is already pointed at these files.

To download fresh specs from the backend, run:

`npm run download:openapi`
