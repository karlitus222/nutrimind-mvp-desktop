# Plano de Desenvolvimento e Divisao de Responsabilidades - Nutrimind

## Objetivo da Entrega

O grupo entregara o **Nutrimind Desktop**, um aplicativo Java 17 + Swing + SQLite com arquitetura MVC, DAOs, Singleton de conexao, CRUDs, consultas relacionadas a pacientes e IA assistiva obrigatoria no fluxo de analise.

A versao web React/Vite publicada na Vercel sera mantida como apoio visual para o pitch e testes pelo celular, mas a entrega principal avaliada pelo requisito tecnico sera o desktop.

## Escopo do Desktop

Entram na entrega:

- login demonstrativo;
- perfis de nutricionista e administrador;
- cadastro, edicao, listagem e inativacao de pacientes;
- cadastro administrativo de nutricionistas;
- consultas vinculadas aos pacientes;
- consentimento de audio/video;
- gravacao de audio e transcricao por IA;
- analise por IA com Gemini principal e OpenAI fallback;
- alertas, decisoes clinicas, relatorio e plano alimentar;
- SQLite com PK/FK;
- documentacao tecnica e testes.

Nao entram na entrega:

- pagamentos reais;
- diagnostico automatico;
- prescricao sem revisao profissional;
- obrigacao de usar o web como sistema principal.

## Divisao por Integrante

### Pessoa 1 - Integracao, autenticacao e README

Responsavel por:

- fluxo de login;
- integracao do aplicativo;
- README final;
- organizacao da entrega no GitHub.

Arquivos principais:

- `desktop/src/main/java/br/com/nutrimind/App.java`
- `desktop/src/main/java/br/com/nutrimind/controller/AppController.java`
- `desktop/src/main/java/br/com/nutrimind/service/AuthService.java`
- `desktop/src/main/java/br/com/nutrimind/view/LoginPanel.java`
- `README.md`

### Pessoa 2 - Banco de dados e DER

Responsavel por:

- modelagem relacional;
- script SQL;
- DER logico;
- dados iniciais de demonstracao.

Arquivos principais:

- `desktop/sql/schema.sql`
- `desktop/src/main/java/br/com/nutrimind/config/Database.java`
- `desktop/src/main/java/br/com/nutrimind/config/DatabaseInitializer.java`
- `docs/DER.md`

### Pessoa 3 - Modelos e POO

Responsavel por:

- classes de dominio;
- encapsulamento;
- heranca;
- enums;
- colecoes/generics quando aplicavel.

Arquivos principais:

- `desktop/src/main/java/br/com/nutrimind/model/User.java`
- `desktop/src/main/java/br/com/nutrimind/model/Nutritionist.java`
- `desktop/src/main/java/br/com/nutrimind/model/SystemUser.java`
- `desktop/src/main/java/br/com/nutrimind/model/Patient.java`
- `desktop/src/main/java/br/com/nutrimind/model/Consultation.java`
- `desktop/src/main/java/br/com/nutrimind/model/ConsultationReport.java`

### Pessoa 4 - DAO, Singleton e CRUD

Responsavel por:

- interface generica de CRUD;
- DAOs de pacientes, consultas, usuarios e relatorios;
- persistencia SQLite;
- tratamento de erros de banco.

Arquivos principais:

- `desktop/src/main/java/br/com/nutrimind/dao/CrudDao.java`
- `desktop/src/main/java/br/com/nutrimind/dao/PatientDao.java`
- `desktop/src/main/java/br/com/nutrimind/dao/ConsultationDao.java`
- `desktop/src/main/java/br/com/nutrimind/dao/UserDao.java`
- `desktop/src/main/java/br/com/nutrimind/dao/ReportDao.java`

### Pessoa 5 - Interface Swing

Responsavel por:

- telas do desktop;
- navegacao;
- validacoes visuais;
- usabilidade da demonstracao.

Arquivos principais:

- `desktop/src/main/java/br/com/nutrimind/view/MainFrame.java`
- `desktop/src/main/java/br/com/nutrimind/view/DashboardPanel.java`
- `desktop/src/main/java/br/com/nutrimind/view/PatientsPanel.java`
- `desktop/src/main/java/br/com/nutrimind/view/ConsultationPanel.java`
- `desktop/src/main/java/br/com/nutrimind/view/ReportsPanel.java`
- `desktop/src/main/java/br/com/nutrimind/view/MealPlansPanel.java`

### Pessoa 6 - Controllers e fluxo da consulta

Responsavel por:

- controllers MVC;
- criacao de consulta vinculada ao paciente;
- geracao de relatorio;
- integracao entre tela, servico e DAO.

Arquivos principais:

- `desktop/src/main/java/br/com/nutrimind/controller/PatientController.java`
- `desktop/src/main/java/br/com/nutrimind/controller/ConsultationController.java`
- `desktop/src/main/java/br/com/nutrimind/service/ConsultationWorkflowService.java`
- `desktop/src/main/java/br/com/nutrimind/exception/AppException.java`

### Pessoa 7 - IA, testes e qualidade

Responsavel por:

- Gemini no desktop;
- OpenAI fallback;
- prompt seguro contra invencao de fatos;
- testes;
- checklist e documentacao de IA.

Arquivos principais:

- `desktop/src/main/java/br/com/nutrimind/service/GeminiClient.java`
- `desktop/src/main/java/br/com/nutrimind/service/GeminiRiskAnalysisService.java`
- `desktop/src/main/java/br/com/nutrimind/service/GeminiTranscriptionService.java`
- `desktop/src/main/java/br/com/nutrimind/service/OpenAiClient.java`
- `desktop/src/main/java/br/com/nutrimind/service/OpenAiRiskAnalysisService.java`
- `desktop/src/main/java/br/com/nutrimind/service/OpenAiTranscriptionService.java`
- `desktop/src/test/java/br/com/nutrimind/SmokeTest.java`
- `docs/ia-openai.md`
- `docs/checklist-qualidade.md`

## Ordem Recomendada

1. Banco, DER e dados de demonstracao.
2. Modelos de dominio e regras basicas.
3. DAOs e CRUDs.
4. Controllers e fluxo de consulta.
5. Telas Swing.
6. IA com Gemini/OpenAI.
7. Testes, documentacao e revisao final.

## Regras de GitHub

1. Cada integrante trabalha em sua propria branch.
2. Cada parte deve ter commits do proprio integrante.
3. Ninguem trabalha direto na `main`.
4. Cada branch deve abrir Pull Request.
5. Antes da entrega, conferir se os integrantes aparecem no historico.
