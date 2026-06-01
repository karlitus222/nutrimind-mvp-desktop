package br.com.nutrimind.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

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
    private List<Consultation> consultations = new ArrayList<>();

    public Patient() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Patient(String name, String cpf, LocalDate birthDate, String phone, String email) {
        this();
        this.name = name;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
    }

    public Patient(long id, long nutritionistId, String name, String cpf, LocalDate birthDate,
                   String phone, String email, String clinicalNotes, String eatingHistory,
                   boolean active, LocalDateTime createdAt) {
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

    public int getAge() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getNutritionistId() { return nutritionistId; }
    public void setNutritionistId(long nutritionistId) { this.nutritionistId = nutritionistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

    public String getEatingHistory() { return eatingHistory; }
    public void setEatingHistory(String eatingHistory) { this.eatingHistory = eatingHistory; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Consultation> getConsultations() { return consultations; }
    public void setConsultations(List<Consultation> consultations) { this.consultations = consultations; }

    @Override
    public String toString() {
        return name + " (CPF: " + cpf + ")";
    }
}
