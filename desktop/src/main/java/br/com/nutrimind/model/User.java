package br.com.nutrimind.model;

import java.time.LocalDateTime;

public abstract class User {

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private boolean active;
    private LocalDateTime createdAt;

    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(String name, String email, String passwordHash) {
        this();
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(int id, String name, String email, String passwordHash, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
        this.createdAt = createdAt;
    }

    public abstract String getRole();

    public String getDisplayName() {
        return name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + getRole() + ")";
    }
}
