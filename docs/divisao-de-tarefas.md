# Plano de Desenvolvimento e Divisão de Responsabilidades - Nutrimind MVP Desktop

## Objetivo da Entrega

O grupo desenvolverá um único sistema: **Nutrimind Desktop**, em Java 17 com Swing e SQLite. O MVP deverá permitir cadastrar pacientes, registrar consultas nutricionais, consultar histórico e utilizar IA como apoio opcional para resumir informações e destacar pontos de atenção que serão revisados pelo nutricionista.

Esta divisão de responsabilidades orienta a construção do projeto conforme os requisitos do documento **II InterConnect Evolution - Prototipagem Técnica (MVP)**:

- interface gráfica desktop em Java Swing;
- CRUD completo de pelo menos duas entidades relacionadas;
- arquitetura MVC;
- DER lógico e script SQL com PK/FK;
- demonstração dos pilares de POO;
- padrões DAO e Singleton;
- `README.md`, diagramas, checklist de qualidade e instruções de execução;
- participação comprovada por commits individuais no GitHub.

## Escopo do MVP

Entram na entrega:

- login demonstrativo;
- cadastro, edição, listagem e inativação de pacientes;
- cadastro e histórico de consultas vinculadas aos pacientes;
- relatório da consulta;
- apoio por IA para resumo e alertas, quando houver chave configurada;
- banco SQLite, documentação e testes de validação.

Não entram na entrega:

- sistema web separado;
- pagamentos ou assinaturas;
- diagnóstico automático;
- prescrição automática sem revisão profissional;
- funcionalidades comerciais além do necessário para demonstrar o MVP.

## Divisão por Integrante

### Pessoa 1 - Integração, autenticação e entrega

**Responsabilidade principal:** implementar a integração principal, autenticação e organização da entrega no GitHub.

**Atividades:**

- desenvolver o fluxo de login e perfis de usuário;
- integrar as telas ao fluxo principal do aplicativo;
- organizar o `README.md` final com descrição, integrantes e execução;
- revisar a integração das partes do grupo;
- preparar a versão final submetida no AVA.

**Arquivos sob responsabilidade:**

- `desktop/src/main/java/br/com/nutrimind/App.java`
- `desktop/src/main/java/br/com/nutrimind/controller/AppController.java`
- `desktop/src/main/java/br/com/nutrimind/service/AuthService.java`
- `desktop/src/main/java/br/com/nutrimind/view/LoginPanel.java`
- `README.md`

**Evidência para avaliação:** aplicação iniciando corretamente, login funcional e README completo.

**Commit sugerido:** `feat: integrar login e inicializacao do aplicativo desktop`

### Pessoa 2 - Banco de dados e DER

**Responsabilidade principal:** modelar e implementar a persistência relacional do MVP.

**Atividades:**

- definir entidades principais, relacionamentos, chaves primárias e estrangeiras;
- criar o script SQL de criação do banco;
- documentar o DER lógico;
- preparar dados iniciais suficientes para demonstração;
- validar os relacionamentos entre paciente, consulta, usuário e relatório.

**Arquivos sob responsabilidade:**

- `desktop/sql/schema.sql`
- `desktop/src/main/java/br/com/nutrimind/config/Database.java`
- `desktop/src/main/java/br/com/nutrimind/config/DatabaseInitializer.java`
- `docs/DER.md`

**Evidência para avaliação:** DER coerente com o SQL e banco SQLite criado com PK/FK corretas.

**Commit sugerido:** `feat: modelar banco sqlite e documentar der logico`

### Pessoa 3 - Modelos e pilares de POO

**Responsabilidade principal:** demonstrar claramente os conceitos de Programação Orientada a Objetos cobrados no PDF.

**Atividades:**

- criar e organizar classes de domínio, atributos privados, construtores e métodos;
- implementar herança entre usuários do sistema, quando aplicável;
- demonstrar sobrescrita, interfaces e uso de coleções/generics;
- revisar enums de status e severidade;
- documentar apenas lógicas que realmente necessitem explicação.

**Arquivos sob responsabilidade:**

