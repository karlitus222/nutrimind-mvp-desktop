package br.com.nutrimind.model;

public class RiskItem {
    private final String type;
    private final Severity severity;
    private final String message;
    private final String justification;

    public RiskItem(String type, Severity severity, String message, String justification) {
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.justification = justification;
    }

    public String getType() {
        return type;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getJustification() {
        return justification;
    }
}

