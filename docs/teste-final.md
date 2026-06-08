# Teste Final - Nutrimind

Data do teste: 2026-06-05

## Ambiente Principal

- Aplicacao: Nutrimind Desktop
- Stack: Java 17 + Swing + SQLite
- Banco: `desktop/data/nutrimind.db`
- Script SQL: `desktop/sql/schema.sql`
- Script de teste: `scripts/test-desktop.ps1`

## Credenciais do Desktop

- Nutricionista: `nutri@nutrimind.com` / `123456`
- Administrador: `admin@nutrimind.com` / `admin123`

## Validacoes Executadas no Desktop

- Compilacao do projeto Java.
- Inicializacao do SQLite.
- Login de nutricionista.
- Seed com pacientes de demonstracao.
- Heuristicas locais auxiliares.
- Bloqueio da IA quando nenhuma chave esta configurada.
- Politica de acesso por perfil.
- Fluxo de consulta com IA simulada no teste.
- Persistencia de consulta.
- Persistencia de analise.
- Persistencia de alertas.
- Registro de decisao frente a alerta.
- Persistencia de relatorio.
- Criacao e aprovacao de plano alimentar.
- Desativacao de usuario pelo administrador.

## Resultado do Desktop

Comando executado:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\test-desktop.ps1
```

Resultado observado:

```text
BuildScriptTest OK
Desktop compilado
SmokeTest OK
Testes desktop concluidos.
```

## IA no Desktop

O desktop seleciona o provedor automaticamente:

1. `GEMINI_API_KEY`: Gemini principal.
2. `OPENAI_API_KEY`: OpenAI fallback.
3. Sem chave: analise/transcricao inteligente ficam bloqueadas com mensagem de configuracao.

O prompt foi ajustado para nao inventar fatos, habitos ou sintomas. Falas vagas, como "tenho problema com comida", devem gerar necessidade de aprofundamento em vez de conclusoes especificas.

## Ambiente Web Mantido

- Producao: `https://nutrimind-two.vercel.app`
- Supabase project ref: `hcobzfxkkvezlopktlzf`
- Edge Function: `analyze-consultation`
- Provedor testado anteriormente: Gemini
- Modelo de apresentacao: `gemini-2.5-flash-lite`
- Login demo: `demo@nutrimind.app` / `Nutrimind@2026`

O web app continua como apoio de pitch e teste pelo celular, mas a entrega tecnica principal e o desktop Java.

## Observacao de Seguranca

Usar somente dados ficticios na apresentacao. Chaves de IA devem ficar em variaveis de ambiente/segredos e nunca devem ser versionadas.
