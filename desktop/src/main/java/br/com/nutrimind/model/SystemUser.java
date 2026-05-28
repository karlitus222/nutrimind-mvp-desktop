package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class SystemUser extends User {

    private String department;

    public SystemUser() {
        super();
    }

    public SystemUser(String name, String email, String passwordHash, String department) {
        super(name, email, passwordHash);
        this.department = department;
    }

    public SystemUser(int id, String name, String email, String passwordHash,
                      boolean active, LocalDateTime createdAt, String department) {
        super(id, name, email, passwordHash, active, createdAt);
        this.department = department;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
