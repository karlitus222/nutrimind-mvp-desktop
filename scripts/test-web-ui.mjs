import { spawnSync } from 'node:child_process';
import { mkdir, stat } from 'node:fs/promises';
import { join, resolve } from 'node:path';

const chrome = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const root = resolve(import.meta.dirname, '..');
const output = join(root, 'desktop', 'out', 'web-previews');
await mkdir(output, { recursive: true });

try {
  const response = await fetch('http://127.0.0.1:5174/');
  if (!response.ok) throw new Error(`HTTP ${response.status}`);
} catch {
  throw new Error('Servidor web indisponivel em http://127.0.0.1:5174/. Inicie o Vite antes do teste.');
}

const captures = [
  ['01-login-desktop.png', '1440,980', 'http://127.0.0.1:5174/'],
  ['02-dashboard-desktop.png', '1440,980', 'http://127.0.0.1:5174/?demo=1&view=dashboard'],
  ['03-consulta-desktop.png', '1440,1100', 'http://127.0.0.1:5174/?demo=1&view=consultation'],
  ['04-dashboard-mobile.png', '500,844', 'http://127.0.0.1:5174/?demo=1&view=dashboard'],
];

for (const [fileName, windowSize, url] of captures) {
  const file = join(output, fileName);
  const result = spawnSync(chrome, [
    '--headless=new',
    '--disable-gpu',
    '--no-first-run',
    `--screenshot=${file}`,
    `--window-size=${windowSize}`,
    url,
  ], { stdio: 'inherit' });
  if (result.status !== 0) {
    throw new Error(`Falha ao capturar ${fileName}.`);
  }
  const info = await stat(file);
  if (info.size < 5000) {
    throw new Error(`Captura invalida: ${fileName}.`);
  }
}

console.log(`Capturas salvas em ${output}`);
