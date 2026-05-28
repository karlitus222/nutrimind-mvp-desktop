package br.com.nutrimind.service;

import br.com.nutrimind.model.AiAnalysisResult;

public interface AiAnalysisService {
    AiAnalysisResult analyze(AnalysisRequest request);
}

