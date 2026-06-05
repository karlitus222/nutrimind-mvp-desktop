# Diagrama de Implantacao

```mermaid
flowchart TB
    User["Nutricionista/Admin"]

    subgraph DesktopMachine["Computador do consultorio"]
        JavaApp["Nutrimind Desktop\nJava 17 + Swing"]
        DB["nutrimind.db\nSQLite"]
        Media["Arquivos locais\n.wav e video vinculado"]
        Exports["Exportacoes JSON"]
    end

    subgraph AICloud["IA externa"]
        Gemini["Gemini API"]
        OpenAI["OpenAI API fallback"]
    end

    subgraph WebDemo["Demo web mantido"]
        Browser["Navegador"]
        Vercel["Vercel\nnutrimind-two.vercel.app"]
        Supabase["Supabase\nAuth + PostgreSQL + Edge Function"]
    end

    User --> JavaApp
    JavaApp --> DB
    JavaApp --> Media
    JavaApp --> Exports
    JavaApp -- HTTPS com GEMINI_API_KEY --> Gemini
    JavaApp -- HTTPS com OPENAI_API_KEY --> OpenAI

    User -. apoio visual .-> Browser
    Browser --> Vercel
    Browser --> Supabase
    Supabase -- HTTPS --> Gemini
    Supabase -- HTTPS --> OpenAI
```

## Fluxo implantado do Desktop

1. O usuario abre o Nutrimind Desktop no computador.
2. O sistema inicializa o SQLite local e carrega os dados de demonstracao.
3. O nutricionista registra pacientes e consultas.
4. Quando houver consentimento, o audio gravado em `.wav` pode ser enviado para transcricao por IA.
5. A analise por IA usa Gemini quando `GEMINI_API_KEY` existir; caso contrario, usa OpenAI se `OPENAI_API_KEY` existir.
6. O resultado estruturado e salvo no SQLite como analise, alertas, relatorio e plano alimentar em revisao.
7. O nutricionista toma a decisao final, registra conduta e aprova o plano.

O web app permanece publicado para apoio de apresentacao e teste visual pelo celular, mas nao altera a entrega principal Java.
