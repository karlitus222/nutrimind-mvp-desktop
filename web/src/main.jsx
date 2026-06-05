import React, { useEffect, useMemo, useRef, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  AlertTriangle,
  Bot,
  Brain,
  CalendarCheck,
  CheckCircle2,
  ClipboardList,
  Database,
  FileText,
  HeartPulse,
  LayoutDashboard,
  LoaderCircle,
  Lock,
  LogOut,
  Mic,
  Plus,
  Save,
  Settings2,
  ShieldCheck,
  Stethoscope,
  Trash2,
  UserRound,
  UsersRound
} from 'lucide-react';
import './styles.css';
import { sampleData } from './sampleData.js';
import { isSupabaseConfigured } from './lib/supabase.js';
import {
  analyzeConsultation,
  approveMealPlan,
  decideAlert,
  deactivatePatient,
  getSession,
  loadWorkspace,
  observeSession,
  savePatient,
  signIn,
  signOut,
  signUp
} from './lib/nutrimindApi.js';

const navItems = [
  ['dashboard', 'Dashboard', LayoutDashboard],
  ['patients', 'Pacientes', UsersRound],
  ['consultation', 'Consulta com IA', Brain],
  ['reports', 'Relatorios', FileText],
  ['plans', 'Planos', ClipboardList],
  ['admin', 'Administracao', Settings2]
];

const demoUsers = [
  {
    name: 'Dra. Juliana Alves',
    email: 'nutri@nutrimind.com',
    password: '123456',
    role: 'Nutricionista'
  },
  {
    name: 'Admin Nutrimind',
    email: 'admin@nutrimind.com',
    password: 'admin123',
    role: 'Administrador'
  }
];

