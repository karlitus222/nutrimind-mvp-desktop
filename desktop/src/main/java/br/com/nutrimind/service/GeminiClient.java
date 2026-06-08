package br.com.nutrimind.service;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.exception.AppException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class GeminiClient {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public void ensureConfigured() {
        if (!AppConfig.hasGeminiKey()) {
            throw new AppException("A integracao com IA e obrigatoria. Configure GEMINI_API_KEY para apresentacao gratuita ou OPENAI_API_KEY para OpenAI.");
        }
    }

    public String generateContent(String model, String body) {
        ensureConfigured();
        String encodedKey = URLEncoder.encode(AppConfig.GEMINI_API_KEY, StandardCharsets.UTF_8);
        URI uri = URI.create("https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + encodedKey);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AppException("Erro do Gemini (" + response.statusCode() + "): " + response.body());
            }
            return response.body();
        } catch (IOException e) {
            throw new AppException("Falha de rede ao chamar o Gemini.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppException("Chamada do Gemini interrompida.", e);
        }
    }
}