- `desktop/src/main/java/br/com/nutrimind/model/User.java`
- `desktop/src/main/java/br/com/nutrimind/model/Nutritionist.java`
- `desktop/src/main/java/br/com/nutrimind/model/SystemUser.java`
- `desktop/src/main/java/br/com/nutrimind/model/Patient.java`
- `desktop/src/main/java/br/com/nutrimind/model/Consultation.java`
- `desktop/src/main/java/br/com/nutrimind/model/ConsultationReport.java`
- `desktop/src/main/java/br/com/nutrimind/model/Severity.java`
- `desktop/src/main/java/br/com/nutrimind/model/ConsultationStatus.java`

**Evidência para avaliação:** classes encapsuladas e exemplos identificáveis de herança, interfaces, sobrescrita, coleções e generics.

**Commit sugerido:** `feat: criar modelos e conceitos de poo do mvp`

### Pessoa 4 - DAO, Singleton e CRUD

**Responsabilidade principal:** implementar o acesso aos dados seguindo os padrões exigidos.

**Atividades:**

- criar a interface genérica de CRUD;
- implementar DAO de pacientes e consultas como entidades principais relacionadas;
- validar inserção, listagem, atualização e operações de inativação;
- utilizar a conexão Singleton sem duplicar lógica SQL;
- revisar o tratamento de erros de persistência.

**Arquivos sob responsabilidade:**

- `desktop/src/main/java/br/com/nutrimind/dao/CrudDao.java`
- `desktop/src/main/java/br/com/nutrimind/dao/PatientDao.java`
- `desktop/src/main/java/br/com/nutrimind/dao/ConsultationDao.java`
- `desktop/src/main/java/br/com/nutrimind/dao/UserDao.java`
- `desktop/src/main/java/br/com/nutrimind/dao/ReportDao.java`

**Evidência para avaliação:** CRUD funcional de pacientes e consultas, relacionamento persistido e padrões DAO/Singleton visíveis no código.

**Commit sugerido:** `feat: implementar crud e persistencia dao do desktop`

### Pessoa 5 - Interface Swing e usabilidade

**Responsabilidade principal:** desenvolver as telas necessárias para demonstrar o MVP.

**Atividades:**

- desenvolver a navegação da janela principal;
- criar telas de pacientes, consultas e relatórios;
- garantir mensagens claras de validação e erro;
- manter layout consistente e adequado à demonstração;
- remover referências visuais a funcionalidades fora do escopo, como web e assinatura.

**Arquivos sob responsabilidade:**

- `desktop/src/main/java/br/com/nutrimind/view/MainFrame.java`
- `desktop/src/main/java/br/com/nutrimind/view/DashboardPanel.java`
- `desktop/src/main/java/br/com/nutrimind/view/PatientsPanel.java`
- `desktop/src/main/java/br/com/nutrimind/view/ConsultationPanel.java`
- `desktop/src/main/java/br/com/nutrimind/view/ReportsPanel.java`
- `desktop/src/main/java/br/com/nutrimind/view/UiUtil.java`

**Evidência para avaliação:** fluxo visível de paciente para consulta e relatório funcionando na interface desktop.

**Commit sugerido:** `feat: ajustar telas swing para o fluxo do mvp`

### Pessoa 6 - Controllers e fluxo da consulta

**Responsabilidade principal:** conectar interface, regras de negócio e persistência pelo MVC.

**Atividades:**

- desenvolver controllers de paciente e consulta;
- implementar a criação de consulta vinculada ao paciente e ao nutricionista;
- controlar geração e exibição do relatório;
- tratar exceções da aplicação com mensagens compreensíveis;
- garantir que o fluxo básico funcione mesmo sem o recurso opcional de IA.

**Arquivos sob responsabilidade:**

- `desktop/src/main/java/br/com/nutrimind/controller/PatientController.java`
- `desktop/src/main/java/br/com/nutrimind/controller/ConsultationController.java`
- `desktop/src/main/java/br/com/nutrimind/service/ConsultationWorkflowService.java`
- `desktop/src/main/java/br/com/nutrimind/exception/AppException.java`

**Evidência para avaliação:** separação MVC clara e consulta registrada e recuperada pelo sistema.

**Commit sugerido:** `feat: implementar fluxo mvc de consulta e relatorio`

### Pessoa 7 - IA assistiva, testes e qualidade

**Responsabilidade principal:** implementar a IA como apoio opcional e validar a entrega técnica.

**Atividades:**

- implementar a integração OpenAI para gerar resumo e alertas de apoio, sem diagnóstico automático;
- documentar configuração de chave e limitações éticas;
- manter comportamento claro quando a chave de IA não estiver configurada;
- criar ou ampliar testes de login, CRUD, consulta e configuração de IA;
- finalizar checklist de qualidade e diagramas.

