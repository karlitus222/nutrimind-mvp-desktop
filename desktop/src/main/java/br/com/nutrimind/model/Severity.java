package br.com.nutrimind.model;

public enum Severity {

    LEVE("Leve"),
    MODERADO("Moderado"),
    GRAVE("Grave");

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
