package br.com.nutrimind.model;

public enum Role {

    NUTRICIONISTA("Nutricionista"),
    ADMIN("Administrador");

    private final String label;

    Role(String label) {
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
