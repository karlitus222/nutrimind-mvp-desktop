package br.com.nutrimind.config;

import java.nio.file.Path;

public final class AppConfig {
    public static final Path PROJECT_ROOT = Path.of(System.getProperty("nutrimind.root", ".")).toAbsolutePath().normalize();
    public static final Path DESKTOP_DIR = PROJECT_ROOT.resolve("desktop");
    public static final Path DATA_DIR = DESKTOP_DIR.resolve("data");
    public static final Path MEDIA_DIR = DATA_DIR.resolve("media");
    public static final Path EXPORT_DIR = DATA_DIR.resolve("exports");
    public static final String DB_URL = "jdbc:sqlite:" + DATA_DIR.resolve("nutrimind.db").toString();
    public static final String GEMINI_API_KEY = getenv("GEMINI_API_KEY", getenv("GOOGLE_API_KEY", ""));
    public static final String GEMINI_ANALYSIS_MODEL = getenv("GEMINI_ANALYSIS_MODEL", "gemini-2.5-flash-lite");
    public static final String GEMINI_TRANSCRIPTION_MODEL = getenv("GEMINI_TRANSCRIPTION_MODEL", "gemini-2.5-flash-lite");
    public static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    public static final String OPENAI_ANALYSIS_MODEL = getenv("OPENAI_ANALYSIS_MODEL", "gpt-5-mini");
    public static final String OPENAI_TRANSCRIPTION_MODEL = getenv("OPENAI_TRANSCRIPTION_MODEL", "gpt-4o-mini-transcribe");

    private AppConfig() {
    }

    public static boolean hasOpenAiKey() {
        return OPENAI_API_KEY != null && !OPENAI_API_KEY.isBlank();
    }

    public static boolean hasGeminiKey() {
        return GEMINI_API_KEY != null && !GEMINI_API_KEY.isBlank();
    }

    public static boolean hasAnyAiKey() {
        return hasGeminiKey() || hasOpenAiKey();
    }

    private static String getenv(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
