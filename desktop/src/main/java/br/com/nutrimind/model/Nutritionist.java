package br.com.nutrimind.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Nutritionist extends User {

    private String crn;
    private String specialty;
    private List<Consultation> consultations = new ArrayList<>();

    public Nutritionist() {
        super();
    }

    public Nutritionist(String name, String email, String passwordHash, String crn, String specialty) {
        super(name, email, passwordHash, Role.NUTRICIONISTA);
        this.crn = crn;
        this.specialty = specialty;
    }

    public Nutritionist(long id, String name, String email, String passwordHash,
                        Role role, boolean active, LocalDateTime createdAt,
                        String crn, String specialty) {
        super(id, name, email, passwordHash, role, active, createdAt);
        this.crn = crn;
        this.specialty = specialty;
    }

    @Override
    public String getDisplayName() {
        return "Nutri. " + getName();
    }

    public String getCrn() { return crn; }
    public void setCrn(String crn) { this.crn = crn; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public List<Consultation> getConsultations() { return consultations; }
    public void setConsultations(List<Consultation> consultations) { this.consultations = consultations; }
}
