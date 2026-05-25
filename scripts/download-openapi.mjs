import { mkdir, writeFile } from 'node:fs/promises';
import path from 'node:path';

const specs = [
  {
    name: 'identity',
    url: 'http://localhost:8080/identity/v3/api-docs',
    output: './openapi/identity/openapi.json',
  },
  {
    name: 'document',
    url: 'http://localhost:8080/document/v3/api-docs',
    output: './openapi/document/openapi.json',
  },
  {
    name: 'workflow',
    url: 'http://localhost:8080/workflow/v3/api-docs',
    output: './openapi/workflow/openapi.json',
  },
  {
    name: 'connector',
    url: 'http://localhost:8080/connector/v3/api-docs',
    output: './openapi/connector/openapi.json',
  },
];

async function downloadSpec({ name, url, output }) {
  const response = await fetch(url);

  if (!response.ok) {
    throw new Error(`Failed to download ${name} spec from ${url} (${response.status} ${response.statusText})`);
  }

  const text = await response.text();

  let json;
  try {
    json = JSON.parse(text);
  } catch {
    throw new Error(`Downloaded ${name} spec from ${url}, but the response was not valid JSON.`);
  }

  const outputPath = path.resolve(process.cwd(), output);
  await mkdir(path.dirname(outputPath), { recursive: true });
  await writeFile(outputPath, `${JSON.stringify(json, null, 2)}\n`, 'utf8');

  console.log(`Saved ${name} spec to ${output}`);
}

try {
  for (const spec of specs) {
    await downloadSpec(spec);
  }
} catch (error) {
  console.error(error instanceof Error ? error.message : error);
  process.exitCode = 1;
}
