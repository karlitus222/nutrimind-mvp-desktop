package br.com.nutrimind.service;

import br.com.nutrimind.model.Role;

public class RoleAccessPolicy {
    public boolean canAccessClinicalWorkspace(Role role) {
        return role == Role.NUTRICIONISTA;
    }

    public boolean canAccessAdministration(Role role) {
        return role == Role.ADMIN;
    }
}
