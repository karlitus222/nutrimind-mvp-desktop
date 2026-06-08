# Nutrimind

O Nutrimind é um sistema acadêmico de apoio à nutrição comportamental, desenvolvido para a entrega do **II InterConnect Evolution - Prototipagem Técnica**.

Nosso objetivo foi criar uma ferramenta que ajude o nutricionista a organizar atendimentos, registrar pacientes, acompanhar consultas e usar inteligência artificial como apoio para identificar pontos de atenção no relato do paciente. A IA não substitui a avaliação profissional: ela apenas auxilia na organização das informações e na geração de alertas que precisam ser revisados pelo nutricionista.

## Integrantes

- João Victor Martins Freitas
- Pedro Henrique Dias Carneiro Matos Silva
- David Gabriel Macedo Carvalho Lima
- Carlos Gabriel Raposo Landim
- Roger Oliveira Feitosa
- João Victor da Cunha Oliveira
- Luiz Eduardo Rios Barradas
- Guilherme Henrique Macedo Estrela

## Entrega

A entrega principal do projeto é um aplicativo desktop desenvolvido em **Java 17**, com interface em **Swing** e banco de dados **SQLite**.

O projeto contempla os requisitos técnicos do MVP:

- arquitetura MVC;
- uso de DAO para persistência;
- Singleton para conexão com o banco;
- CRUDs de entidades relacionadas;
- consultas vinculadas a pacientes;
- banco SQLite com chaves primárias e estrangeiras;
- documentação técnica com DER, diagramas e checklist;
- fluxo de consulta com apoio de IA.

Também mantivemos uma versão web demonstrativa para apoio visual na apresentação, mas o sistema principal do trabalho é o desktop Java.

## Funcionalidades

- Login com perfis de nutricionista e administrador.
- Cadastro, edição, listagem e inativação de pacientes.
- Cadastro administrativo de nutricionistas.
- Registro de consultas vinculadas ao paciente.
- Consentimento para uso de áudio e vídeo.
- Gravação de áudio em `.wav`.
- Transcrição e análise com apoio de IA.
- Geração de alertas com severidade e justificativa.
- Registro da decisão clínica sobre cada alerta.
- Relatório da consulta.
- Plano alimentar em revisão.
- Aprovação profissional do plano alimentar.
- Histórico e dados de demonstração.

O sistema não realiza diagnóstico automático e não substitui prescrição, anamnese ou avaliação clínica feita pelo nutricionista.

## Executável Windows

Para facilitar a avaliação e a apresentação, disponibilizamos um pacote Windows com o aplicativo já empacotado.

- [Baixar Nutrimind-Windows.zip](https://github.com/karlitus222/nutrimind-mvp-desktop/releases/download/v1.0.0/Nutrimind-Windows.zip)
- [Ver release v1.0.0](https://github.com/karlitus222/nutrimind-mvp-desktop/releases/tag/v1.0.0)

Como executar:

1. Baixe o arquivo `Nutrimind-Windows.zip`.
2. Extraia o ZIP.
3. Abra a pasta `Nutrimind`.
4. Execute `Nutrimind.exe`.

O pacote já inclui o runtime Java necessário para abrir o sistema.

## Credenciais de Demonstração

| Perfil | E-mail | Senha |
| --- | --- | --- |
| Nutricionista | `nutri@nutrimind.com` | `123456` |
| Administrador | `admin@nutrimind.com` | `admin123` |

## Inteligência Artificial

A IA é usada no fluxo de consulta para apoiar a análise do relato do paciente. Ela pode auxiliar na transcrição, no resumo da consulta, na criação de alertas e na sugestão inicial de plano alimentar.

Por segurança, nenhuma chave de API foi salva no repositório, no ZIP ou no executável. O sistema pode ser aberto e navegado sem chave, incluindo login, cadastros, telas, banco e CRUDs. A chave externa só é necessária para executar uma análise real por IA.

Configuração recomendada para uso com Gemini:

```text
GEMINI_API_KEY=sua-chave
GEMINI_ANALYSIS_MODEL=gemini-2.5-flash-lite
GEMINI_TRANSCRIPTION_MODEL=gemini-2.5-flash-lite
```

Configuração alternativa com OpenAI:

```text
OPENAI_API_KEY=sua-chave
OPENAI_TRANSCRIPTION_MODEL=gpt-4o-mini-transcribe
OPENAI_ANALYSIS_MODEL=gpt-5-mini
```

Sem chave configurada, o fluxo de IA real fica indisponível, mas o restante do sistema continua funcionando para demonstração técnica.

## Fluxo Principal

1. O nutricionista acessa o sistema.
2. Seleciona ou cadastra um paciente.
3. Registra uma consulta.
4. Informa observações clínicas e, quando houver consentimento, grava áudio.
5. Solicita o apoio da IA.
6. O sistema registra resumo, alertas, relatório e plano em revisão.
7. O nutricionista avalia os alertas e registra sua decisão.
8. O plano alimentar só é aprovado após revisão profissional.

## Tecnologias Utilizadas

| Área | Tecnologia |
| --- | --- |
| Aplicação principal | Java 17 |
| Interface gráfica | Swing |
| Banco de dados | SQLite |
| Persistência | JDBC + DAO |
| Arquitetura | MVC |
| IA principal | Gemini API |
| IA alternativa | OpenAI API |
| Web demonstrativa | React + Vite |
| Backend da web demo | Supabase |
| Deploy da web demo | Vercel |

## Como Rodar pelo Código

Para rodar o projeto a partir do código-fonte, é necessário ter Java 17 instalado.

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-desktop.ps1
```

Para gerar novamente o executável Windows:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\build-desktop-exe.ps1
```

O executável gerado localmente fica em:

```text
desktop/dist/Nutrimind/Nutrimind.exe
```

## Testes

O projeto possui um teste de fumaça para validar compilação, login, dados iniciais, fluxo de consulta, alertas, relatório, plano alimentar e permissões.

```powershell
powershell -ExecutionPolicy Bypass -File scripts\test-desktop.ps1
```

Resultado esperado:

```text
BuildScriptTest OK
SmokeTest OK
Testes desktop concluidos.
```

## Web Demonstrativa

Além do desktop, mantivemos uma versão web para apoio na apresentação:

- URL: `https://nutrimind-two.vercel.app`
- Login: `demo@nutrimind.app`
- Senha: `Nutrimind@2026`

Essa versão ajuda a demonstrar a ideia do produto pelo navegador, mas não substitui a entrega desktop.

## Estrutura do Repositório

```text
desktop/     Aplicação Java Swing, SQLite, MVC, DAOs e testes
docs/        DER, diagramas, checklist e documentação técnica
scripts/     Scripts de execução, teste e empacotamento
supabase/    Estrutura usada pela versão web demonstrativa
web/         Aplicação React/Vite da versão web demonstrativa
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

## Observação Ética

O Nutrimind é um sistema acadêmico e demonstrativo. A inteligência artificial é utilizada somente como apoio à organização das informações da consulta. A decisão final, o diagnóstico, a prescrição e qualquer encaminhamento continuam sendo responsabilidade do profissional de nutrição.