function App() {
  const previewParams = new URLSearchParams(window.location.search);
  const previewEnabled = import.meta.env.DEV && previewParams.get('demo') === '1';
  const [view, setView] = useState(import.meta.env.DEV && previewParams.get('view') || 'dashboard');
  const [booting, setBooting] = useState(isSupabaseConfigured);
  const [currentUser, setCurrentUser] = useState(previewEnabled ? demoUsers[0] : null);
  const [data, setData] = useState(normalizeData(sampleData));
  const [selectedPatientId, setSelectedPatientId] = useState(sampleData.patients[0]?.id);
  const [notice, setNotice] = useState('');

  const selectedPatient = useMemo(
    () => data.patients.find((patient) => String(patient.id) === String(selectedPatientId)) || data.patients[0],
    [data, selectedPatientId]
  );

  useEffect(() => {
    if (!isSupabaseConfigured) {
      setBooting(false);
      return undefined;
    }

    let active = true;
    getSession()
      .then((session) => {
        if (!active) return;
        if (session) return enterSession(session);
        setBooting(false);
      })
      .catch((error) => {
        if (!active) return;
        setNotice(error.message);
        setBooting(false);
      });

    const subscription = observeSession((session) => {
      if (!active || !session) return;
      enterSession(session);
    });

    return () => {
      active = false;
      subscription.unsubscribe();
    };
  }, []);

  async function enterSession(session) {
    setCurrentUser({
      name: session.user.user_metadata?.full_name || session.user.email,
      email: session.user.email,
      role: 'Nutricionista'
    });
    await refreshData();
    setBooting(false);
    setView('dashboard');
  }

  async function refreshData() {
    if (!isSupabaseConfigured) return;
    const workspace = normalizeData(await loadWorkspace());
    setData(workspace);
    setSelectedPatientId((current) => workspace.patients.some((patient) => String(patient.id) === String(current))
      ? current
      : workspace.patients[0]?.id);
  }

  async function handleLogin(credentials) {
    setBooting(true);
    setNotice('');
    try {
      if (isSupabaseConfigured) {
        await signIn(credentials.email, credentials.password);
        return;
      }
      const user = demoUsers.find(
        (candidate) => candidate.email === credentials.email && candidate.password === credentials.password
      );
      if (!user) throw new Error('Credenciais invalidas para o modo demonstrativo.');
      setCurrentUser(user);
      setView('dashboard');
    } finally {
      setBooting(false);
    }
  }

  async function handleSignup(form) {
    if (!isSupabaseConfigured) {
      setCurrentUser({ name: form.name, email: form.email, role: 'Nutricionista' });
      setView('dashboard');
      return 'Conta demonstrativa criada apenas neste navegador.';
    }
    const result = await signUp(form);
    if (!result.session) {
      return 'Cadastro criado. Confirme o e-mail antes de entrar.';
    }
    return 'Cadastro criado.';
  }

  async function handleLogout() {
    if (isSupabaseConfigured) await signOut();
    setCurrentUser(null);
    setView('dashboard');
  }

  async function handleSavePatient(patient) {
    if (isSupabaseConfigured) {
      await savePatient(patient);
      await refreshData();
      return;
    }
    const localPatient = {
      ...patient,
      id: patient.id || Date.now(),
      consultations: patient.consultations || [],
      plans: patient.plans || []
    };
    setData((current) => ({
      ...current,
      patients: current.patients.some((item) => item.id === localPatient.id)
        ? current.patients.map((item) => (item.id === localPatient.id ? localPatient : item))
        : [localPatient, ...current.patients]
    }));
    setSelectedPatientId(localPatient.id);
  }

  async function handleDeactivatePatient(patientId) {
    if (isSupabaseConfigured) {
      await deactivatePatient(patientId);
      await refreshData();
      return;
    }
    setData((current) => ({
      ...current,
      patients: current.patients.filter((patient) => patient.id !== patientId)
    }));
  }

  async function handleAnalysis(input) {
    if (isSupabaseConfigured) {
      const result = await analyzeConsultation(input);
      await refreshData();
      return result;
    }
    const result = buildLocalAnalysis(input);
    setData((current) => appendLocalAnalysis(current, input.patient.id, result));
    return result;
  }

  async function handleApprovePlan(planId) {
    if (isSupabaseConfigured) {
      await approveMealPlan(planId);
      await refreshData();
      return;
    }
    setData((current) => ({
      ...current,
      patients: current.patients.map((patient) => ({
        ...patient,
        plans: patient.plans.map((plan) => (plan.id === planId ? { ...plan, status: 'APPROVED' } : plan))
      }))
    }));
  }

  async function handleDecideAlert(alertId) {
    if (isSupabaseConfigured) {
      await decideAlert(alertId, 'MONITOR', 'Acompanhar na proxima consulta.');
      await refreshData();
      return;
    }
    setData((current) => ({
      ...current,
      patients: current.patients.map((patient) => ({
        ...patient,
        consultations: patient.consultations.map((consultation) => ({
          ...consultation,
          alerts: (consultation.alerts || []).map((alert) =>
            alert.id === alertId ? { ...alert, status: 'DECIDED' } : alert
          )
        }))
      }))
    }));
  }

  if (booting) return <FullScreenLoader />;
  if (!currentUser) return <LoginScreen onLogin={handleLogin} onSignup={handleSignup} notice={notice} />;

  return (
    <div className="app-shell">
      <Sidebar view={view} setView={setView} currentUser={currentUser} onLogout={handleLogout} />
      <main>
        {!isSupabaseConfigured && (
          <StatusBanner>
            Modo demonstrativo local. Configure o Supabase para persistir dados e liberar chamadas reais da IA.
          </StatusBanner>
        )}
        {view === 'dashboard' && (
          <Dashboard
            data={data}
            selectedPatient={selectedPatient}
            currentUser={currentUser}
            setView={setView}
            onDecideAlert={handleDecideAlert}
          />
        )}
        {view === 'patients' && (
          <Patients
            patients={data.patients}
            selectedPatientId={selectedPatientId}
            setSelectedPatientId={setSelectedPatientId}
            onSave={handleSavePatient}
            onDeactivate={handleDeactivatePatient}
          />
        )}
        {view === 'consultation' && (
          <Consultation
            patients={data.patients}
            selectedPatientId={selectedPatientId}
            setSelectedPatientId={setSelectedPatientId}
            onAnalyze={handleAnalysis}
          />
        )}
        {view === 'reports' && (
          <Reports patients={data.patients} selectedPatientId={selectedPatientId} setSelectedPatientId={setSelectedPatientId} />
        )}
        {view === 'plans' && <Plans data={data} onApprove={handleApprovePlan} />}
        {view === 'admin' && <Admin data={data} />}
      </main>
    </div>
  );
}

function Sidebar({ view, setView, currentUser, onLogout }) {
  return (
    <aside className="sidebar">
      <button className="brand" onClick={() => setView('dashboard')}>
        <span className="brand-mark">N</span>
        <span>
          <strong>Nutrimind</strong>
          <small>IA nutricional</small>
        </span>
      </button>
      <nav aria-label="Navegacao principal">
        {navItems.map(([id, label, Icon]) => (
          <button key={id} className={view === id ? 'active' : ''} onClick={() => setView(id)}>
            <Icon size={18} />
            <span>{label}</span>
          </button>
        ))}
      </nav>
      <div className="sidebar-card">
        <ShieldCheck size={18} />
        <strong>Apoio a decisao</strong>
        <span>A IA sugere. O nutricionista revisa e aprova.</span>
      </div>
      <div className="user-box">
        <UserRound size={20} />
        <span>
          <strong>{currentUser.name}</strong>
          <small>{currentUser.role}</small>
        </span>
      </div>
      <button className="logout-button" onClick={onLogout}>
        <LogOut size={17} />
        Sair
      </button>
    </aside>
  );
}

