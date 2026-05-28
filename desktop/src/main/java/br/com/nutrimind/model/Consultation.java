package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class Consultation {

    private int id;
    private Patient paciente;
    private Nutritionist nutricionista;
    private LocalDateTime agendadoEm;
    private LocalDateTime concluidoEm;
    private ConsultationStatus status;
    private String anamnese;
    private String anotacoesNutricionista;
    private double pesoKg;
    private double alturaCm;
    private ConsultationReport relatorio;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Consultation() {
        this.status = ConsultationStatus.AGENDADA;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Consultation(Patient paciente, Nutritionist nutricionista, LocalDateTime agendadoEm) {
        this();
        this.paciente = paciente;
        this.nutricionista = nutricionista;
        this.agendadoEm = agendadoEm;
    }

    public Consultation(int id, Patient paciente, Nutritionist nutricionista,
                        LocalDateTime agendadoEm, LocalDateTime concluidoEm,
                        ConsultationStatus status, String anamnese,
                        String anotacoesNutricionista, double pesoKg, double alturaCm,
                        LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.paciente = paciente;
        this.nutricionista = nutricionista;
        this.agendadoEm = agendadoEm;
        this.concluidoEm = concluidoEm;
        this.status = status;
        this.anamnese = anamnese;
        this.anotacoesNutricionista = anotacoesNutricionista;
        this.pesoKg = pesoKg;
        this.alturaCm = alturaCm;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public double calcularImc() {
        if (alturaCm <= 0 || pesoKg <= 0) return 0;
        double alturaEmMetros = alturaCm / 100.0;
        return pesoKg / (alturaEmMetros * alturaEmMetros);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Patient getPaciente() { return paciente; }
    public void setPaciente(Patient paciente) { this.paciente = paciente; }

    public Nutritionist getNutricionista() { return nutricionista; }
    public void setNutricionista(Nutritionist nutricionista) { this.nutricionista = nutricionista; }

    public LocalDateTime getAgendadoEm() { return agendadoEm; }
    public void setAgendadoEm(LocalDateTime agendadoEm) { this.agendadoEm = agendadoEm; }

    public LocalDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(LocalDateTime concluidoEm) { this.concluidoEm = concluidoEm; }

    public ConsultationStatus getStatus() { return status; }
    public void setStatus(ConsultationStatus status) {
        this.status = status;
        this.atualizadoEm = LocalDateTime.now();
    }

    public String getAnamnese() { return anamnese; }
    public void setAnamnese(String anamnese) { this.anamnese = anamnese; }

    public String getAnotacoesNutricionista() { return anotacoesNutricionista; }
    public void setAnotacoesNutricionista(String anotacoesNutricionista) { this.anotacoesNutricionista = anotacoesNutricionista; }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }

    public double getAlturaCm() { return alturaCm; }
    public void setAlturaCm(double alturaCm) { this.alturaCm = alturaCm; }

    public ConsultationReport getRelatorio() { return relatorio; }
    public void setRelatorio(ConsultationReport relatorio) { this.relatorio = relatorio; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    @Override
    public String toString() {
        String nomePaciente = paciente != null ? paciente.getNome() : "sem paciente";
        return "Consulta de " + nomePaciente + " - " + status;
    }
}
