package br.com.nutrimind.service;

import br.com.nutrimind.exception.AppException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class AudioRecorderService {
    private TargetDataLine line;
    private Thread writerThread;
    private Instant startedAt;
    private Path currentFile;

    public synchronized void start(Path file) {
        if (line != null && line.isOpen()) {
            throw new AppException("Já existe uma gravação em andamento.");
        }
        try {
            Files.createDirectories(file.getParent());
            AudioFormat format = new AudioFormat(16_000.0f, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                throw new AppException("Microfone não disponível ou formato de áudio não suportado.");
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            currentFile = file;
            startedAt = Instant.now();
            writerThread = new Thread(() -> {
                try (AudioInputStream stream = new AudioInputStream(line)) {
                    AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file.toFile());
                } catch (IOException ignored) {
                    // A interface registra falhas síncronas. O encerramento da linha encerra a escrita.
                }
            }, "nutrimind-audio-writer");
            writerThread.start();
        } catch (Exception e) {
            if (e instanceof AppException appException) {
                throw appException;
            }
            throw new AppException("Falha ao iniciar gravação de áudio.", e);
        }
    }

    public synchronized RecordedAudio stop() {
        if (line == null || !line.isOpen()) {
            throw new AppException("Não há gravação em andamento.");
        }
        line.stop();
        line.close();
        try {
            writerThread.join(3_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        int duration = (int) Duration.between(startedAt, Instant.now()).toSeconds();
        RecordedAudio audio = new RecordedAudio(currentFile, Math.max(duration, 1));
        line = null;
        writerThread = null;
        currentFile = null;
        return audio;
    }

    public boolean isRecording() {
        return line != null && line.isOpen();
    }

    public record RecordedAudio(Path file, int durationSeconds) {
    }
}

