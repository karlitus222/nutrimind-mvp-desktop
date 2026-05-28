package br.com.nutrimind.service;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.exception.AppException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

public class OpenAiClient {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public void ensureConfigured() {
        if (!AppConfig.hasOpenAiKey()) {
            throw new AppException("A integração com IA é obrigatória. Configure a variável OPENAI_API_KEY para analisar consultas.");
        }
    }

    public String postJson(String path, String body) {
        ensureConfigured();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com" + path))
                .timeout(Duration.ofSeconds(90))
                .header("Authorization", "Bearer " + AppConfig.OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    public String postMultipartAudio(Path audioFile, String model) {
        ensureConfigured();
        if (audioFile == null || !Files.exists(audioFile)) {
            throw new AppException("Nenhum arquivo de áudio foi encontrado para transcrição.");
        }
        try {
            String boundary = "NUTRIMIND-" + UUID.randomUUID();
            byte[] prefix = (
                    "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"model\"\r\n\r\n" +
                            model + "\r\n" +
                            "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"file\"; filename=\"" + audioFile.getFileName() + "\"\r\n" +
                            "Content-Type: audio/wav\r\n\r\n"
            ).getBytes(StandardCharsets.UTF_8);
            byte[] fileBytes = Files.readAllBytes(audioFile);
            byte[] suffix = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
            byte[] body = new byte[prefix.length + fileBytes.length + suffix.length];
            System.arraycopy(prefix, 0, body, 0, prefix.length);
            System.arraycopy(fileBytes, 0, body, prefix.length, fileBytes.length);
            System.arraycopy(suffix, 0, body, prefix.length + fileBytes.length, suffix.length);

            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/audio/transcriptions"))
                    .timeout(Duration.ofSeconds(120))
                    .header("Authorization", "Bearer " + AppConfig.OPENAI_API_KEY)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();
            return send(request);
        } catch (IOException e) {
            throw new AppException("Falha ao ler áudio para transcrição.", e);
        }
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AppException("Erro da OpenAI (" + response.statusCode() + "): " + response.body());
            }
            return response.body();
        } catch (IOException e) {
            throw new AppException("Falha de rede ao chamar a OpenAI.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppException("Chamada da OpenAI interrompida.", e);
        }
    }
}

