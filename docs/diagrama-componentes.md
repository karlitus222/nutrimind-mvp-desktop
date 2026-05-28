# Diagrama de Componentes

```mermaid
flowchart LR
    subgraph Desktop["Desktop Java Swing"]
        View["Views Swing"]
        Controller["Controllers MVC"]
        Service["Services de domínio"]
        DAO["DAOs"]
        Config["Database Singleton"]
        Audio["Captura de áudio"]
        Report["Relatórios e planos"]
    end

    subgraph IA["OpenAI"]
        STT["Speech-to-text"]
        Responses["Responses API"]
    end

    subgraph DB["SQLite local"]
        Tables["Tabelas PK/FK"]
    end

    View --> Controller
    Controller --> Service
    Service --> DAO
    DAO --> Config
    Config --> Tables
    Service --> Audio
    Service --> STT
    Service --> Responses
    Service --> Report
```

Responsabilidades:

- Views Swing: interação desktop, formulários e tabelas.
- Controllers: mediação entre tela, domínio e persistência.
- Services: autenticação, gravação, transcrição, análise por IA, relatório e regras do fluxo.
- DAOs: CRUD e consultas SQLite.
