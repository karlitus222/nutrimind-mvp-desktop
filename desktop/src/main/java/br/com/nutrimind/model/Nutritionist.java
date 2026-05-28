package br.com.nutrimind.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Nutritionist extends User {

    private String cfn;
    private String especialidade;
    private List<Consultation> consultas = new ArrayList<>();

    public Nutritionist() {
        super();
    }

    public Nutritionist(String nome, String email, String senhaCriptografada, String cfn, String especialidade) {
        super(nome, email, senhaCriptografada);
        this.cfn = cfn;
        this.especialidade = especialidade;
    }

    public Nutritionist(int id, String nome, String email, String senhaCriptografada,
                        boolean ativo, LocalDateTime criadoEm, String cfn, String especialidade) {
        super(id, nome, email, senhaCriptografada, ativo, criadoEm);
        this.cfn = cfn;
        this.especialidade = especialidade;
    }

    @Override
    public String getPerfil() {
        return "NUTRICIONISTA";
    }

    @Override
    public String getNomeExibicao() {
        return "Nutri. " + getNome();
    }

    public String getCfn() { return cfn; }
    public void setCfn(String cfn) { this.cfn = cfn; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public List<Consultation> getConsultas() { return consultas; }
    public void setConsultas(List<Consultation> consultas) { this.consultas = consultas; }
}
