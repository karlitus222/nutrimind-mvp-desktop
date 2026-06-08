PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL,
    active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS nutritionists (
    user_id INTEGER PRIMARY KEY,
    crn TEXT NOT NULL,
    specialty TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS patients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nutritionist_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    cpf TEXT,
    birth_date TEXT,
    phone TEXT,
    email TEXT,
    clinical_notes TEXT,
    eating_history TEXT,
    active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL,
    FOREIGN KEY (nutritionist_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS consultations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id INTEGER NOT NULL,
    nutritionist_id INTEGER NOT NULL,
    started_at TEXT NOT NULL,
    ended_at TEXT,
    status TEXT NOT NULL,
    consent_audio INTEGER NOT NULL DEFAULT 0,
    consent_video INTEGER NOT NULL DEFAULT 0,
    clinical_notes TEXT,
    transcript TEXT,
    visual_observations TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (nutritionist_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS media_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    consultation_id INTEGER NOT NULL,
    type TEXT NOT NULL,
    file_path TEXT NOT NULL,
    quality TEXT,
    duration_seconds INTEGER DEFAULT 0,
    status TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultations(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS risk_analyses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    consultation_id INTEGER NOT NULL,
    provider TEXT NOT NULL,
    model TEXT NOT NULL,
    raw_json TEXT NOT NULL,
    summary TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultations(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS alerts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    consultation_id INTEGER NOT NULL,
    analysis_id INTEGER,
    risk_type TEXT NOT NULL,
    severity TEXT NOT NULL,
    message TEXT NOT NULL,
    justification TEXT NOT NULL,
    status TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultations(id) ON DELETE CASCADE,
    FOREIGN KEY (analysis_id) REFERENCES risk_analyses(id)
);

CREATE TABLE IF NOT EXISTS alert_decisions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alert_id INTEGER NOT NULL,
    action TEXT NOT NULL,
    notes TEXT NOT NULL,
    decided_at TEXT NOT NULL,
    FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS consultation_reports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    consultation_id INTEGER NOT NULL,
    identification_section TEXT NOT NULL,
    clinical_section TEXT NOT NULL,
    recommendations_section TEXT NOT NULL,
    limitations_section TEXT NOT NULL,
    generated_at TEXT NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultations(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS meal_plans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id INTEGER NOT NULL,
    consultation_id INTEGER,
    objective TEXT NOT NULL,
    description TEXT NOT NULL,
    status TEXT NOT NULL,
    start_date TEXT,
    end_date TEXT,
    approved_by INTEGER,
    approved_at TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (consultation_id) REFERENCES consultations(id),
    FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nutritionist_id INTEGER NOT NULL,
    plan_name TEXT NOT NULL,
    status TEXT NOT NULL,
    last_payment_status TEXT NOT NULL,
    next_due_date TEXT NOT NULL,
    FOREIGN KEY (nutritionist_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    action TEXT NOT NULL,
    details TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
