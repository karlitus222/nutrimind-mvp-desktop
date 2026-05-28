package br.com.nutrimind.service;

import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.Consultation;
import br.com.nutrimind.model.ConsultationReport;
import br.com.nutrimind.model.MealPlan;
import br.com.nutrimind.model.RiskAnalysis;

import java.util.List;

public class ConsultationResult {
    private final Consultation consultation;
    private final RiskAnalysis analysis;
    private final List<Alert> alerts;
    private final ConsultationReport report;
    private final MealPlan mealPlan;

    public ConsultationResult(Consultation consultation, RiskAnalysis analysis, List<Alert> alerts,
                              ConsultationReport report, MealPlan mealPlan) {
        this.consultation = consultation;
        this.analysis = analysis;
        this.alerts = alerts;
        this.report = report;
        this.mealPlan = mealPlan;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public RiskAnalysis getAnalysis() {
        return analysis;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public ConsultationReport getReport() {
        return report;
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }
}

