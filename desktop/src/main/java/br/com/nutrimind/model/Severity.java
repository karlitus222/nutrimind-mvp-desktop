package br.com.nutrimind.model;

public enum Severity {

    INFO("Informativo"),
    ATENCAO("Atenção"),
    ALERTA("Alerta");

    private final String rotulo;

    Severity(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
