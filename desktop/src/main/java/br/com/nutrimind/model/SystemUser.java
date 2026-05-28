package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class SystemUser extends User {

    private String departamento;

    public SystemUser() {
        super();
    }

    public SystemUser(String nome, String email, String senhaCriptografada, String departamento) {
        super(nome, email, senhaCriptografada);
        this.departamento = departamento;
    }

    public SystemUser(int id, String nome, String email, String senhaCriptografada,
                      boolean ativo, LocalDateTime criadoEm, String departamento) {
        super(id, nome, email, senhaCriptografada, ativo, criadoEm);
        this.departamento = departamento;
    }

    @Override
    public String getPerfil() {
        return "ADMIN";
    }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
