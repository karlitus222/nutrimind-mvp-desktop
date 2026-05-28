package br.com.nutrimind.service;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.AiAnalysisResult;
import br.com.nutrimind.model.RiskItem;
import br.com.nutrimind.model.Severity;
import br.com.nutrimind.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class OpenAiRiskAnalysisService implements AiAnalysisService {
    private final OpenAiClient client;

    public OpenAiRiskAnalysisService(OpenAiClient client) {
        this.client = client;
    }

    @Override
    public AiAnalysisResult analyze(AnalysisRequest request) {
        String payload = buildPayload(request);
        String response = client.postJson("/v1/responses", payload);
        String outputJson = extractOutputText(response);
        if (outputJson.isBlank()) {
            throw new AppException("A IA não retornou uma análise estruturada.");
        }
        return parseStructuredOutput(outputJson);
    }

    private String buildPayload(AnalysisRequest request) {
        String prompt = """
                Você é o módulo de IA do Nutrimind, um sistema acadêmico de apoio à nutrição comportamental.
                Analise a transcrição e os dados clínicos em português. Não faça diagnóstico fechado.
                Gere alertas para ansiedade alimentar, compulsão alimentar, dietas extremas, pular refeições,
                desconforto emocional ligado à alimentação e outros riscos nutricionais relevantes.
                A decisão final é sempre do nutricionista.

                Paciente: %s
                Histórico alimentar: %s
                Observações clínicas: %s
                Observações visuais/comportamentais: %s
                Transcrição da consulta: %s
                Número de consultas anteriores: %d
                """.formatted(
                request.getPatient().getName(),
                nullSafe(request.getPatient().getEatingHistory()),
                nullSafe(request.getClinicalNotes()),
                nullSafe(request.getVisualObservations()),
                nullSafe(request.getTranscript()),
                request.getHistory().size()
        );

        String schema = """
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "summary":{"type":"string"},
                    "recommendations":{"type":"string"},
                    "meal_plan_suggestion":{"type":"string"},
                    "risks":{
                      "type":"array",
                      "items":{
                        "type":"object",
                        "additionalProperties":false,
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
                  "model": %s,
                  "input": [
                    {
                      "role": "system",
                      "content": [{"type":"input_text","text":"Responda somente com JSON válido seguindo o schema. Linguagem: português do Brasil."}]
                    },
                    {
                      "role": "user",
                      "content": [{"type":"input_text","text":%s}]
                    }
                  ],
                  "text": {
                    "format": {
                      "type": "json_schema",
                      "name": "nutrimind_analysis",
                      "strict": true,
                      "schema": %s
                    }
                  }
                }
                """.formatted(JsonUtil.quote(AppConfig.OPENAI_ANALYSIS_MODEL), JsonUtil.quote(prompt), schema);
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
                "OpenAI",
                AppConfig.OPENAI_ANALYSIS_MODEL,
                outputJson,
                JsonUtil.extractString(outputJson, "summary"),
                JsonUtil.extractString(outputJson, "recommendations"),
                JsonUtil.extractString(outputJson, "meal_plan_suggestion"),
                risks
        );
    }

    private String extractOutputText(String response) {
        int typeIndex = response.indexOf("\"type\":\"output_text\"");
        if (typeIndex < 0) {
            typeIndex = response.indexOf("\"type\": \"output_text\"");
        }
        if (typeIndex >= 0) {
            String slice = response.substring(typeIndex);
            return JsonUtil.extractString(slice, "text");
        }
        String direct = JsonUtil.extractString(response, "output_text");
        if (!direct.isBlank()) {
            return direct;
        }
        return JsonUtil.extractString(response, "text");
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Não informado." : value;
    }
}

