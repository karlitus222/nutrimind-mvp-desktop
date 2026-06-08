import "jsr:@supabase/functions-js/edge-runtime.d.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

const analysisSchema = {
  type: "object",
  additionalProperties: false,
  properties: {
    summary: { type: "string" },
    risks: {
      type: "array",
      items: {
        type: "object",
        additionalProperties: false,
        properties: {
          type: { type: "string" },
          severity: { type: "string", enum: ["LEVE", "MODERADO", "GRAVE"] },
          justification: { type: "string" },
          message: { type: "string" },
        },
        required: ["type", "severity", "justification", "message"],
      },
    },
    recommendations: {
      type: "array",
      items: { type: "string" },
    },
    mealPlan: {
      type: "object",
      additionalProperties: false,
      properties: {
        objective: { type: "string" },
        description: { type: "string" },
      },
      required: ["objective", "description"],
    },
    report: {
      type: "object",
      additionalProperties: false,
      properties: {
        identificationSection: { type: "string" },
        clinicalSection: { type: "string" },
        recommendationsSection: { type: "string" },
        limitationsSection: { type: "string" },
      },
      required: [
        "identificationSection",
        "clinicalSection",
        "recommendationsSection",
        "limitationsSection",
      ],
    },
  },
  required: ["summary", "risks", "recommendations", "mealPlan", "report"],
};

const geminiResponseSchema = {
  type: "object",
  properties: {
    summary: { type: "string" },
    risks: {
      type: "array",
      items: {
        type: "object",
        properties: {
          type: { type: "string" },
          severity: { type: "string", enum: ["LEVE", "MODERADO", "GRAVE"] },
          justification: { type: "string" },
          message: { type: "string" },
        },
        required: ["type", "severity", "justification", "message"],
      },
    },
    recommendations: { type: "array", items: { type: "string" } },
    mealPlan: {
      type: "object",
      properties: {
        objective: { type: "string" },
        description: { type: "string" },
      },
      required: ["objective", "description"],
    },
    report: {
      type: "object",
      properties: {
        identificationSection: { type: "string" },
        clinicalSection: { type: "string" },
        recommendationsSection: { type: "string" },
        limitationsSection: { type: "string" },
      },
      required: [
        "identificationSection",
        "clinicalSection",
        "recommendationsSection",
        "limitationsSection",
      ],
    },
  },
  required: ["summary", "risks", "recommendations", "mealPlan", "report"],
};

Deno.serve(async (request: Request) => {
  if (request.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const input = await request.json();
    const geminiKey = Deno.env.get("GEMINI_API_KEY") || Deno.env.get("GOOGLE_API_KEY");
    const openAiKey = Deno.env.get("OPENAI_API_KEY");

    if (geminiKey) {
      const transcript = await buildGeminiTranscript(geminiKey, input);
      const model = Deno.env.get("GEMINI_ANALYSIS_MODEL") || "gemini-2.0-flash";
      const analysis = await analyzeWithGemini(geminiKey, model, buildContext(input, transcript));
      return json({ ...analysis, transcript, model, provider: "Gemini" });
    }

    if (!openAiKey) {
      throw new Error("Configure GEMINI_API_KEY para apresentacao gratuita ou OPENAI_API_KEY para OpenAI.");
    }

    const transcript = await buildOpenAiTranscript(openAiKey, input);
    const model = Deno.env.get("OPENAI_ANALYSIS_MODEL") || "gpt-5.4-mini";
    const analysis = await analyzeWithOpenAI(openAiKey, model, buildContext(input, transcript));
    return json({ ...analysis, transcript, model, provider: "OpenAI" });
  } catch (error) {
    return json({ error: error.message }, 400);
  }
});

function buildContext(input: Record<string, unknown>, transcript: string) {
  const patient = input.patient as { name?: string; eatingHistory?: string } | undefined;
  return [
    "== Identificacao do paciente ==",
    `Paciente: ${patient?.name || "Nao informado"}`,
    "",
    "== Historico alimentar cadastrado, possivelmente desatualizado ==",
    `${patient?.eatingHistory || "Nao informado"}`,
    "",
    "== Notas clinicas do nutricionista nesta consulta ==",
    `${input.clinicalNotes || "Nao informado"}`,
    "",
    "== Fala/transcricao desta consulta ==",
    `${transcript || "Nao informada"}`,
    "",
    "== Observacoes visuais ou comportamentais desta consulta ==",
    `${input.visualNotes || "Nao informadas"}`,
  ].join("\n\n");
}

async function buildGeminiTranscript(geminiKey: string, input: Record<string, unknown>) {
  const manualTranscript = String(input.manualTranscript || "").trim();
  const audio = input.audio as { name?: string; type?: string; base64?: string } | undefined;
  if (!audio?.base64) {
    return manualTranscript;
  }

  if (!input.consentAudio) {
    throw new Error("O audio so pode ser transcrito apos o consentimento do paciente.");
  }

  const model = Deno.env.get("GEMINI_TRANSCRIPTION_MODEL") || "gemini-2.0-flash";
  const response = await fetch(
    `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${geminiKey}`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [
          {
            parts: [
              { text: "Transcreva este audio em portugues brasileiro. Retorne apenas a transcricao." },
              {
                inline_data: {
                  mime_type: audio.type || "audio/webm",
                  data: audio.base64,
                },
              },
            ],
          },
        ],
      }),
    },
  );
  const body = await response.json();
  if (!response.ok) {
    throw new Error(body.error?.message || "Falha ao transcrever audio com Gemini.");
  }
  return [extractGeminiText(body), manualTranscript].filter(Boolean).join("\n\n");
}

