# Nutrimind Desktop Completion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restaurar um desktop Java compilável, reaproveitar contribuições compatíveis e completar o fluxo demonstrável de pacientes, consultas com IA, alertas, relatórios, planos e administração.

**Architecture:** Manter Java 17 + Swing + SQLite com MVC, DAO e Singleton. Restaurar o schema completo usado pelos DAOs existentes, adicionar controladores pequenos para permissões e planos alimentares e validar o fluxo principal com IA falsa em testes locais, mantendo OpenAI obrigatória apenas na execução real.

**Tech Stack:** Java 17, Swing, SQLite JDBC, PowerShell, OpenAI Responses API e Audio Transcriptions API.

**Execution status:** concluído em 01/06/2026 na branch `codex/completar-desktop`.

---

### Task 1: Fazer os scripts falharem corretamente

**Files:**
- Create: `scripts/fixtures/failing-bin/javac.cmd`
- Create: `scripts/test-build-scripts.ps1`
- Modify: `scripts/compile-desktop.ps1`
- Modify: `scripts/test-desktop.ps1`
- Modify: `scripts/run-desktop.ps1`

- [ ] Criar um teste que injeta um `javac.cmd` com saída `7`.
- [ ] Executar `powershell -ExecutionPolicy Bypass -File scripts\test-build-scripts.ps1` e confirmar falha porque o script atual imprime sucesso após erro nativo.
- [ ] Adicionar verificação de `$LASTEXITCODE` após `javac` e `java`.
- [ ] Limpar diretórios de saída antes da compilação para impedir execução de classes antigas.
- [ ] Executar novamente o teste de script e confirmar `BuildScriptTest OK`.

### Task 2: Restaurar persistência compatível e remover artefatos indevidos

**Files:**
- Modify: `desktop/src/main/java/br/com/nutrimind/config/Database.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/config/DatabaseInitializer.java`
- Modify: `desktop/sql/schema.sql`
- Modify: `docs/DER.md`
- Delete: `desktop/src/main/resources/schema.sql`
- Delete: `nutrimind_app.zip`
- Delete: `projetoapp`

- [ ] Restaurar o Singleton `Database.getInstance().getConnection()` usado pelos DAOs.
- [ ] Restaurar o schema completo com tabelas plurais e PK/FK para consultas, mídia, análises, alertas, decisões, relatórios, planos, assinaturas e auditoria.
- [ ] Remover duplicação de schema e arquivos enviados manualmente sem função no código.
- [ ] Executar `powershell -ExecutionPolicy Bypass -File scripts\test-desktop.ps1` e confirmar compilação limpa.

### Task 3: Reaproveitar modelos compatíveis da Pessoa 3

**Files:**
- Modify: `desktop/src/main/java/br/com/nutrimind/model/Consultation.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/ConsultationReport.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/ConsultationStatus.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/Nutritionist.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/Patient.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/Role.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/Severity.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/SystemUser.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/User.java`

- [ ] Incorporar construtores, setters, labels e métodos de apresentação compatíveis com os DAOs atuais.
- [ ] Não incorporar a interface `Auditable`, pois ela não é usada por nenhuma entidade.
- [ ] Executar compilação e smoke test.

### Task 4: Completar permissões por perfil

**Files:**
- Create: `desktop/src/main/java/br/com/nutrimind/service/RoleAccessPolicy.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/view/MainFrame.java`
- Modify: `desktop/src/test/java/br/com/nutrimind/SmokeTest.java`

- [ ] Escrever teste para garantir que nutricionista acesse área clínica e admin acesse administração.
- [ ] Executar o teste e confirmar falha por classe ausente.
- [ ] Criar política simples baseada em `Role`.
- [ ] Aplicar política nas abas visíveis após login.
- [ ] Executar novamente e confirmar sucesso.

### Task 5: Completar aprovação de plano alimentar e fluxo testável

**Files:**
- Create: `desktop/src/main/java/br/com/nutrimind/service/TranscriptionService.java`
- Create: `desktop/src/main/java/br/com/nutrimind/controller/MealPlanController.java`
- Create: `desktop/src/main/java/br/com/nutrimind/view/MealPlansPanel.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/service/OpenAiTranscriptionService.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/service/ConsultationWorkflowService.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/controller/AppController.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/dao/MealPlanDao.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/model/MealPlan.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/view/MainFrame.java`
- Modify: `desktop/src/test/java/br/com/nutrimind/SmokeTest.java`

- [ ] Escrever teste de consulta com IA falsa: consulta, alerta, relatório e plano em revisão.
- [ ] Executar o teste e confirmar falha pela dependência concreta de transcrição e ID do plano não preenchido.
- [ ] Extrair interface de transcrição e persistir ID gerado do plano.
- [ ] Criar controlador e painel Swing para listar e aprovar planos.
- [ ] Executar teste e confirmar plano aprovado com usuário responsável.

### Task 6: Completar administração e configuração OpenAI

**Files:**
- Modify: `desktop/src/main/java/br/com/nutrimind/controller/AdminController.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/view/AdminPanel.java`
- Modify: `desktop/src/main/java/br/com/nutrimind/config/AppConfig.java`
- Modify: `README.md`
- Modify: `docs/ia-openai.md`
- Modify: `docs/checklist-qualidade.md`
- Modify: `desktop/src/test/java/br/com/nutrimind/SmokeTest.java`

- [ ] Adicionar desativação administrativa de usuário na interface.
- [ ] Trocar o modelo padrão não documentado `gpt-5.4-mini` pelo alias oficial `gpt-5-mini`.
- [ ] Documentar o fluxo final e marcar checklist apenas com itens verificados.
- [ ] Executar teste completo.

### Task 7: Verificação final

**Files:**
- Verify: all changed files

- [ ] Executar `git diff --check`.
- [ ] Executar `powershell -ExecutionPolicy Bypass -File scripts\test-build-scripts.ps1`.
- [ ] Executar `powershell -ExecutionPolicy Bypass -File scripts\test-desktop.ps1`.
- [ ] Executar `git status --short`.
- [ ] Revisar o diff e registrar itens reaproveitados, descartados e corrigidos.
