package br.com.nutrimind.service;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class GeminiTranscriptionService implements TranscriptionService {
    private final GeminiClient client;

    public GeminiTranscriptionService(GeminiClient client) {
        this.client = client;
    }

    @Override
    public String transcribe(Path audioFile) {
        if (audioFile == null || !Files.exists(audioFile)) {
            throw new AppException("Nenhum arquivo de audio foi encontrado para transcricao.");
        }
        try {
            String base64Audio = Base64.getEncoder().encodeToString(Files.readAllBytes(audioFile));
            String payload = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {"text": "Transcreva este audio em portugues brasileiro. Retorne apenas a transcricao."},
                            {
                              "inline_data": {
                                "mime_type": "audio/wav",
                                "data": %s
                              }
                            }
                          ]
                        }
                      ]
                    }
                    """.formatted(JsonUtil.quote(base64Audio));
            String response = client.generateContent(AppConfig.GEMINI_TRANSCRIPTION_MODEL, payload);
            return JsonUtil.extractString(response, "text");
        } catch (IOException e) {
            throw new AppException("Falha ao ler audio para transcricao.", e);
        }
    }
}
