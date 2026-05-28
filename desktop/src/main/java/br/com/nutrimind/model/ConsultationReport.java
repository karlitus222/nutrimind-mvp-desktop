package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class ConsultationReport {
    private long id;
    private long consultationId;
    private String identificationSection;
    private String clinicalSection;
    private String recommendationsSection;
    private String limitationsSection;
    private LocalDateTime generatedAt;

    public ConsultationReport(long id, long consultationId, String identificationSection, String clinicalSection,
                              String recommendationsSection, String limitationsSection, LocalDateTime generatedAt) {
        this.id = id;
        this.consultationId = consultationId;
        this.identificationSection = identificationSection;
        this.clinicalSection = clinicalSection;
        this.recommendationsSection = recommendationsSection;
        this.limitationsSection = limitationsSection;
        this.generatedAt = generatedAt;
    }

    public long getId() {
        return id;
    }

    public long getConsultationId() {
        return consultationId;
    }

    public String getIdentificationSection() {
        return identificationSection;
    }

    public String getClinicalSection() {
        return clinicalSection;
    }

    public String getRecommendationsSection() {
        return recommendationsSection;
    }

    public String getLimitationsSection() {
        return limitationsSection;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}

