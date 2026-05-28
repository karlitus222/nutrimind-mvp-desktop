package br.com.nutrimind.model;

public enum ConsultationStatus {

    SCHEDULED("Agendada"),
    IN_PROGRESS("Em andamento"),
    COMPLETED("Concluída"),
    CANCELLED("Cancelada");

    private final String label;

    ConsultationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
