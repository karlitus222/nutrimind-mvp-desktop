package br.com.nutrimind.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationReport {

    private int id;
    private Consultation consulta;
    private String resumo;
    private String planoAlimentar;
    private String recomendacoes;
    private boolean apoiadoPorIa;
    private String resumoSugeridoPelaIa;
    private List<String> alertasDaIa = new ArrayList<>();
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public ConsultationReport() {
        this.apoiadoPorIa = false;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public ConsultationReport(Consultation consulta, String resumo,
                               String planoAlimentar, String recomendacoes) {
        this();
        this.consulta = consulta;
        this.resumo = resumo;
        this.planoAlimentar = planoAlimentar;
        this.recomendacoes = recomendacoes;
    }

    public ConsultationReport(int id, Consultation consulta, String resumo,
                               String planoAlimentar, String recomendacoes,
                               boolean apoiadoPorIa, String resumoSugeridoPelaIa,
                               LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.consulta = consulta;
        this.resumo = resumo;
        this.planoAlimentar = planoAlimentar;
        this.recomendacoes = recomendacoes;
        this.apoiadoPorIa = apoiadoPorIa;
        this.resumoSugeridoPelaIa = resumoSugeridoPelaIa;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Consultation getConsulta() { return consulta; }
    public void setConsulta(Consultation consulta) { this.consulta = consulta; }

    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }

    public String getPlanoAlimentar() { return planoAlimentar; }
    public void setPlanoAlimentar(String planoAlimentar) { this.planoAlimentar = planoAlimentar; }

    public String getRecomendacoes() { return recomendacoes; }
    public void setRecomendacoes(String recomendacoes) { this.recomendacoes = recomendacoes; }

    public boolean isApoiadoPorIa() { return apoiadoPorIa; }
    public void setApoiadoPorIa(boolean apoiadoPorIa) { this.apoiadoPorIa = apoiadoPorIa; }

    public String getResumoSugeridoPelaIa() { return resumoSugeridoPelaIa; }
    public void setResumoSugeridoPelaIa(String resumoSugeridoPelaIa) { this.resumoSugeridoPelaIa = resumoSugeridoPelaIa; }

    public List<String> getAlertasDaIa() { return alertasDaIa; }
    public void setAlertasDaIa(List<String> alertasDaIa) { this.alertasDaIa = alertasDaIa; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
