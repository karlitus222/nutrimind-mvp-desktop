# DER Logico - Desktop Java + SQLite

Este DER representa a entrega principal do Nutrimind: aplicativo Java 17 + Swing + SQLite.

```mermaid
erDiagram
    USERS ||--o| NUTRITIONISTS : especializa
    USERS ||--o{ PATIENTS : atende
    USERS ||--o{ CONSULTATIONS : realiza
    PATIENTS ||--o{ CONSULTATIONS : possui
    CONSULTATIONS ||--o{ MEDIA_SESSIONS : registra
    CONSULTATIONS ||--o{ RISK_ANALYSES : recebe
    RISK_ANALYSES ||--o{ ALERTS : gera
    CONSULTATIONS ||--o{ ALERTS : possui
    ALERTS ||--o{ ALERT_DECISIONS : recebe
    CONSULTATIONS ||--o{ CONSULTATION_REPORTS : gera
    PATIENTS ||--o{ MEAL_PLANS : recebe
    CONSULTATIONS o|--o{ MEAL_PLANS : orienta
    USERS o|--o{ MEAL_PLANS : aprova
    USERS ||--o{ SUBSCRIPTIONS : possui
    USERS o|--o{ AUDIT_LOGS : registra

    USERS {
        integer id PK
        text name
        text email UK
        text password_hash
        text role
        integer active
        text created_at
    }

    NUTRITIONISTS {
        integer user_id PK,FK
        text crn
        text specialty
    }

    PATIENTS {
        integer id PK
        integer nutritionist_id FK
        text name
        text cpf
        text birth_date
        text phone
        text email
        text clinical_notes
        text eating_history
        integer active
        text created_at
    }

    CONSULTATIONS {
        integer id PK
        integer patient_id FK
        integer nutritionist_id FK
        text started_at
        text ended_at
        text status
        integer consent_audio
        integer consent_video
        text clinical_notes
        text transcript
        text visual_observations
    }

    MEDIA_SESSIONS {
        integer id PK
        integer consultation_id FK
        text type
        text file_path
        text quality
        integer duration_seconds
        text status
        text created_at
    }

    RISK_ANALYSES {
        integer id PK
        integer consultation_id FK
        text provider
        text model
        text raw_json
        text summary
        text created_at
    }

    ALERTS {
        integer id PK
        integer consultation_id FK
        integer analysis_id FK
        text risk_type
        text severity
        text message
        text justification
        text status
        text created_at
    }

    ALERT_DECISIONS {
        integer id PK
        integer alert_id FK
        text action
        text notes
        text decided_at
    }

    CONSULTATION_REPORTS {
        integer id PK
        integer consultation_id FK
        text identification_section
        text clinical_section
        text recommendations_section
        text limitations_section
        text generated_at
    }

    MEAL_PLANS {
        integer id PK
        integer patient_id FK
        integer consultation_id FK
        text objective
        text description
        text status
        text start_date
        text end_date
        integer approved_by FK
        text approved_at
    }

    SUBSCRIPTIONS {
        integer id PK
        integer nutritionist_id FK
        text plan_name
        text status
        text last_payment_status
        text next_due_date
    }

    AUDIT_LOGS {
        integer id PK
        integer user_id FK
        text action
        text details
        text created_at
    }
```

O script SQL de criacao esta em `desktop/sql/schema.sql`.

A versao web usa Supabase/PostgreSQL e foi mantida como demonstrativo publicado, mas o DER acima e o modelo usado na entrega desktop.
