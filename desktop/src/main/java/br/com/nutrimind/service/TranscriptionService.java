package br.com.nutrimind.service;

import java.nio.file.Path;

@FunctionalInterface
public interface TranscriptionService {
    String transcribe(Path audioFile);
}
