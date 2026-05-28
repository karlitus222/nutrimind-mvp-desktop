package br.com.nutrimind.model;

public enum Severity {

    INFO("Informativo"),
    WARNING("Atenção"),
    ALERT("Alerta");

    private final String label;

    Severity(String label) {
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
