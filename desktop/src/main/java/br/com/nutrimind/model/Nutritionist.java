package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class Nutritionist extends User {
    private String crn;
    private String specialty;

    public Nutritionist(long id, String name, String email, String passwordHash, Role role, boolean active,
                        LocalDateTime createdAt, String crn, String specialty) {
        super(id, name, email, passwordHash, role, active, createdAt);
        this.crn = crn;
        this.specialty = specialty;
    }

    public String getCrn() {
        return crn;
    }

    public String getSpecialty() {
        return specialty;
    }
}

