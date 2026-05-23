export default {
  identity: {
    input: './openapi/identity/openapi.json',
    output: {
      target: './src/api/identity/index.ts',
      client: 'axios',
    },
  },
  document: {
    input: './openapi/document/openapi.json',
    output: {
      target: './src/api/document/index.ts',
      client: 'axios',
    },
  },
  workflow: {
    input: './openapi/workflow/openapi.json',
    output: {
      target: './src/api/workflow/index.ts',
      client: 'axios',
    },
  },
  connector: {
    input: './openapi/connector/openapi.json',
    output: {
      target: './src/api/connector/index.ts',
      client: 'axios',
    },
  },
};
