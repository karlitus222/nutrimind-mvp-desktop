# Nutrimind

Sistema academico de apoio a nutricao comportamental, desenvolvido para a entrega do **II InterConnect Evolution - Prototipagem Tecnica**.

## Entrega Principal

O projeto sera apresentado como um unico aplicativo desktop:

- **Java 17**
- **Swing**
- **SQLite**
- **Arquitetura MVC**
- **DAO para persistencia**
- **Singleton de conexao**
- **CRUDs e consultas relacionadas a pacientes**
- **IA assistiva obrigatoria no fluxo de analise**

A aplicacao web React/Vite continua no repositorio e publicada na Vercel como apoio visual/demo, mas nao substitui o desktop na entrega principal.

## Integrantes

- Pessoa 1
- Pessoa 2
- Pessoa 3
- Pessoa 4
- Pessoa 5
- Pessoa 6
- Pessoa 7

## Escopo do Desktop

O desktop contempla:

- login demonstrativo com perfil de nutricionista e administrador;
- cadastro, listagem, edicao e inativacao de pacientes;
- cadastro administrativo de nutricionistas;
- registro de consultas vinculadas aos pacientes;
- consentimento de audio/video;
- gravacao de audio em `.wav`;
- vinculo opcional de video;
- transcricao e analise por IA;
- alertas com severidade;
- decisao clinica sobre alertas;
- relatorio da consulta;
- plano alimentar em revisao;
- aprovacao profissional do plano;
- banco SQLite com PK/FK;
- documentacao tecnica com DER, componentes, implantacao e checklist.

Nao fazem parte do escopo final: pagamentos reais, diagnostico automatico ou prescricao sem revisao profissional.

## IA no Desktop

A IA e apoio a decisao. O nutricionista sempre revisa e aprova os resultados antes de usar.

O desktop agora reaproveita o fluxo do web:

- usa **Gemini API** como provedor principal quando `GEMINI_API_KEY` estiver configurada;
- usa **OpenAI API** como fallback quando nao houver Gemini e `OPENAI_API_KEY` existir;
- bloqueia analise, transcricao e relatorio inteligente se nenhuma chave estiver configurada.

Configuracao recomendada para apresentacao gratuita:

```text
GEMINI_API_KEY=sua-chave
GEMINI_ANALYSIS_MODEL=gemini-2.5-flash-lite
GEMINI_TRANSCRIPTION_MODEL=gemini-2.5-flash-lite
```

Fallback pago:

```text
OPENAI_API_KEY=sua-chave
OPENAI_TRANSCRIPTION_MODEL=gpt-4o-mini-transcribe
OPENAI_ANALYSIS_MODEL=gpt-5-mini
```

Nunca coloque chaves de API no GitHub.

## Como Rodar o Desktop

Pre-requisito: Java 17 instalado.

No Explorador de Arquivos, abra a pasta:

```text
C:\Users\carlo\OneDrive\Documentos\New project 6
```

Para rodar pelo terminal:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-desktop.ps1
```

Credenciais de demonstracao:

- Nutricionista: `nutri@nutrimind.com` / `123456`
- Administrador: `admin@nutrimind.com` / `admin123`

## Fluxo Principal

1. O nutricionista entra no desktop.
2. Cadastra ou seleciona um paciente.
3. Registra consentimento, notas clinicas e transcricao manual ou audio gravado.
4. Solicita analise obrigatoria por IA.
5. O sistema salva consulta, resumo, alertas, relatorio e plano em revisao.
6. O nutricionista registra decisoes frente aos alertas.
7. O nutricionista revisa e aprova o plano alimentar.

## Aplicacao Web Mantida

A versao web continua disponivel para demonstracao visual:

- URL: `https://nutrimind-two.vercel.app`
- Stack: React + Vite + Supabase + Vercel
- Login demo: `demo@nutrimind.app` / `Nutrimind@2026`

Ela foi mantida porque ajuda no pitch e no teste pelo celular, mas o requisito academico principal fica coberto pelo desktop Java.

## Organizacao no GitHub

O grupo deve trabalhar com branches e Pull Requests. Ninguem deve trabalhar direto na `main`.

Sugestao de divisao:

- `pessoa1-login-readme`
- `pessoa2-banco-der`
- `pessoa3-modelos-poo`
- `pessoa4-dao-crud`
- `pessoa5-interface-swing`
- `pessoa6-controllers-fluxo`
- `pessoa7-ia-testes-docs`

## Documentacao

- [Divisao de tarefas](docs/divisao-de-tarefas.md)
- [DER logico](docs/DER.md)
- [Diagrama de componentes](docs/diagrama-componentes.md)
- [Diagrama de implantacao](docs/diagrama-implantacao.md)
- [Checklist de qualidade](docs/checklist-qualidade.md)
- [Cuidados com IA](docs/ia-openai.md)
- [Teste final](docs/teste-final.md)

## Aviso Etico

O Nutrimind e um sistema academico demonstrativo. A IA apoia a organizacao da consulta, mas nao substitui anamnese, avaliacao presencial, diagnostico, prescricao nutricional ou encaminhamento profissional.