function LoginScreen({ onLogin, onSignup, notice }) {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState(() => isSupabaseConfigured
    ? { name: '', crn: '', email: '', password: '' }
    : {
        name: 'Dra. Juliana Alves',
        crn: 'CRN12345',
        email: 'nutri@nutrimind.com',
        password: '123456'
      });
  const [message, setMessage] = useState(notice);
  const [submitting, setSubmitting] = useState(false);

  async function submit(event) {
    event.preventDefault();
    setSubmitting(true);
    setMessage('');
    try {
      if (mode === 'login') {
        await onLogin(form);
      } else {
        setMessage(await onSignup(form));
        if (isSupabaseConfigured) setMode('login');
      }
    } catch (error) {
      setMessage(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="auth-screen">
      <form className="auth-card wide" onSubmit={submit}>
        <div className="auth-head">
          <div className="auth-icon"><Brain size={28} /></div>
          <span className="secure-note">
            <ShieldCheck size={15} />
            {isSupabaseConfigured ? 'Supabase conectado' : 'Demo local'}
          </span>
        </div>
        <h1>{mode === 'login' ? 'Entrar no Nutrimind' : 'Criar conta profissional'}</h1>
        <p>IA aplicada a nutricao comportamental com revisao obrigatoria do nutricionista.</p>
        {mode === 'signup' && (
          <div className="form-grid">
            <Field label="Nome profissional" value={form.name} onChange={(name) => setForm({ ...form, name })} />
            <Field label="CRN" value={form.crn} onChange={(crn) => setForm({ ...form, crn })} />
          </div>
        )}
        <Field label="E-mail" type="email" value={form.email} onChange={(email) => setForm({ ...form, email })} />
        <Field label="Senha" type="password" value={form.password} onChange={(password) => setForm({ ...form, password })} />
        {message && <div className="form-error">{message}</div>}
        <button className="primary stretch" type="submit" disabled={submitting}>
          {submitting ? <LoaderCircle className="spin" size={18} /> : <Lock size={18} />}
          {mode === 'login' ? 'Entrar' : 'Criar conta'}
        </button>
        <button className="text-button" type="button" onClick={() => setMode(mode === 'login' ? 'signup' : 'login')}>
          {mode === 'login' ? 'Criar conta profissional' : 'Ja tenho conta'}
        </button>
        {!isSupabaseConfigured && (
          <div className="demo-credentials">
            <strong>Credenciais demo</strong>
            <span>nutri@nutrimind.com / 123456</span>
            <span>admin@nutrimind.com / admin123</span>
          </div>
        )}
      </form>
    </section>
  );
}

function Dashboard({ data, selectedPatient, currentUser, setView, onDecideAlert }) {
  const alerts = allAlerts(data);
  const latest = latestConsultation(selectedPatient);
  return (
    <section className="dashboard">
      <header className="topbar">
        <div>
          <h1>Painel clinico</h1>
          <p>Bem-vinda, {currentUser.name}. Acompanhe atendimentos e decisoes pendentes.</p>
        </div>
        <span className={`ai-online ${isSupabaseConfigured ? '' : 'offline'}`}>
          <Bot size={18} />
          {isSupabaseConfigured ? 'Fluxo de IA via Supabase' : 'IA real aguardando Supabase'}
        </span>
      </header>
      <div className="metric-row">
        <Metric icon={UsersRound} label="Pacientes ativos" value={data.patients.length} tone="teal" />
        <Metric icon={CalendarCheck} label="Consultas analisadas" value={totalConsultations(data)} tone="green" />
        <Metric icon={AlertTriangle} label="Alertas abertos" value={alerts.filter((item) => item.status !== 'DECIDED').length} tone="coral" />
        <Metric icon={ClipboardList} label="Planos em revisao" value={countPlans(data, 'IN_REVIEW')} tone="amber" />
      </div>
      <div className="dashboard-grid">
        <div className="panel table-panel">
          <div className="panel-head">
            <div>
              <h2>Pacientes recentes</h2>
              <p>Historico e risco mais recente.</p>
            </div>
            <button onClick={() => setView('patients')}>Abrir lista</button>
          </div>
          <PatientTable patients={data.patients.slice(0, 5)} />
        </div>
        <div className="panel focus">
          <div className="panel-head stacked">
            <div>
              <h2>Analise em destaque</h2>
              <p>{selectedPatient?.name || 'Nenhum paciente cadastrado'}</p>
            </div>
            <span className="chip neutral">Revisao humana</span>
          </div>
          <p className="summary-text">{latest?.summary || 'Cadastre um paciente e registre a primeira consulta.'}</p>
          <AlertList alerts={latest?.alerts || []} onDecide={onDecideAlert} />
          <button className="primary" onClick={() => setView('consultation')}>
            <Brain size={17} />
            Nova consulta com IA
          </button>
        </div>
      </div>
      <div className="clinical-feed">
        <WorkflowStep icon={Mic} title="Captura" text="Audio ou transcricao da consulta" />
        <WorkflowStep icon={Database} title="Contexto" text="Historico e observacoes clinicas" />
        <WorkflowStep icon={Brain} title="IA" text="Riscos e justificativas em JSON" />
        <WorkflowStep icon={Stethoscope} title="Decisao" text="Revisao obrigatoria do profissional" />
      </div>
    </section>
  );
}

function Patients({ patients, selectedPatientId, setSelectedPatientId, onSave, onDeactivate }) {
  const emptyForm = { name: '', cpf: '', birthDate: '', phone: '', email: '', clinicalNotes: '', eatingHistory: '' };
  const [form, setForm] = useState(emptyForm);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState('');

  function selectPatient(patient) {
    setSelectedPatientId(patient.id);
    setForm(patient);
    setMessage('');
  }

  async function submit(event) {
    event.preventDefault();
    setBusy(true);
    setMessage('');
    try {
      await onSave(form);
      setForm(emptyForm);
      setMessage('Paciente salvo.');
    } catch (error) {
      setMessage(error.message);
    } finally {
      setBusy(false);
    }
  }

  async function remove() {
    if (!form.id) return;
    setBusy(true);
    try {
      await onDeactivate(form.id);
      setForm(emptyForm);
      setMessage('Paciente desativado.');
    } catch (error) {
      setMessage(error.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <section className="content">
      <PageHeading title="Pacientes" text="Cadastre prontuarios e mantenha o contexto clinico usado pela IA." />
      <div className="patients-workspace">
        <div className="panel">
          <div className="panel-head">
            <div><h2>Pacientes ativos</h2><p>{patients.length} cadastro(s)</p></div>
            <button onClick={() => setForm(emptyForm)}><Plus size={16} />Novo</button>
          </div>
          <div className="patient-list">
            {patients.map((patient) => (
              <button
                key={patient.id}
                className={String(patient.id) === String(selectedPatientId) ? 'patient-row selected' : 'patient-row'}
                onClick={() => selectPatient(patient)}
              >
                <span className="avatar">{initials(patient.name)}</span>
                <span><strong>{patient.name}</strong><small>{patient.cpf || 'CPF nao informado'}</small></span>
                <small>{patient.consultations.length} consulta(s)</small>
              </button>
            ))}
            {!patients.length && <EmptyState text="Nenhum paciente cadastrado." />}
          </div>
        </div>
        <form className="panel editor-card" onSubmit={submit}>
          <div className="panel-head">
            <div><h2>{form.id ? 'Editar paciente' : 'Novo paciente'}</h2><p>Dados clinicos e de contato.</p></div>
          </div>
          <div className="form-grid">
            <Field label="Nome" value={form.name} onChange={(name) => setForm({ ...form, name })} required />
            <Field label="CPF" value={form.cpf} onChange={(cpf) => setForm({ ...form, cpf })} required />
            <Field label="Nascimento" type="date" value={form.birthDate || ''} onChange={(birthDate) => setForm({ ...form, birthDate })} />
            <Field label="Telefone" value={form.phone || ''} onChange={(phone) => setForm({ ...form, phone })} />
            <Field label="E-mail" type="email" value={form.email || ''} onChange={(email) => setForm({ ...form, email })} />
          </div>
          <TextArea label="Notas clinicas" value={form.clinicalNotes || ''} onChange={(clinicalNotes) => setForm({ ...form, clinicalNotes })} />
          <TextArea label="Historico alimentar" value={form.eatingHistory || ''} onChange={(eatingHistory) => setForm({ ...form, eatingHistory })} />
          {message && <div className="inline-message">{message}</div>}
          <div className="actions">
            <button className="primary" type="submit" disabled={busy}><Save size={17} />Salvar paciente</button>
            {form.id && <button className="danger-button" type="button" onClick={remove} disabled={busy}><Trash2 size={17} />Desativar</button>}
          </div>
        </form>
      </div>
    </section>
  );
}

function Consultation({ patients, selectedPatientId, setSelectedPatientId, onAnalyze }) {
  const [form, setForm] = useState({
    patientId: selectedPatientId || '',
    consentAudio: false,
    consentVideo: false,
    clinicalNotes: '',
    manualTranscript: '',
    visualNotes: '',
    audio: null
  });
  const [result, setResult] = useState(null);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState('');
  const [recordingState, setRecordingState] = useState('idle');
  const [recordingSeconds, setRecordingSeconds] = useState(0);
  const recorderRef = useRef(null);
  const streamRef = useRef(null);
  const chunksRef = useRef([]);
  const timerRef = useRef(null);
  const discardOnStopRef = useRef(false);
  const patient = patients.find((item) => String(item.id) === String(form.patientId));

  useEffect(() => () => {
    discardOnStopRef.current = true;
    clearRecordingTimer();
    if (recorderRef.current?.state === 'recording') recorderRef.current.stop();
    stopInputStream();
  }, []);

  async function submit(event) {
    event.preventDefault();
    if (!patient) return setMessage('Selecione um paciente.');
    if (recordingState === 'recording') return setMessage('Pare a gravacao antes de analisar a consulta.');
    if (form.audio && !form.consentAudio) return setMessage('Marque o consentimento para audio antes de enviar a gravacao.');
    if (!form.manualTranscript.trim() && !form.audio) return setMessage('Informe a transcricao ou grave/selecione um audio.');
    setBusy(true);
    setMessage('');
    try {
      const audio = form.audio ? await fileToPayload(form.audio) : null;
      const analysis = await onAnalyze({ ...form, patient, audio });
      setSelectedPatientId(patient.id);
      setResult(analysis);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setBusy(false);
    }
  }

  async function startRecording() {
    if (!form.consentAudio) return setMessage('Marque o consentimento para audio antes de gravar.');
    if (!navigator.mediaDevices?.getUserMedia || typeof MediaRecorder === 'undefined') {
      return setMessage('Este navegador nao permite gravacao de audio pelo sistema.');
    }

    try {
      setMessage('');
      discardOnStopRef.current = false;
      chunksRef.current = [];
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      const mimeType = supportedAudioMimeType();
      const recorder = mimeType ? new MediaRecorder(stream, { mimeType }) : new MediaRecorder(stream);
      recorderRef.current = recorder;

      recorder.ondataavailable = (event) => {
        if (event.data?.size > 0) chunksRef.current.push(event.data);
      };

      recorder.onstop = () => {
        clearRecordingTimer();
        stopInputStream();
        if (discardOnStopRef.current) {
          discardOnStopRef.current = false;
          chunksRef.current = [];
          return;
        }

        if (!chunksRef.current.length) {
          setRecordingState('idle');
          setMessage('Nenhum audio foi capturado. Tente gravar novamente.');
          return;
        }

        const type = recorder.mimeType || mimeType || 'audio/webm';
        const blob = new Blob(chunksRef.current, { type });
        const file = new File([blob], `consulta-${timestampForFile()}.${audioExtension(type)}`, { type });
        setForm((current) => ({ ...current, audio: file }));
        setRecordingState('recorded');
      };

      setForm((current) => ({ ...current, audio: null }));
      setRecordingSeconds(0);
      setRecordingState('recording');
      recorder.start();
      timerRef.current = window.setInterval(() => setRecordingSeconds((seconds) => seconds + 1), 1000);
    } catch (error) {
      clearRecordingTimer();
      stopInputStream();
      setRecordingState('idle');
      setMessage(`Nao foi possivel acessar o microfone: ${error.message}`);
    }
  }

  function stopRecording() {
    if (recorderRef.current?.state === 'recording') {
      recorderRef.current.requestData?.();
      recorderRef.current.stop();
    }
  }

  function discardRecording() {
    discardOnStopRef.current = true;
    if (recorderRef.current?.state === 'recording') recorderRef.current.stop();
    clearRecordingTimer();
    stopInputStream();
    chunksRef.current = [];
    setForm((current) => ({ ...current, audio: null }));
    setRecordingSeconds(0);
    setRecordingState('idle');
    setMessage('');
  }

  function selectUploadedAudio(file) {
    setForm({ ...form, audio: file });
    setRecordingSeconds(0);
    setRecordingState(file ? 'recorded' : 'idle');
  }

  function clearRecordingTimer() {
    if (!timerRef.current) return;
    window.clearInterval(timerRef.current);
    timerRef.current = null;
  }

  function stopInputStream() {
    streamRef.current?.getTracks().forEach((track) => track.stop());
    streamRef.current = null;
  }

  return (
    <section className="content">
      <PageHeading title="Consulta com IA" text="A IA analisa o atendimento e gera um rascunho clinico para revisao obrigatoria." />
      <div className="consultation-layout">
        <form className="panel editor-card" onSubmit={submit}>
          <label className="field-label">Paciente
            <select value={form.patientId} onChange={(event) => setForm({ ...form, patientId: event.target.value })}>
              <option value="">Selecione</option>
              {patients.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
            </select>
          </label>
          <div className="consent-row">
            <label><input type="checkbox" checked={form.consentAudio} onChange={(event) => setForm({ ...form, consentAudio: event.target.checked })} />Consentimento para audio</label>
            <label><input type="checkbox" checked={form.consentVideo} onChange={(event) => setForm({ ...form, consentVideo: event.target.checked })} />Consentimento para video</label>
          </div>
          <div className={`recording-card ${recordingState === 'recording' ? 'live' : ''}`}>
            <div className="recording-head">
              <span className={`recording-dot ${recordingState === 'recording' ? 'live' : ''}`} />
              <strong>{recordingState === 'recording' ? 'Gravando consulta' : form.audio ? 'Audio pronto' : 'Gravacao da consulta'}</strong>
              <span className="recording-time">{formatDuration(recordingSeconds)}</span>
            </div>
            <div className="recording-actions">
              {recordingState === 'recording' ? (
                <button className="danger-button" type="button" onClick={stopRecording} disabled={busy}>
                  <CheckCircle2 size={17} />Parar gravacao
                </button>
              ) : (
                <button className="primary" type="button" onClick={startRecording} disabled={busy || !patients.length || !form.consentAudio}>
                  <Mic size={17} />Gravar consulta
                </button>
              )}
              {form.audio || recordingState === 'recording' ? (
                <button className="secondary" type="button" onClick={discardRecording} disabled={busy}>
                  <Trash2 size={17} />Descartar
                </button>
              ) : null}
            </div>
            <span className="recording-meta">
              {form.audio ? `${form.audio.name} pronto para analise` : form.consentAudio ? 'Microfone pronto para iniciar' : 'Microfone aguardando consentimento'}
            </span>
          </div>
          <label className="upload-field">
            <Mic size={18} />
            <span>{form.audio ? form.audio.name : 'Selecionar audio da consulta'}</span>
            <input type="file" accept="audio/*" onChange={(event) => selectUploadedAudio(event.target.files?.[0] || null)} />
          </label>
          <TextArea label="Notas clinicas" value={form.clinicalNotes} onChange={(clinicalNotes) => setForm({ ...form, clinicalNotes })} />
          <TextArea label="Transcricao manual ou complemento" value={form.manualTranscript} onChange={(manualTranscript) => setForm({ ...form, manualTranscript })} />
          <TextArea label="Observacoes visuais ou comportamentais" value={form.visualNotes} onChange={(visualNotes) => setForm({ ...form, visualNotes })} />
          {message && <div className="form-error">{message}</div>}
          <button className="primary stretch" disabled={busy || !patients.length || recordingState === 'recording'}>
            {busy ? <LoaderCircle className="spin" size={18} /> : <Brain size={18} />}
            {busy ? 'Analisando com IA...' : 'Encerrar consulta e analisar'}
          </button>
        </form>
        <AnalysisResult result={result} />
      </div>
    </section>
  );
}

function AnalysisResult({ result }) {
  return (
    <article className="panel analysis-result">
      <div className="panel-head">
        <div><h2>Resultado da analise</h2><p>Rascunho sujeito a revisao profissional.</p></div>
        <Bot size={22} />
      </div>
      {!result && <EmptyState text="A analise aparecera aqui apos o encerramento da consulta." />}
      {result && (
        <>
          <span className="chip neutral">{result.model}</span>
          <h3>Resumo</h3>
          <p>{result.summary}</p>
          <h3>Alertas</h3>
          <AlertList alerts={result.risks} />
          <h3>Plano inicial</h3>
          <p><strong>{result.mealPlan.objective}</strong></p>
          <p>{result.mealPlan.description}</p>
        </>
      )}
    </article>
  );
}

function Reports({ patients, selectedPatientId, setSelectedPatientId }) {
  const patient = patients.find((item) => String(item.id) === String(selectedPatientId)) || patients[0];
  const consultation = latestConsultation(patient);
  return (
    <section className="content">
      <PageHeading title="Relatorios clinicos" text="Achados da IA, relato clinico, recomendacoes e limitacoes em um unico documento." />
      <label className="patient-selector">Paciente
        <select value={patient?.id || ''} onChange={(event) => setSelectedPatientId(event.target.value)}>
          {patients.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
        </select>
      </label>
      {!patient && <EmptyState text="Cadastre um paciente para consultar relatorios." />}
      {patient && (
        <div className="report-layout">
          <aside className="panel patient-summary">
            <div className="avatar large">{initials(patient.name)}</div>
            <h2>{patient.name}</h2>
            <p>{patient.clinicalNotes}</p>
            <AlertList alerts={consultation?.alerts || []} />
          </aside>
          <article className="report-paper">
            {(consultation?.report || 'Nenhum relatorio gerado para este paciente.').split('\n\n').map((block, index) => (
              <p key={index}>{block}</p>
            ))}
          </article>
        </div>
      )}
    </section>
  );
}

function Plans({ data, onApprove }) {
  const [busy, setBusy] = useState('');
  const plans = data.patients.flatMap((patient) => patient.plans.map((plan) => ({ ...plan, patient: patient.name })));
  async function approve(planId) {
    setBusy(planId);
    try {
      await onApprove(planId);
    } finally {
      setBusy('');
    }
  }
  return (
    <section className="content">
      <PageHeading title="Planos alimentares" text="A sugestao da IA so entra em vigor depois da aprovacao do nutricionista." />
      <div className="plan-grid">
        {plans.map((plan) => (
          <article className="plan-card" key={plan.id || `${plan.patient}-${plan.objective}`}>
            <div className="plan-head">
              <span className={`chip ${plan.status === 'APPROVED' ? 'success' : 'neutral'}`}>{planStatus(plan.status)}</span>
              <ClipboardList size={20} />
            </div>
            <h2>{plan.patient}</h2>
            <h3>{plan.objective}</h3>
            <p>{plan.description}</p>
            {plan.status !== 'APPROVED' && (
              <button className="primary" onClick={() => approve(plan.id)} disabled={busy === plan.id}>
                <CheckCircle2 size={17} />Aprovar plano
              </button>
            )}
          </article>
        ))}
        {!plans.length && <EmptyState text="Nenhum plano alimentar gerado." />}
      </div>
    </section>
  );
}

function Admin({ data }) {
  return (
    <section className="content">
      <PageHeading title="Administracao" text="Estado da infraestrutura e indicadores do ambiente." />
      <div className="admin-grid">
        <Feature icon={Database} title="Supabase" text={isSupabaseConfigured ? 'Autenticacao e banco conectados.' : 'Modo local ativo. Variaveis VITE_SUPABASE ainda nao configuradas.'} />
        <Feature icon={Bot} title="OpenAI" text={isSupabaseConfigured ? 'Edge Function preparada para exigir OPENAI_API_KEY no servidor.' : 'A analise exibida localmente e simulada para desenvolvimento.'} />
        <Feature icon={ShieldCheck} title="RLS" text="Politicas isolam prontuarios por nutricionista e permitem auditoria administrativa." />
        <Feature icon={HeartPulse} title="Uso academico" text="A IA apoia decisoes e nao substitui avaliacao clinica nem diagnostico." />
      </div>
      <div className="admin-status">
        <div><h2>Resumo do ambiente</h2><p>Indicadores calculados a partir dos prontuarios atuais.</p></div>
        <pre className="json-preview">{JSON.stringify(statsFor(data), null, 2)}</pre>
      </div>
    </section>
  );
}

function PageHeading({ title, text }) {
  return <div className="page-heading"><h1>{title}</h1><p>{text}</p></div>;
}

function Feature({ icon: Icon, title, text }) {
  return <article className="feature"><Icon size={24} /><h2>{title}</h2><p>{text}</p></article>;
}

function Metric({ icon: Icon, label, value, tone }) {
  return <div className={`metric ${tone}`}><Icon size={20} /><span>{label}</span><strong>{value}</strong></div>;
}

function WorkflowStep({ icon: Icon, title, text }) {
  return <article className="workflow-step"><Icon size={20} /><strong>{title}</strong><span>{text}</span></article>;
}

function Field({ label, value, onChange, type = 'text', required = false }) {
  return <label className="field-label">{label}<input required={required} type={type} value={value} onChange={(event) => onChange(event.target.value)} /></label>;
}

function TextArea({ label, value, onChange, required = false }) {
  return <label className="field-label">{label}<textarea required={required} value={value} onChange={(event) => onChange(event.target.value)} /></label>;
}

function StatusBanner({ children }) {
  return <div className="status-banner"><AlertTriangle size={18} /><span>{children}</span></div>;
}

function EmptyState({ text }) {
  return <div className="empty-state"><FileText size={22} /><span>{text}</span></div>;
}

function FullScreenLoader() {
  return <div className="full-loader"><LoaderCircle className="spin" size={28} /><span>Carregando Nutrimind...</span></div>;
}

function PatientTable({ patients }) {
  if (!patients.length) return <EmptyState text="Nenhum paciente cadastrado." />;
  return (
    <table>
      <thead><tr><th>Paciente</th><th>Historico</th><th>Risco</th><th>Consultas</th></tr></thead>
      <tbody>
        {patients.map((patient) => {
          const latest = latestConsultation(patient);
          const severity = highestSeverity(latest?.alerts || []);
          return <tr key={patient.id}><td><strong>{patient.name}</strong></td><td>{patient.eatingHistory}</td><td><span className={`chip ${severityClass(severity)}`}>{severity}</span></td><td>{patient.consultations.length}</td></tr>;
        })}
      </tbody>
    </table>
  );
}

function AlertList({ alerts, onDecide }) {
  if (!alerts.length) return <p className="muted">Nenhum alerta identificado.</p>;
  return (
    <div className="alert-list">
      {alerts.map((alert, index) => (
        <div className="alert-item" key={alert.id || `${alert.type}-${index}`}>
          <span className={`chip ${severityClass(alert.severity)}`}>{alert.severity}</span>
          <strong>{alert.type}</strong>
          <p>{alert.message}</p>
          {onDecide && alert.status !== 'DECIDED' && (
            <button className="alert-action" type="button" onClick={() => onDecide(alert.id)}>
              Registrar revisao
            </button>
          )}
        </div>
      ))}
    </div>
  );
}

function normalizeData(data) {
  return {
    generatedAt: data.generatedAt || new Date().toISOString(),
    patients: (data.patients || []).map((patient) => ({
      ...patient,
      consultations: (patient.consultations || []).map((consultation, consultationIndex) => ({
        ...consultation,
        alerts: (consultation.alerts || []).map((alert, alertIndex) => ({
          ...alert,
          id: alert.id || `${patient.id}-alert-${consultationIndex}-${alertIndex}`,
          status: alert.status || 'OPEN'
        }))
      })),
      plans: (patient.plans || []).map((plan, index) => ({
        ...plan,
        id: plan.id || `${patient.id}-plan-${index}`,
        status: normalizePlanStatus(plan.status)
      }))
    }))
  };
}

function buildLocalAnalysis(input) {
  const content = `${input.clinicalNotes} ${input.manualTranscript} ${input.visualNotes}`.toLowerCase();
  const risks = [];
  if (content.includes('ansiedade') || content.includes('culpa')) risks.push({ type: 'Ansiedade alimentar', severity: 'MODERADO', justification: 'Relato associado a desconforto emocional durante a alimentacao.', message: 'Revisar sinais de ansiedade e culpa apos refeicoes.' });
  if (content.includes('pulo') || content.includes('pular') || content.includes('sem comer')) risks.push({ type: 'Omissao de refeicoes', severity: 'LEVE', justification: 'Ha indicio de baixa regularidade alimentar.', message: 'Monitorar horarios e frequencia das refeicoes.' });
  if (content.includes('compuls') || content.includes('perda de controle')) risks.push({ type: 'Compulsao alimentar', severity: 'GRAVE', justification: 'Relato de perda de controle requer avaliacao profissional.', message: 'Avaliar encaminhamento e acompanhamento multiprofissional.' });
  if (!risks.length) risks.push({ type: 'Acompanhamento preventivo', severity: 'LEVE', justification: 'Nao foram detectados marcadores de maior gravidade na simulacao.', message: 'Manter acompanhamento e revisar contexto clinico.' });
  return {
    model: 'SIMULACAO_LOCAL',
    transcript: input.manualTranscript,
    summary: `Simulacao local para ${input.patient.name}. Revise os achados antes de qualquer decisao clinica.`,
    risks,
    recommendations: ['Revisar rotina alimentar.', 'Registrar decisao profissional.'],
    mealPlan: { objective: 'Regularidade alimentar', description: 'Plano inicial demonstrativo com rotina alimentar estruturada e revisao profissional.' },
    report: {
      identificationSection: `Paciente: ${input.patient.name}. Analise demonstrativa local.`,
      clinicalSection: input.clinicalNotes || 'Sem notas clinicas.',
      recommendationsSection: 'Revisar rotina, frequencia alimentar e sinais comportamentais.',
      limitationsSection: 'Resultado simulado. Configure Supabase e OPENAI_API_KEY para usar IA real.'
    }
  };
}

function appendLocalAnalysis(data, patientId, result) {
  const id = Date.now();
  return {
    ...data,
    patients: data.patients.map((patient) => patient.id !== patientId ? patient : {
      ...patient,
      consultations: [{
        id,
        patientName: patient.name,
        date: new Date().toISOString(),
        summary: result.summary,
        transcript: result.transcript,
        alerts: result.risks.map((alert, index) => ({
          ...alert,
          id: `${id}-alert-${index}`,
          status: 'OPEN'
        })),
        report: [
          'PARTE 1 - Identificacao e achados da IA', result.report.identificationSection,
          'PARTE 2 - Relato e impressao clinica', result.report.clinicalSection,
          'PARTE 3 - Recomendacoes e plano', result.report.recommendationsSection,
          'LIMITACOES DA IA', result.report.limitationsSection
        ].join('\n\n')
      }, ...patient.consultations],
      plans: [{ id: `${id}-plan`, ...result.mealPlan, status: 'IN_REVIEW' }, ...patient.plans]
    })
  };
}

async function fileToPayload(file) {
  const base64 = await new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result).split(',')[1]);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
  return { name: file.name, type: file.type, base64 };
}

function supportedAudioMimeType() {
  if (typeof MediaRecorder === 'undefined') return '';
  return [
    'audio/webm;codecs=opus',
    'audio/webm',
    'audio/ogg;codecs=opus',
    'audio/ogg',
    'audio/mp4'
  ].find((type) => MediaRecorder.isTypeSupported(type)) || '';
}

function audioExtension(mimeType) {
  if (mimeType.includes('ogg')) return 'ogg';
  if (mimeType.includes('mp4')) return 'm4a';
  return 'webm';
}

function timestampForFile() {
  return new Date().toISOString().replace(/[:.]/g, '-');
}

function formatDuration(seconds) {
  const minutes = Math.floor(seconds / 60).toString().padStart(2, '0');
  const remainingSeconds = (seconds % 60).toString().padStart(2, '0');
  return `${minutes}:${remainingSeconds}`;
}

function latestConsultation(patient) {
  return patient?.consultations?.[0];
}

function totalConsultations(data) {
  return data.patients.reduce((sum, patient) => sum + patient.consultations.length, 0);
}

function allAlerts(data) {
  return data.patients.flatMap((patient) => patient.consultations.flatMap((consultation) => consultation.alerts || []));
}

function countPlans(data, status) {
  return data.patients.reduce((sum, patient) => sum + patient.plans.filter((plan) => normalizePlanStatus(plan.status) === status).length, 0);
}

function statsFor(data) {
  return { pacientes: data.patients.length, consultas: totalConsultations(data), alertas: allAlerts(data).length, planos: data.patients.reduce((sum, patient) => sum + patient.plans.length, 0) };
}

function highestSeverity(alerts) {
  if (alerts.some((alert) => alert.severity === 'GRAVE')) return 'GRAVE';
  if (alerts.some((alert) => alert.severity === 'MODERADO')) return 'MODERADO';
  return 'LEVE';
}

function severityClass(severity) {
  if (severity === 'GRAVE') return 'danger';
  if (severity === 'MODERADO') return 'moderate';
  return 'neutral';
}

function normalizePlanStatus(status) {
  if (status === 'APROVADO' || status === 'APPROVED') return 'APPROVED';
  return 'IN_REVIEW';
}

function planStatus(status) {
  return normalizePlanStatus(status) === 'APPROVED' ? 'APROVADO' : 'EM REVISAO';
}

function initials(name) {
  return name.split(' ').filter(Boolean).slice(0, 2).map((part) => part[0]).join('').toUpperCase();
}

createRoot(document.getElementById('root')).render(<App />);
