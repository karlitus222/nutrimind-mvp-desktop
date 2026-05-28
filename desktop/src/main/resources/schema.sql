-- ============================================================
-- Nutrimind MVP Desktop
-- Banco de dados SQLite
-- Responsável: Pessoa 2 - Banco de dados e DER
-- ============================================================

CREATE TABLE IF NOT EXISTS user (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    name     TEXT    NOT NULL,
    email    TEXT    NOT NULL UNIQUE,
    password TEXT    NOT NULL,
    role     TEXT    NOT NULL CHECK(role IN ('NUTRITIONIST', 'ADMIN')),
    active   INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS nutritionist (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL UNIQUE,
    crn     TEXT    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS patient (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT    NOT NULL,
    birth_date TEXT    NOT NULL,
    cpf        TEXT    NOT NULL UNIQUE,
    email      TEXT,
    phone      TEXT,
    active     INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS consultation (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id       INTEGER NOT NULL,
    nutritionist_id  INTEGER NOT NULL,
    date             TEXT    NOT NULL,
    notes            TEXT,
    status           TEXT    NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (patient_id)      REFERENCES patient(id),
    FOREIGN KEY (nutritionist_id) REFERENCES nutritionist(id)
);

CREATE TABLE IF NOT EXISTS consultation_report (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    consultation_id  INTEGER NOT NULL UNIQUE,
    summary          TEXT,
    alerts           TEXT,
    reviewed         INTEGER NOT NULL DEFAULT 0,
    created_at       TEXT    NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultation(id)
);

-- ============================================================
-- Dados iniciais para demonstração do MVP
-- ============================================================

INSERT OR IGNORE INTO user (name, email, password, role)
VALUES ('Admin', 'admin@nutrimind.com', 'admin123', 'NUTRITIONIST');

INSERT OR IGNORE INTO nutritionist (user_id, crn)
VALUES (1, 'CRN-8/12345');

INSERT OR IGNORE INTO patient (name, birth_date, cpf, email, phone)
VALUES ('Maria Silva', '1990-05-15', '123.456.789-00', 'maria@email.com', '(86) 99999-0001');

INSERT OR IGNORE INTO patient (name, birth_date, cpf, email, phone)
VALUES ('João Souza', '1985-08-22', '987.654.321-00', 'joao@email.com', '(86) 99999-0002');
