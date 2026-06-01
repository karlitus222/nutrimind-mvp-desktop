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

    public ConsultationReport() {
        this.generatedAt = LocalDateTime.now();
    }

    public ConsultationReport(long consultationId, String identificationSection, String clinicalSection,
                               String recommendationsSection, String limitationsSection) {
        this();
        this.consultationId = consultationId;
        this.identificationSection = identificationSection;
        this.clinicalSection = clinicalSection;
        this.recommendationsSection = recommendationsSection;
        this.limitationsSection = limitationsSection;
    }

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

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getConsultationId() { return consultationId; }
    public void setConsultationId(long consultationId) { this.consultationId = consultationId; }

    public String getIdentificationSection() { return identificationSection; }
    public void setIdentificationSection(String identificationSection) { this.identificationSection = identificationSection; }

    public String getClinicalSection() { return clinicalSection; }
    public void setClinicalSection(String clinicalSection) { this.clinicalSection = clinicalSection; }

    public String getRecommendationsSection() { return recommendationsSection; }
    public void setRecommendationsSection(String recommendationsSection) { this.recommendationsSection = recommendationsSection; }

    public String getLimitationsSection() { return limitationsSection; }
    public void setLimitationsSection(String limitationsSection) { this.limitationsSection = limitationsSection; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
