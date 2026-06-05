# Nutrimind

Sistema acadêmico de apoio à nutrição comportamental, desenvolvido para a entrega do **II InterConnect Evolution - Prototipagem Técnica**.

A entrega principal é uma aplicação web **React + Vite**, publicada pela Vercel e conectada ao Supabase. O repositório também preserva a implementação desktop **Java 17 + Swing + SQLite** produzida durante o desenvolvimento.

## Ambiente Publicado

- Aplicacao web: `https://nutrimind-two.vercel.app`
- Projeto Supabase: `nutrimind`
- Project ref: `hcobzfxkkvezlopktlzf`
- Regiao: `sa-east-1`

O banco, o login real, as politicas RLS e a Edge Function estao publicados. Para apresentacao gratuita, cadastre uma chave valida como segredo `GEMINI_API_KEY` no gerenciamento de segredos das Edge Functions do Supabase. A chave nunca deve ser colocada no frontend ou versionada no Git.

## Integrantes

- Pessoa 1
- Pessoa 2
- Pessoa 3
- Pessoa 4
- Pessoa 5
- Pessoa 6
- Pessoa 7

## Escopo

O MVP contempla:

- login e cadastro com Supabase Auth;
- cadastro, listagem, edição e inativação de pacientes;
- registro de consultas vinculadas aos pacientes;
- consentimento, gravação pelo navegador, áudio enviado para análise ou transcrição manual e observações clínicas;
- análise obrigatória por IA para resumo, riscos, relatório e plano inicial;
- aprovação profissional do plano alimentar;
- banco PostgreSQL do Supabase com PK/FK e RLS;
- documentação com DER, diagramas e checklist.

Não fazem parte do escopo final: pagamentos reais, diagnóstico automático ou prescrição sem revisão profissional.

## Requisito de IA

A IA é usada como apoio ao nutricionista, não como substituta da decisão profissional. Para a apresentacao academica, o provedor principal e Gemini API no free tier. A chave deve ser cadastrada somente como segredo da Edge Function do Supabase:

```text
GEMINI_API_KEY=sua-chave
```

Modelos configuráveis:

- `GEMINI_ANALYSIS_MODEL`: padrao `gemini-2.0-flash`
- `GEMINI_TRANSCRIPTION_MODEL`: padrao `gemini-2.0-flash`
- `OPENAI_TRANSCRIPTION_MODEL`: padrão `gpt-4o-mini-transcribe`
- `OPENAI_ANALYSIS_MODEL`: padrão `gpt-5.4-mini`

Se `GEMINI_API_KEY` existir, a Edge Function usa Gemini. Se nao existir e `OPENAI_API_KEY` existir, usa OpenAI. Sem nenhuma chave configurada, o sistema mantém os cadastros disponíveis, mas bloqueia a análise real. O ambiente local oferece uma simulação claramente identificada para desenvolvimento da interface.

No Supabase Dashboard, abra o projeto `nutrimind`, acesse o gerenciamento de segredos das Edge Functions e adicione:

```text
GEMINI_API_KEY=...
```

Para pegar a chave Gemini gratuita, use `https://aistudio.google.com/apikey`. O segredo fica disponivel imediatamente para a funcao publicada; nao e necessario refazer o deploy.

## Aplicação Web

Pré-requisito: Node.js 20 ou superior.

```powershell
cd web
npm install
npm run dev
```

Configure `web/.env.local` a partir de `web/.env.example`:

```text
VITE_SUPABASE_URL=https://seu-projeto.supabase.co
VITE_SUPABASE_PUBLISHABLE_KEY=sb_publishable_sua_chave_publica
```

O schema, as políticas RLS e a Edge Function estão em `supabase/`. O deploy da Vercel usa `vercel.json`.

## Aplicação Desktop

Pré-requisito: Java 17.

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-desktop.ps1
```

O script baixa as dependências em `lib/`, compila o projeto legado e inicia o aplicativo Swing.

Credenciais de demonstração:

- Nutricionista: `nutri@nutrimind.com` / `123456`
- Administrador: `admin@nutrimind.com` / `admin123`

## Fluxo Principal

1. O nutricionista autentica na aplicação web.
2. Cadastra ou seleciona um paciente.
3. Registra consentimento, notas clínicas e grava a consulta pelo navegador ou informa transcrição manual.
4. Encerra a consulta e solicita apoio obrigatório da IA.
5. O sistema salva resumo, alertas, relatório e plano em revisão.
6. O nutricionista registra decisões frente aos alertas.
7. O nutricionista revisa e aprova o plano alimentar antes de utilizá-lo.

## Organização no GitHub

O grupo deve trabalhar com branches e Pull Requests:

- `pessoa1-login-readme`
- `pessoa2-banco-der`
- `pessoa3-modelos-poo`
- `pessoa4-dao-crud`
- `pessoa5-interface-swing`
- `pessoa6-controllers-fluxo`
- `pessoa7-ia-testes-docs`

Ninguém deve trabalhar direto na `main`. Cada integrante abre um Pull Request com sua parte.

## Documentação

- [Divisão de tarefas](docs/divisao-de-tarefas.md)
- [DER lógico](docs/DER.md)
- [Diagrama de componentes](docs/diagrama-componentes.md)
- [Diagrama de implantação](docs/diagrama-implantacao.md)
- [Checklist de qualidade](docs/checklist-qualidade.md)
- [Cuidados com IA](docs/ia-openai.md)
- [Teste final](docs/teste-final.md)

## Aviso Ético

O Nutrimind é um sistema acadêmico demonstrativo. A IA apoia a organização da consulta, mas não substitui anamnese, avaliação presencial, diagnóstico, prescrição nutricional ou encaminhamento profissional.

O áudio bruto da consulta não é persistido no banco pelo fluxo principal; ele é usado apenas para transcrição/análise da Edge Function. O sistema salva transcrição, resumo, alertas, relatório e plano.
