package br.com.nutrimind.model;

public enum ConsultationStatus {

    EM_ANDAMENTO("Em andamento"),
    ENCERRADA("Encerrada"),
    CANCELADA("Cancelada");

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
