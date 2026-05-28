package br.com.nutrimind.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AiAnalysisResult {
    private final String provider;
    private final String model;
    private final String rawJson;
    private final String summary;
    private final String recommendations;
    private final String mealPlanSuggestion;
    private final List<RiskItem> risks;

    public AiAnalysisResult(String provider, String model, String rawJson, String summary,
                            String recommendations, String mealPlanSuggestion, List<RiskItem> risks) {
        this.provider = provider;
        this.model = model;
        this.rawJson = rawJson;
        this.summary = summary;
        this.recommendations = recommendations;
        this.mealPlanSuggestion = mealPlanSuggestion;
        this.risks = new ArrayList<>(risks);
    }

    public String getProvider() {
        return provider;
    }

    public String getModel() {
        return model;
    }

    public String getRawJson() {
        return rawJson;
    }

    public String getSummary() {
        return summary;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public String getMealPlanSuggestion() {
        return mealPlanSuggestion;
    }

    public List<RiskItem> getRisks() {
        return Collections.unmodifiableList(risks);
    }
}