async function buildOpenAiTranscript(openAiKey: string, input: Record<string, unknown>) {
  const manualTranscript = String(input.manualTranscript || "").trim();
  const audio = input.audio as { name?: string; type?: string; base64?: string } | undefined;
  if (!audio?.base64) {
    return manualTranscript;
  }

  if (!input.consentAudio) {
    throw new Error("O audio so pode ser transcrito apos o consentimento do paciente.");
  }

  const bytes = Uint8Array.from(atob(audio.base64), (character) => character.charCodeAt(0));
  const form = new FormData();
  form.append("model", Deno.env.get("OPENAI_TRANSCRIPTION_MODEL") || "gpt-4o-mini-transcribe");
  form.append("language", "pt");
  form.append("file", new Blob([bytes], { type: audio.type || "audio/webm" }), audio.name || "consulta.webm");

  const response = await fetch("https://api.openai.com/v1/audio/transcriptions", {
    method: "POST",
    headers: { Authorization: `Bearer ${openAiKey}` },
    body: form,
  });
  const body = await response.json();
  if (!response.ok) {
    throw new Error(body.error?.message || "Falha ao transcrever audio com OpenAI.");
  }
  return [body.text, manualTranscript].filter(Boolean).join("\n\n");
}

async function analyzeWithGemini(geminiKey: string, model: string, context: string) {
  const prompt = [
    "Voce apoia nutricionistas em nutricao comportamental.",
    "Nao diagnostique e nao substitua decisao profissional.",
    "Nao invente fatos, habitos ou sintomas. So afirme comportamentos especificos quando eles estiverem explicitamente escritos no historico, nas notas clinicas, na transcricao ou nas observacoes.",
    "Se um achado vier do historico cadastrado, diga isso claramente na justificativa. Se vier da fala atual, diga que veio da transcricao da consulta.",
    "Se a fala for vaga, por exemplo 'tenho problema com comida', nao conclua que a pessoa pula refeicoes, tem compulsao, sente culpa ou restringe comida. Use um alerta leve de 'necessidade de aprofundamento' e recomende perguntas de triagem.",
    "Identifique riscos apenas quando houver evidencia textual, justifique cada achado e proponha somente um plano alimentar inicial para revisao obrigatoria.",
    "Responda em JSON conforme o schema configurado.",
    "",
    context,
  ].join("\n");

  const response = await fetch(
    `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${geminiKey}`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
          responseMimeType: "application/json",
          responseJsonSchema: geminiResponseSchema,
        },
      }),
    },
  );
  const body = await response.json();
  if (!response.ok) {
    throw new Error(body.error?.message || "Falha ao analisar consulta com Gemini.");
  }
  const outputText = extractGeminiText(body);
  if (!outputText) {
    throw new Error("O Gemini nao retornou a analise estruturada.");
  }
  return JSON.parse(outputText);
}

async function analyzeWithOpenAI(openAiKey: string, model: string, context: string) {
  const response = await fetch("https://api.openai.com/v1/responses", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${openAiKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model,
      input: [
        {
          role: "system",
          content: [
            {
              type: "input_text",
              text:
                "Voce apoia nutricionistas em nutricao comportamental. Nao diagnostique. Nao invente fatos, habitos ou sintomas. So afirme comportamentos especificos quando eles estiverem explicitamente escritos no historico, nas notas clinicas, na transcricao ou nas observacoes. Se um achado vier do historico cadastrado, diga isso claramente na justificativa. Se vier da fala atual, diga que veio da transcricao da consulta. Se a fala for vaga, por exemplo 'tenho problema com comida', nao conclua que a pessoa pula refeicoes, tem compulsao, sente culpa ou restringe comida. Use um alerta leve de necessidade de aprofundamento e recomende perguntas de triagem. Identifique riscos apenas quando houver evidencia textual, justifique cada achado e proponha somente um plano alimentar inicial para revisao obrigatoria do nutricionista. Registre limitacoes da IA com clareza.",
            },
          ],
        },
        {
          role: "user",
          content: [{ type: "input_text", text: context }],
        },
      ],
      text: {
        format: {
          type: "json_schema",
          name: "nutrimind_consultation_analysis",
          strict: true,
          schema: analysisSchema,
        },
      },
    }),
  });

  const responseBody = await response.json();
  if (!response.ok) {
    throw new Error(responseBody.error?.message || "Falha ao analisar consulta com OpenAI.");
  }

  const outputText = extractOpenAiOutputText(responseBody);
  if (!outputText) {
    throw new Error("A OpenAI nao retornou a analise estruturada.");
  }

  return JSON.parse(outputText);
}

function extractGeminiText(response: Record<string, unknown>) {
  const candidates = Array.isArray(response.candidates) ? response.candidates : [];
  const parts = candidates.flatMap((candidate) => {
    const content = candidate.content as { parts?: Array<{ text?: string }> } | undefined;
    return Array.isArray(content?.parts) ? content.parts : [];
  });
  return parts.map((part) => part.text || "").join("").trim();
}

function extractOpenAiOutputText(response: Record<string, unknown>) {
  if (typeof response.output_text === "string") {
    return response.output_text;
  }
  const output = Array.isArray(response.output) ? response.output : [];
  for (const item of output) {
    const content = Array.isArray(item.content) ? item.content : [];
    for (const part of content) {
      if (part.type === "output_text" && typeof part.text === "string") {
        return part.text;
      }
    }
  }
  return "";
}

function json(body: Record<string, unknown>, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { ...corsHeaders, "Content-Type": "application/json" },
  });
}
