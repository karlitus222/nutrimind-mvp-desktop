package br.com.nutrimind.model;

import java.time.LocalDateTime;

public abstract class User {

    private int id;
    private String nome;
    private String email;
    private String senhaCriptografada;
    private boolean ativo;
    private LocalDateTime criadoEm;

    public User() {
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
    }

    public User(String nome, String email, String senhaCriptografada) {
        this();
        this.nome = nome;
        this.email = email;
        this.senhaCriptografada = senhaCriptografada;
    }

    public User(int id, String nome, String email, String senhaCriptografada, boolean ativo, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaCriptografada = senhaCriptografada;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
    }

    public abstract String getPerfil();

    public String getNomeExibicao() {
        return nome;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaCriptografada() { return senhaCriptografada; }
    public void setSenhaCriptografada(String senhaCriptografada) { this.senhaCriptografada = senhaCriptografada; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return nome + " (" + getPerfil() + ")";
    }
}
