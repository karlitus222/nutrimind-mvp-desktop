# Diagrama de Componentes

```mermaid
flowchart LR
    subgraph Browser["Navegador do usuario"]
        UI["React + Vite"]
        Recorder["MediaRecorder\ncaptura de audio"]
        SupabaseClient["Supabase JS Client"]
    end

    subgraph Vercel["Vercel"]
        StaticApp["Frontend publicado\nnutrimind-two.vercel.app"]
    end

    subgraph Supabase["Supabase"]
        Auth["Auth\nlogin e sessao"]
        Database["PostgreSQL\nPK/FK + RLS"]
        EdgeFunction["Edge Function\nanalyze-consultation"]
    end

    subgraph AI["Provedores de IA"]
        Gemini["Gemini API\nprovedor principal"]
        OpenAI["OpenAI API\nfallback configuravel"]
    end

    UI --> SupabaseClient
    UI --> Recorder
    StaticApp --> UI
    SupabaseClient --> Auth
    SupabaseClient --> Database
    SupabaseClient --> EdgeFunction
    Recorder --> EdgeFunction
    EdgeFunction --> Gemini
    EdgeFunction --> OpenAI
    EdgeFunction --> Database
```

## Responsabilidades

- **React + Vite**: interface, login, pacientes, consulta, relatorios, alertas, planos e painel administrativo.
- **MediaRecorder**: grava o audio da consulta no navegador quando houver consentimento.
- **Supabase Auth**: autentica nutricionistas e administradores.
- **PostgreSQL + RLS**: guarda pacientes, consultas, alertas, relatorios, planos e auditoria com controle de acesso.
- **Edge Function**: recebe dados da consulta, audio/transcricao e contexto clinico; chama a IA e devolve JSON estruturado.
- **Gemini/OpenAI**: geram transcricao, resumo, riscos, recomendacoes e sugestao inicial de plano.

O audio bruto nao e persistido pelo fluxo principal. O sistema salva transcricao, resumo, alertas, relatorio e plano para revisao do nutricionista.
