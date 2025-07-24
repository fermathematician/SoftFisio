package models;

import java.time.LocalDateTime;

public class Avaliacao implements HistoricoItem {

    private final int id;
    private final int idPaciente;
    private final LocalDateTime dataAvaliacao;
    private final String queixaPrincipal;
    private final String historicoDoencaAtual;
    private final String examesFisicos;
    private final String diagnosticoFisioterapeutico;
    private final String planoTratamento;

    public Avaliacao(int id, int idPaciente, LocalDateTime dataAvaliacao, String queixaPrincipal, 
                     String historicoDoencaAtual, String examesFisicos, 
                     String diagnosticoFisioterapeutico, String planoTratamento) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.dataAvaliacao = dataAvaliacao;
        this.queixaPrincipal = queixaPrincipal;
        this.historicoDoencaAtual = historicoDoencaAtual;
        this.examesFisicos = examesFisicos;
        this.diagnosticoFisioterapeutico = diagnosticoFisioterapeutico;
        this.planoTratamento = planoTratamento;
    }

    // Getters para todos os campos
    public int getId() { return id; }
    public int getIdPaciente() { return idPaciente; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public String getQueixaPrincipal() { return queixaPrincipal; }
    public String getHistoricoDoencaAtual() { return historicoDoencaAtual; }
    public String getExamesFisicos() { return examesFisicos; }
    public String getDiagnosticoFisioterapeutico() { return diagnosticoFisioterapeutico; }
    public String getPlanoTratamento() { return planoTratamento; }

    @Override
    public LocalDateTime getDataHora() {
        return this.dataAvaliacao;
    }

    @Override
    public String getTipo() {
        return "Avaliação";
    }
}