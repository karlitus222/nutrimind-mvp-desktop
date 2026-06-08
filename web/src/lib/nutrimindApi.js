import { supabase } from './supabase.js';

function requireSupabase() {
  if (!supabase) {
    throw new Error('Supabase nao configurado. Defina VITE_SUPABASE_URL e VITE_SUPABASE_PUBLISHABLE_KEY.');
  }
  return supabase;
}

function assertResult(result) {
  if (result.error) {
    throw result.error;
  }
  return result.data;
}

export async function getSession() {
  const client = requireSupabase();
  return assertResult(await client.auth.getSession()).session;
}

export function observeSession(callback) {
  const client = requireSupabase();
  return client.auth.onAuthStateChange((_event, session) => callback(session)).data.subscription;
}

export async function signIn(email, password) {
  const client = requireSupabase();
  return assertResult(await client.auth.signInWithPassword({ email, password })).user;
}

export async function signUp({ name, crn, email, password }) {
  const client = requireSupabase();
  return assertResult(
    await client.auth.signUp({
      email,
      password,
      options: {
        data: {
          full_name: name,
          crn
        }
      }
    })
  );
}

export async function signOut() {
  const client = requireSupabase();
  assertResult(await client.auth.signOut());
}

export async function loadWorkspace() {
  const client = requireSupabase();
  const patients = assertResult(
    await client
      .from('patients')
      .select(`
        id,
        name,
        cpf,
        birth_date,
        phone,
        email,
        clinical_notes,
        eating_history,
        consultations (
          id,
          created_at,
          ai_summary,
          transcript,
          alerts (id, type, severity, message, justification, status),
          reports (identification_section, clinical_section, recommendations_section, limitations_section)
        ),
        meal_plans (id, objective, description, status, approved_at)
      `)
      .eq('active', true)
      .order('created_at', { ascending: false })
  );

  return {
    generatedAt: new Date().toISOString(),
    patients: patients.map(toPatient)
  };
}

export async function savePatient(patient) {
  const client = requireSupabase();
  const user = assertResult(await client.auth.getUser()).user;
  const payload = {
    owner_id: user.id,
    name: patient.name,
    cpf: patient.cpf,
    birth_date: patient.birthDate || null,
    phone: patient.phone || '',
    email: patient.email || '',
    clinical_notes: patient.clinicalNotes || '',
    eating_history: patient.eatingHistory || ''
  };

  if (patient.id) {
    return assertResult(await client.from('patients').update(payload).eq('id', patient.id).select().single());
  }
  return assertResult(await client.from('patients').insert(payload).select().single());
}

export async function deactivatePatient(patientId) {
  const client = requireSupabase();
  assertResult(await client.from('patients').update({ active: false }).eq('id', patientId));
}

export async function analyzeConsultation(input) {
  const client = requireSupabase();
  const user = assertResult(await client.auth.getUser()).user;
  const functionResult = await client.functions.invoke('analyze-consultation', { body: input });
  const analysis = assertResult(functionResult);

  const consultation = assertResult(
    await client
      .from('consultations')
      .insert({
        patient_id: input.patient.id,
        nutritionist_id: user.id,
        consent_audio: input.consentAudio,
        consent_video: input.consentVideo,
        clinical_notes: input.clinicalNotes,
        manual_transcript: input.manualTranscript,
        transcript: analysis.transcript,
        visual_notes: input.visualNotes,
        status: 'ANALYZED',
        ai_summary: analysis.summary,
        ai_model: analysis.model
      })
      .select()
      .single()
  );

  if (analysis.risks.length) {
    assertResult(
      await client.from('alerts').insert(
        analysis.risks.map((risk) => ({
          consultation_id: consultation.id,
          type: risk.type,
          severity: risk.severity,
          justification: risk.justification,
          message: risk.message
        }))
      )
    );
  }

  assertResult(
    await client.from('reports').insert({
      consultation_id: consultation.id,
      identification_section: analysis.report.identificationSection,
      clinical_section: analysis.report.clinicalSection,
      recommendations_section: analysis.report.recommendationsSection,
      limitations_section: analysis.report.limitationsSection
    })
  );

  assertResult(
    await client.from('meal_plans').insert({
      patient_id: input.patient.id,
      consultation_id: consultation.id,
      objective: analysis.mealPlan.objective,
      description: analysis.mealPlan.description
    })
  );

  return analysis;
}

export async function approveMealPlan(planId) {
  const client = requireSupabase();
  const user = assertResult(await client.auth.getUser()).user;
  assertResult(
    await client
      .from('meal_plans')
      .update({ status: 'APPROVED', approved_by: user.id, approved_at: new Date().toISOString() })
      .eq('id', planId)
  );
}

export async function decideAlert(alertId, decision, notes) {
  const client = requireSupabase();
  assertResult(
    await client
      .from('alerts')
      .update({ status: 'DECIDED', decision, decision_notes: notes, decided_at: new Date().toISOString() })
      .eq('id', alertId)
  );
}

function toPatient(patient) {
  return {
    id: patient.id,
    name: patient.name,
    cpf: patient.cpf,
    birthDate: patient.birth_date,
    phone: patient.phone,
    email: patient.email,
    clinicalNotes: patient.clinical_notes,
    eatingHistory: patient.eating_history,
    consultations: (patient.consultations || [])
      .map((consultation) => ({
        id: consultation.id,
        patientName: patient.name,
        date: consultation.created_at,
        summary: consultation.ai_summary,
        transcript: consultation.transcript,
        alerts: consultation.alerts || [],
        report: toReport(consultation.reports?.[0])
      }))
      .sort((a, b) => new Date(b.date) - new Date(a.date)),
    plans: (patient.meal_plans || []).map((plan) => ({
      id: plan.id,
      objective: plan.objective,
      description: plan.description,
      status: plan.status,
      approvedAt: plan.approved_at
    }))
  };
}

function toReport(report) {
  if (!report) return '';
  return [
    'PARTE 1 - Identificacao e achados da IA',
    report.identification_section,
    'PARTE 2 - Relato e impressao clinica',
    report.clinical_section,
    'PARTE 3 - Recomendacoes e plano',
    report.recommendations_section,
    'LIMITACOES DA IA',
    report.limitations_section
  ].join('\n\n');
}

