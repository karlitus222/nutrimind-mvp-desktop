# Diagrama de Componentes

```mermaid
flowchart LR
    subgraph Desktop["Entrega principal: Desktop Java"]
        View["Views Swing"]
        Controller["Controllers MVC"]
        Service["Services de dominio"]
        DAO["DAOs"]
        Config["Database Singleton"]
        Audio["AudioRecorderService"]
        Export["Exportacao JSON"]
    end

    subgraph LocalDB["Banco local"]
        SQLite["SQLite\nnutrimind.db"]
    end

    subgraph IA["IA externa"]
        Gemini["Gemini API\nprincipal/free"]
        OpenAI["OpenAI API\nfallback"]
    end

    subgraph Web["Demo mantido"]
        React["React + Vite"]
        Supabase["Supabase\nAuth + PostgreSQL + Edge Function"]
        Vercel["Vercel"]
    end

    View --> Controller
    Controller --> Service
    Service --> DAO
    DAO --> Config
    Config --> SQLite
    Service --> Audio
    Service --> Export
    Service -- HTTPS --> Gemini
    Service -- HTTPS --> OpenAI

    React --> Supabase
    Vercel --> React
    Supabase -- HTTPS --> Gemini
    Supabase -- HTTPS --> OpenAI
```

## Responsabilidades do Desktop

- **Views Swing**: telas de login, dashboard, pacientes, consulta com IA, relatorios, planos e administracao.
- **Controllers MVC**: conectam interface, servicos e persistencia.
- **Services**: regras de autenticacao, fluxo de consulta, gravacao, transcricao, analise por IA, relatorio e plano.
- **DAOs**: CRUD e consultas SQL no SQLite.
- **Database Singleton**: centraliza a conexao JDBC com `nutrimind.db`.
- **Gemini/OpenAI**: geram transcricao, resumo, riscos, recomendacoes e sugestao inicial de plano.

O web app continua no repositorio como demonstrativo, mas a arquitetura cobrada pelo PDF esta implementada no desktop.
