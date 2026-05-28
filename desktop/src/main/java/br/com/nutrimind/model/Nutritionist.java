package br.com.nutrimind.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Nutritionist extends User {

    private String cfn;
    private String specialty;
    private List<Consultation> consultations = new ArrayList<>();

    public Nutritionist() {
        super();
    }

    public Nutritionist(String name, String email, String passwordHash, String cfn, String specialty) {
        super(name, email, passwordHash);
        this.cfn = cfn;
        this.specialty = specialty;
    }

    public Nutritionist(int id, String name, String email, String passwordHash,
                        boolean active, LocalDateTime createdAt, String cfn, String specialty) {
        super(id, name, email, passwordHash, active, createdAt);
        this.cfn = cfn;
        this.specialty = specialty;
    }

    @Override
    public String getRole() {
        return "NUTRITIONIST";
    }

    @Override
    public String getDisplayName() {
        return "Nutri. " + getName();
    }

    public String getCfn() { return cfn; }
    public void setCfn(String cfn) { this.cfn = cfn; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public List<Consultation> getConsultations() { return consultations; }
    public void setConsultations(List<Consultation> consultations) { this.consultations = consultations; }
}
