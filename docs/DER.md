# DER — Nutrimind Desktop

> Documento de Modelagem Relacional  
> Responsável: Pessoa 2 - Banco de dados e DER  
> MVP: Java 17 + Swing + SQLite

---

## Diagrama de Relacionamentos

```
┌──────────────┐        ┌─────────────────┐
│     user     │1      1│   nutritionist  │
│──────────────│────────│─────────────────│
│ id (PK)      │        │ id (PK)         │
│ name         │        │ user_id (FK)    │
│ email        │        │ crn             │
│ password     │        └────────┬────────┘
│ role         │                 │ 1
│ active       │                 │
└──────────────┘                 │ N
                     ┌───────────▼──────────┐
┌──────────────┐N   1│     consultation     │
│   patient    │─────│──────────────────────│
│──────────────│     │ id (PK)              │
│ id (PK)      │     │ patient_id (FK)      │
│ name         │     │ nutritionist_id (FK) │
│ birth_date   │     │ date                 │
│ cpf          │     │ notes                │
│ email        │     │ status               │
│ phone        │     └───────────┬──────────┘
│ active       │                 │ 1
└──────────────┘                 │
                                 │ 1
                    ┌────────────▼──────────────┐
                    │    consultation_report     │
                    │────────────────────────────│
                    │ id (PK)                    │
                    │ consultation_id (FK)       │
                    │ summary                    │
                    │ alerts                     │
                    │ reviewed                   │
                    │ created_at                 │
                    └────────────────────────────┘
```

---

## Descrição das Entidades

### user
Representa os usuários do sistema (nutricionistas e administradores).

| Campo    | Tipo    | Restrição                            |
|----------|---------|--------------------------------------|
| id       | INTEGER | PK, AUTOINCREMENT                    |
| name     | TEXT    | NOT NULL                             |
| email    | TEXT    | NOT NULL, UNIQUE                     |
| password | TEXT    | NOT NULL                             |
| role     | TEXT    | NOT NULL, CHECK(NUTRITIONIST\|ADMIN) |
| active   | INTEGER | NOT NULL, DEFAULT 1                  |

---

### nutritionist
Especialização de `user`, armazena o CRN do nutricionista.

| Campo   | Tipo    | Restrição               |
|---------|---------|-------------------------|
| id      | INTEGER | PK, AUTOINCREMENT       |
| user_id | INTEGER | FK → user(id), UNIQUE   |
| crn     | TEXT    | NOT NULL                |

**Relacionamento:** `nutritionist.user_id` → `user.id` (1:1)

---

### patient
Representa os pacientes cadastrados no sistema.

| Campo      | Tipo    | Restrição         |
|------------|---------|-------------------|
| id         | INTEGER | PK, AUTOINCREMENT |
| name       | TEXT    | NOT NULL          |
| birth_date | TEXT    | NOT NULL          |
| cpf        | TEXT    | NOT NULL, UNIQUE  |
| email      | TEXT    |                   |
| phone      | TEXT    |                   |
| active     | INTEGER | NOT NULL, DEFAULT 1 |

---

### consultation
Registra as consultas nutricionais vinculando paciente e nutricionista.

| Campo           | Tipo    | Restrição                    |
|-----------------|---------|------------------------------|
| id              | INTEGER | PK, AUTOINCREMENT            |
| patient_id      | INTEGER | FK → patient(id), NOT NULL   |
| nutritionist_id | INTEGER | FK → nutritionist(id), NOT NULL |
| date            | TEXT    | NOT NULL                     |
| notes           | TEXT    |                              |
| status          | TEXT    | NOT NULL, DEFAULT 'PENDING'  |

**Relacionamentos:**
- `consultation.patient_id` → `patient.id` (N:1)
- `consultation.nutritionist_id` → `nutritionist.id` (N:1)

---

### consultation_report
Relatório gerado para cada consulta, podendo incluir resumo e alertas da IA assistiva.

| Campo           | Tipo    | Restrição                          |
|-----------------|---------|------------------------------------|
| id              | INTEGER | PK, AUTOINCREMENT                  |
| consultation_id | INTEGER | FK → consultation(id), NOT NULL, UNIQUE |
| summary         | TEXT    |                                    |
| alerts          | TEXT    |                                    |
| reviewed        | INTEGER | NOT NULL, DEFAULT 0                |
| created_at      | TEXT    | NOT NULL                           |

**Relacionamento:** `consultation_report.consultation_id` → `consultation.id` (1:1)

---

## Resumo dos Relacionamentos

| Entidade A       | Cardinalidade | Entidade B            |
|------------------|---------------|-----------------------|
| user             | 1 : 1         | nutritionist          |
| nutritionist     | 1 : N         | consultation          |
| patient          | 1 : N         | consultation          |
| consultation     | 1 : 1         | consultation_report   |

---

## Script SQL

O script completo de criação está em:  
`desktop/sql/schema.sql`  
`desktop/src/main/resources/schema.sql` *(cópia para o classpath Java)*
