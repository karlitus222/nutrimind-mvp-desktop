# Diagrama de Implantação

```mermaid
flowchart TB
    User["Nutricionista/Admin"]

    subgraph DesktopMachine["Computador do consultório"]
        JavaApp["Nutrimind Desktop\nJava 17 + Swing"]
        SQLite["nutrimind.db\nSQLite"]
        Media["Arquivos .wav e vídeo vinculado"]
        Reports["Relatórios e dados locais"]
    end

    subgraph OpenAICloud["OpenAI Cloud"]
        Transcribe["Audio Transcriptions API"]
        Analyze["Responses API\nJSON estruturado"]
    end

    User --> JavaApp
    JavaApp --> SQLite
    JavaApp --> Media
    JavaApp -- HTTPS --> Transcribe
    JavaApp -- HTTPS --> Analyze
    JavaApp --> Reports
```

O desktop opera localmente para cadastros e histórico. A análise principal exige internet e `OPENAI_API_KEY`, pois a proposta do sistema é ser uma IA de apoio ao atendimento nutricional.
