export default {
  identity: {
    input: './openapi/identity/openapi.json',

    output: {
      target: './src/api/identity/index.ts',
      client: 'axios',

      override: {
        mutator: {
          path: './src/lib/orvalMutator.ts',
          name: 'orvalMutator',
        },
      },
    },
  },

  document: {
    input: './openapi/document/openapi.json',

    output: {
      target: './src/api/document/index.ts',
      client: 'axios',

      override: {
        mutator: {
          path: './src/lib/orvalMutatorDocument.ts',
          name: 'orvalMutator',
        },
      },
    },
  },

  workflow: {
    input: './openapi/workflow/openapi.json',

    output: {
      target: './src/api/workflow/index.ts',
      client: 'axios',

      override: {
        mutator: {
          path: './src/lib/orvalMutator.ts',
          name: 'orvalMutator',
        },
      },
    },
  },

  connector: {
    input: './openapi/connector/openapi.json',

    output: {
      target: './src/api/connector/index.ts',
      client: 'axios',

      override: {
        mutator: {
          path: './src/lib/orvalMutator.ts',
          name: 'orvalMutator',
        },
      },
    },
  },
};
