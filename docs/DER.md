# DER Logico

Os diagramas enviados servem como base de estudo, mas nao representam exatamente a versao final do Nutrimind. Eles usam tabelas como `user`, `nutritionist` e `consultation_report`, enquanto a aplicacao publicada usa Supabase Auth e o schema real abaixo.

No projeto final, o login fica em `auth.users`, gerenciado pelo Supabase. A tabela `profiles` guarda os dados do profissional e fica ligada ao usuario autenticado pelo mesmo `id`.

```mermaid
erDiagram
    AUTH_USERS ||--|| PROFILES : autentica
    PROFILES ||--o{ PATIENTS : acompanha
    PROFILES ||--o{ CONSULTATIONS : realiza
    PATIENTS ||--o{ CONSULTATIONS : possui
    CONSULTATIONS ||--o{ ALERTS : gera
    CONSULTATIONS ||--o| REPORTS : gera
    PATIENTS ||--o{ MEAL_PLANS : recebe
    CONSULTATIONS o|--o{ MEAL_PLANS : orienta
    PROFILES o|--o{ MEAL_PLANS : aprova
    PROFILES o|--o{ AUDIT_LOGS : registra

    AUTH_USERS {
        uuid id PK
        text email
    }

    PROFILES {
        uuid id PK,FK
        text full_name
        text role
        text crn
        timestamptz created_at
    }

    PATIENTS {
        uuid id PK
        uuid owner_id FK
        text name
        text cpf
        date birth_date
        text phone
        text email
        text clinical_notes
        text eating_history
        boolean active
        timestamptz created_at
        timestamptz updated_at
    }

    CONSULTATIONS {
        uuid id PK
        uuid patient_id FK
        uuid nutritionist_id FK
        boolean consent_audio
        boolean consent_video
        text clinical_notes
        text manual_transcript
        text transcript
        text visual_notes
        text status
        text ai_summary
        text ai_model
        timestamptz created_at
    }

    ALERTS {
        uuid id PK
        uuid consultation_id FK
        text type
        text severity
        text justification
        text message
        text status
        text decision
        text decision_notes
        timestamptz decided_at
        timestamptz created_at
    }

    REPORTS {
        uuid id PK
        uuid consultation_id FK,UK
        text identification_section
        text clinical_section
        text recommendations_section
        text limitations_section
        timestamptz created_at
    }

    MEAL_PLANS {
        uuid id PK
        uuid patient_id FK
        uuid consultation_id FK
        text objective
        text description
        text status
        uuid approved_by FK
        timestamptz approved_at
        timestamptz created_at
    }

    AUDIT_LOGS {
        bigint id PK
        uuid user_id FK
        text action
        jsonb details
        timestamptz created_at
    }
```

## Ajustes em relacao aos diagramas enviados

- `user` foi substituida por `auth.users` + `profiles`, porque o login real e feito pelo Supabase Auth.
- `nutritionist` nao e uma tabela separada no schema web; o nutricionista e um `profile` com `role = 'NUTRITIONIST'` e campo `crn`.
- `consultation_report` foi normalizada como `reports`.
- O modelo final inclui `alerts`, `meal_plans` e `audit_logs`, que sao necessarios para o fluxo de IA, revisao profissional e rastreabilidade.
- As chaves primarias usam `uuid`, exceto `audit_logs.id`, que usa `bigint` gerado automaticamente.

O script SQL principal esta em `supabase/migrations/20260601180000_nutrimind_schema.sql`.
