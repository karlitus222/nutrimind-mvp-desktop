package br.com.nutrimind.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Patient {
    private long id;
    private long nutritionistId;
    private String name;
    private String cpf;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private String clinicalNotes;
    private String eatingHistory;
    private boolean active;
    private LocalDateTime createdAt;

    public Patient(long id, long nutritionistId, String name, String cpf, LocalDate birthDate, String phone,
                   String email, String clinicalNotes, String eatingHistory, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.nutritionistId = nutritionistId;
        this.name = name;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.clinicalNotes = clinicalNotes;
        this.eatingHistory = eatingHistory;
        this.active = active;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNutritionistId() {
        return nutritionistId;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public String getEatingHistory() {
        return eatingHistory;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}

