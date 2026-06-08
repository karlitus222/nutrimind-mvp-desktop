package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class SystemUser extends User {

    public SystemUser() {
        super();
    }

    public SystemUser(String name, String email, String passwordHash, Role role) {
        super(name, email, passwordHash, role);
    }

    public SystemUser(long id, String name, String email, String passwordHash,
                      Role role, boolean active, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, role, active, createdAt);
    }
}