**Arquivos sob responsabilidade:**

- `desktop/src/main/java/br/com/nutrimind/service/AiAnalysisService.java`
- `desktop/src/main/java/br/com/nutrimind/service/OpenAiClient.java`
- `desktop/src/main/java/br/com/nutrimind/service/OpenAiRiskAnalysisService.java`
- `desktop/src/main/java/br/com/nutrimind/service/OpenAiTranscriptionService.java`
- `desktop/src/test/java/br/com/nutrimind/SmokeTest.java`
- `docs/ia-openai.md`
- `docs/checklist-qualidade.md`
- `docs/diagrama-componentes.md`
- `docs/diagrama-implantacao.md`

**Evidência para avaliação:** IA apresentada como suporte profissional, testes executáveis e documentação coerente com o MVP.

**Commit sugerido:** `feat: integrar ia assistiva e validar fluxo do mvp`

## Matriz de Requisitos do PDF

| Requisito avaliado | Responsável primário | Evidência esperada |
| --- | --- | --- |
| Desktop Java Swing | Pessoa 5 | Telas executáveis do MVP |
| CRUD de duas entidades relacionadas | Pessoa 4 | Pacientes e consultas persistidos |
| MVC | Pessoa 6 | Pacotes e controllers separados |
| DER e SQL com PK/FK | Pessoa 2 | `docs/DER.md` e `desktop/sql/schema.sql` |
| Pilares de POO | Pessoa 3 | Classes e modelos documentados |
| DAO e Singleton | Pessoa 4 | DAOs e configuração da conexão |
| README e integração final | Pessoa 1 | Instruções e fluxo descritos |
| Qualidade, testes e IA assistiva | Pessoa 7 | Testes, diagramas, checklist e ética |

## Regras para Commits no GitHub

O PDF informa que os commits de todos os membros serão avaliados. Por isso:

1. Cada integrante deve trabalhar em sua própria conta do GitHub.
2. Cada integrante deve realizar commits de sua parte, com mensagem objetiva.
3. Nenhum integrante deve enviar todo o projeto pronto em um único commit.
4. Alterações integradas por outra pessoa devem preservar o histórico do autor original.
5. Antes da entrega, o grupo deve conferir se os sete integrantes aparecem no histórico.

Exemplos de mensagens adequadas:

```text
feat: adicionar cadastro e listagem de pacientes
feat: implementar persistencia de consultas com dao
docs: adicionar der logico e instrucoes do banco
test: validar login e fluxo basico de consulta
fix: corrigir validacao de consentimento na consulta
```

## Cronograma Sugerido

| Data limite | Entrega interna | Integrantes envolvidos |
| --- | --- | --- |
| 28/05/2026 | Banco, DER, modelos e confirmação do escopo desktop | Pessoas 1, 2 e 3 |
| 01/06/2026 | CRUD, controllers e telas principais funcionando | Pessoas 4, 5 e 6 |
| 04/06/2026 | IA assistiva, testes, diagramas e checklist | Pessoa 7 e apoio do grupo |
| 06/06/2026 | Integração geral, correção de erros e README final | Todos |
| 07/06/2026 | Conferência dos commits e ensaio do pitch | Todos |
| 08/06/2026, até 23h59 | Envio do link do repositório no AVA | Pessoa 1 e grupo |
| 12/06/2026, pela manhã | Pitch do MVP | Todos |

## Roteiro Funcional para Demonstração

1. Abrir o Nutrimind Desktop.
2. Entrar com usuário nutricionista.
3. Listar e cadastrar um paciente.
4. Registrar uma consulta para esse paciente.
5. Mostrar o histórico salvo no SQLite.
6. Demonstrar resumo e alertas por IA, caso a chave esteja configurada.
7. Mostrar que o profissional revisa o relatório e que a IA não realiza diagnóstico.
8. Apontar DER, script SQL, arquitetura MVC, DAO e Singleton no repositório.

## Observação Final

Cada integrante possui uma responsabilidade primária, mas a entrega pertence ao grupo. Qualquer ajuste feito em conjunto deve ser registrado por commits claros e revisado antes do envio, para que a documentação, o código e o discurso do pitch apresentem o mesmo MVP.
