package br.com.nutrimind.service;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.util.JsonUtil;

import java.nio.file.Path;

public class OpenAiTranscriptionService implements TranscriptionService {
    private final OpenAiClient client;

    public OpenAiTranscriptionService(OpenAiClient client) {
        this.client = client;
    }

    @Override
    public String transcribe(Path audioFile) {
        String response = client.postMultipartAudio(audioFile, AppConfig.OPENAI_TRANSCRIPTION_MODEL);
        return JsonUtil.extractString(response, "text");
    }
}
