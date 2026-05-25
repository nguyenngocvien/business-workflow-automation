const sharedAxiosMutator = './src/lib/orvalMutator.ts';

export default {
  identity: {
    input: './openapi/identity/openapi.json',
    output: {
      target: './src/api/identity/index.ts',
      client: 'axios',
      override: {
        mutator: sharedAxiosMutator,
      },
    },
  },
  document: {
    input: './openapi/document/openapi.json',
    output: {
      target: './src/api/document/index.ts',
      client: 'axios',
      override: {
        mutator: sharedAxiosMutator,
      },
    },
  },
  workflow: {
    input: './openapi/workflow/openapi.json',
    output: {
      target: './src/api/workflow/index.ts',
      client: 'axios',
      override: {
        mutator: sharedAxiosMutator,
      },
    },
  },
  connector: {
    input: './openapi/connector/openapi.json',
    output: {
      target: './src/api/connector/index.ts',
      client: 'axios',
      override: {
        mutator: sharedAxiosMutator,
      },
    },
  },
};
