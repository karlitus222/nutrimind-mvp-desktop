# Nutrimind

Sistema acadêmico de apoio à nutrição comportamental, desenvolvido para a entrega do **II InterConnect Evolution - Prototipagem Técnica**.

O Nutrimind ajuda o nutricionista a registrar pacientes, organizar consultas, analisar relatos com apoio de IA, gerar alertas, revisar relatórios e aprovar planos alimentares. A IA é usada apenas como apoio à decisão: o profissional continua responsável por avaliar, ajustar e aprovar tudo.

## Integrantes

- João Victor Martins Freitas
- Pedro Henrique Dias Carneiro Matos Silva
- David Gabriel Macedo Carvalho Lima
- Carlos Gabriel Raposo Landim
- Roger Oliveira Feitosa
- João Victor da Cunha Oliveira
- Luiz Eduardo Rios Barradas
- Guilherme Henrique Macedo Estrela

## Entrega Principal

A entrega principal é um aplicativo desktop:

- Java 17
- Swing
- SQLite
- Arquitetura MVC
- DAOs para persistência
- Singleton de conexão com banco
- CRUDs e consultas relacionadas a pacientes
- IA assistiva obrigatória no fluxo de análise

A aplicação web React/Vite também foi mantida no repositório e publicada na Vercel como apoio visual para o pitch, mas o sistema principal avaliado é o desktop Java.

## Download para Apresentação

Para usar o Nutrimind sem abrir PowerShell e sem instalar Java manualmente, baixe o pacote Windows pela release:

- [Download do Nutrimind-Windows.zip](https://github.com/karlitus222/nutrimind-mvp-desktop/releases/download/v1.0.0/Nutrimind-Windows.zip)
- [Página da release v1.0.0](https://github.com/karlitus222/nutrimind-mvp-desktop/releases/tag/v1.0.0)

Como abrir:

1. Baixe `Nutrimind-Windows.zip`.
2. Extraia o ZIP.
3. Abra a pasta `Nutrimind`.
4. Dê dois cliques em `Nutrimind.exe`.

O pacote já inclui runtime Java próprio.

## Funcionalidades

- Login demonstrativo com perfis de nutricionista e administrador.
- Cadastro, listagem, edição e inativação de pacientes.
- Cadastro administrativo de nutricionistas.
- Registro de consultas vinculadas aos pacientes.
- Consentimento de áudio e vídeo.
- Gravação de áudio em `.wav`.
- Vínculo opcional de vídeo à consulta.
- Transcrição e análise por IA.
- Alertas com severidade e justificativa.
- Registro de decisão clínica frente aos alertas.
- Relatório da consulta.
- Plano alimentar em revisão.
- Aprovação profissional do plano alimentar.
- Banco SQLite com chaves primárias e estrangeiras.
- Documentação técnica com DER, diagramas e checklist.

Fora do escopo: pagamentos reais, diagnóstico automático e prescrição sem revisão profissional.

## Tecnologias

| Parte | Tecnologia |
| --- | --- |
| Desktop principal | Java 17 + Swing |
| Banco local | SQLite |
| Persistência | DAO + JDBC |
| Arquitetura | MVC |
| IA principal | Gemini API |
| IA fallback | OpenAI API |
| Web demo | React + Vite |
| Backend web demo | Supabase |
| Deploy web demo | Vercel |

## Como Rodar em Desenvolvimento

Use esta opção apenas para desenvolver, testar ou gerar um novo pacote.

Pré-requisito para desenvolvimento: Java 17 instalado.

Para desenvolvimento, na pasta do projeto, execute:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-desktop.ps1
```

## Executável Windows

Para apresentação, o projeto pode ser empacotado como executável Windows. Assim o usuário não precisa abrir PowerShell para usar o sistema.

O pacote publicado fica na release:

```text
https://github.com/karlitus222/nutrimind-mvp-desktop/releases/tag/v1.0.0
```

Ao gerar localmente, o executável fica em:

```text
desktop/dist/Nutrimind/Nutrimind.exe
```

Para gerar o executável:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\build-desktop-exe.ps1
```

Depois disso, basta abrir a pasta `desktop/dist/Nutrimind` e dar dois cliques em `Nutrimind.exe`.

O pacote inclui runtime Java próprio, então a pessoa que for apenas testar o sistema não precisa instalar Java separadamente.

Credenciais de demonstração:

| Perfil | E-mail | Senha |
| --- | --- | --- |
| Nutricionista | `nutri@nutrimind.com` | `123456` |
| Administrador | `admin@nutrimind.com` | `admin123` |

## Configuração da IA

O professor não precisa configurar chave nenhuma apenas para abrir o sistema, fazer login, cadastrar pacientes, navegar pelas telas e verificar a estrutura Java/Swing/SQLite.

A chave só é necessária se a análise real por IA for testada no computador dele. Por segurança, nenhuma chave de API é salva no GitHub, no ZIP ou dentro do `.exe`.

Para a apresentação, existem duas opções:

1. Rodar a demonstração da IA no computador do grupo, onde a chave já pode estar configurada.
2. Se o professor quiser testar a IA real no computador dele, ele precisará configurar uma chave própria de Gemini ou OpenAI como variável de ambiente.

Opção recomendada para apresentação:

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

Sem chave configurada, o sistema abre normalmente para cadastros e navegação, mas bloqueia transcrição, análise inteligente e relatório gerado por IA.

Nunca coloque chaves de API no GitHub.

## Fluxo de Uso

1. O nutricionista entra no desktop.
2. Cadastra ou seleciona um paciente.
3. Registra consentimento, notas clínicas e transcrição manual ou áudio gravado.
4. Solicita análise por IA.
5. O sistema salva consulta, resumo, alertas, relatório e plano em revisão.
6. O nutricionista registra decisões frente aos alertas.
7. O nutricionista revisa e aprova o plano alimentar.

## Como Testar

Execute:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\test-desktop.ps1
```

Resultado esperado:

```text
BuildScriptTest OK
SmokeTest OK
Testes desktop concluidos.
```

## Web Demo

A versão web continua disponível como apoio para apresentação e teste pelo celular:

- URL: `https://nutrimind-two.vercel.app`
- Stack: React + Vite + Supabase + Vercel
- Login demo: `demo@nutrimind.app` / `Nutrimind@2026`

## Estrutura do Repositório

```text
desktop/     Aplicação Java Swing, SQLite, MVC, DAOs e testes
docs/        DER, diagramas, checklist, guia GitHub e documentação da IA
scripts/     Scripts de compilação, execução e testes
supabase/    Schema e Edge Function da versão web demo
web/         Aplicação React/Vite usada como apoio visual
```

## Documentação

- [Divisão de tarefas](docs/divisao-de-tarefas.md)
- [DER lógico](docs/DER.md)
- [Diagrama de componentes](docs/diagrama-componentes.md)
- [Diagrama de implantação](docs/diagrama-implantacao.md)
- [Checklist de qualidade](docs/checklist-qualidade.md)
- [Cuidados com IA](docs/ia-openai.md)
- [Teste final](docs/teste-final.md)
- [Fluxo no GitHub](docs/fluxo-github.md)

## Aviso Ético

O Nutrimind é um sistema acadêmico demonstrativo. A IA apoia a organização da consulta, mas não substitui anamnese, avaliação presencial, diagnóstico, prescrição nutricional ou encaminhamento profissional.
