package br.com.nutrimind.service;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.AiAnalysisResult;
import br.com.nutrimind.model.RiskItem;
import br.com.nutrimind.model.Severity;
import br.com.nutrimind.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class GeminiRiskAnalysisService implements AiAnalysisService {
    private final GeminiClient client;

    public GeminiRiskAnalysisService(GeminiClient client) {
        this.client = client;
    }

    @Override
    public AiAnalysisResult analyze(AnalysisRequest request) {
        String response = client.generateContent(AppConfig.GEMINI_ANALYSIS_MODEL, buildPayload(request));
        String outputJson = normalizeJson(JsonUtil.extractString(response, "text"));
        if (outputJson.isBlank()) {
            throw new AppException("O Gemini nao retornou uma analise estruturada.");
        }
        return parseStructuredOutput(outputJson);
    }

    private String buildPayload(AnalysisRequest request) {
        String prompt = """
                Voce e o modulo de IA do Nutrimind, um sistema academico de apoio a nutricao comportamental.
                A IA apoia o nutricionista, mas nao substitui decisao profissional e nao fecha diagnostico.
                Nao invente fatos, habitos ou sintomas.
                So afirme comportamentos especificos quando eles estiverem explicitamente escritos no historico,
                nas notas clinicas, na transcricao ou nas observacoes visuais.
                Se um achado vier do historico cadastrado, diga isso claramente na justificativa.
                Se vier da fala atual, diga que veio da transcricao da consulta.
                Se a fala for vaga, por exemplo "tenho problema com comida", nao conclua que a pessoa pula refeicoes,
                tem compulsao, sente culpa ou restringe comida. Use um alerta leve de necessidade de aprofundamento
                e recomende perguntas de triagem.
                Identifique riscos apenas quando houver evidencia textual.

                == Identificacao do paciente ==
                Paciente: %s

                == Historico alimentar cadastrado, possivelmente desatualizado ==
                %s

                == Notas clinicas do nutricionista nesta consulta ==
                %s

                == Fala/transcricao desta consulta ==
                %s

                == Observacoes visuais ou comportamentais desta consulta ==
                %s

                Numero de consultas anteriores: %d
                """.formatted(
                request.getPatient().getName(),
                nullSafe(request.getPatient().getEatingHistory()),
                nullSafe(request.getClinicalNotes()),
                nullSafe(request.getTranscript()),
                nullSafe(request.getVisualObservations()),
                request.getHistory().size()
        );

        String schema = """
                {
                  "type":"object",
                  "properties":{
                    "summary":{"type":"string"},
                    "recommendations":{"type":"string"},
                    "meal_plan_suggestion":{"type":"string"},
                    "risks":{
                      "type":"array",
                      "items":{
                        "type":"object",
                        "properties":{
                          "type":{"type":"string"},
                          "severity":{"type":"string","enum":["LEVE","MODERADO","GRAVE"]},
                          "message":{"type":"string"},
                          "justification":{"type":"string"}
                        },
                        "required":["type","severity","message","justification"]
                      }
                    }
                  },
                  "required":["summary","recommendations","meal_plan_suggestion","risks"]
                }
                """;

        return """
                {
                  "contents": [
                    {
                      "parts": [
                        {"text": %s}
                      ]
                    }
                  ],
                  "generationConfig": {
                    "responseMimeType": "application/json",
                    "responseJsonSchema": %s
                  }
                }
                """.formatted(JsonUtil.quote(prompt), schema);
    }

    private AiAnalysisResult parseStructuredOutput(String outputJson) {
        List<RiskItem> risks = new ArrayList<>();
        for (String riskJson : JsonUtil.extractObjectsFromArray(outputJson, "risks")) {
            String severityValue = JsonUtil.extractString(riskJson, "severity");
            Severity severity = severityValue.isBlank() ? Severity.LEVE : Severity.valueOf(severityValue);
            risks.add(new RiskItem(
                    JsonUtil.extractString(riskJson, "type"),
                    severity,
                    JsonUtil.extractString(riskJson, "message"),
                    JsonUtil.extractString(riskJson, "justification")
            ));
        }
        return new AiAnalysisResult(
                "Gemini",
                AppConfig.GEMINI_ANALYSIS_MODEL,
                outputJson,
                JsonUtil.extractString(outputJson, "summary"),
                JsonUtil.extractString(outputJson, "recommendations"),
                JsonUtil.extractString(outputJson, "meal_plan_suggestion"),
                risks
        );
    }

    private String normalizeJson(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }
        int firstBreak = trimmed.indexOf('\n');
        int lastFence = trimmed.lastIndexOf("```");
        if (firstBreak >= 0 && lastFence > firstBreak) {
            return trimmed.substring(firstBreak + 1, lastFence).trim();
        }
        return trimmed;
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Nao informado." : value;
    }
}
