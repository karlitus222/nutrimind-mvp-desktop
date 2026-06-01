# Nutrimind

Sistema acadêmico de apoio à nutrição comportamental, desenvolvido como MVP desktop para a entrega do **II InterConnect Evolution - Prototipagem Técnica**.

O projeto será entregue como um único aplicativo **Java 17 + Swing + SQLite**, com arquitetura MVC, DAOs, Singleton de conexão, CRUDs, consultas relacionadas a pacientes e documentação técnica.

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

- login demonstrativo;
- cadastro, listagem, edição e inativação de pacientes;
- registro de consultas vinculadas aos pacientes;
- relatório da consulta;
- apoio por IA para resumo e alertas, quando configurado;
- banco SQLite com PK/FK;
- documentação com DER, diagramas e checklist.

Não fazem parte do escopo final: sistema web separado, pagamentos, diagnóstico automático ou prescrição sem revisão profissional.

## Requisito de IA

A IA é usada como apoio ao nutricionista, não como substituta da decisão profissional. Para habilitar a análise assistida, configure a variável:

```powershell
$env:OPENAI_API_KEY="sua-chave"
```

Modelos configuráveis:

- `OPENAI_TRANSCRIPTION_MODEL`: padrão `gpt-4o-mini-transcribe`
- `OPENAI_ANALYSIS_MODEL`: padrão `gpt-5-mini`

Sem chave configurada, o sistema deve apresentar mensagem clara e manter os demais cadastros disponíveis.

## Como Rodar

Pré-requisito: Java 17.

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-desktop.ps1
```

O script baixa as dependências em `lib/`, compila o projeto e inicia o aplicativo Swing.

Credenciais de demonstração:

- Nutricionista: `nutri@nutrimind.com` / `123456`
- Administrador: `admin@nutrimind.com` / `admin123`

## Fluxo Principal

1. O nutricionista autentica no desktop.
2. Cadastra ou seleciona um paciente.
3. Registra consentimento, notas clínicas e transcrição manual ou áudio.
4. Encerra a consulta e solicita apoio da IA, se configurado.
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

## Aviso Ético

O Nutrimind é um sistema acadêmico demonstrativo. A IA apoia a organização da consulta, mas não substitui anamnese, avaliação presencial, diagnóstico, prescrição nutricional ou encaminhamento profissional.
