package models;

import java.time.LocalDate;

public class Avaliacao implements HistoricoItem {

    private final int id;
    private final int idPaciente;
    private final LocalDate dataAvaliacao;

    // Novos campos baseados no PDF detalhado
    private final String doencaAtual;
    private final String historiaPregressa;
    private final String inspecaoPalpacao;
    private final String adm; // Amplitude de Movimento
    private final String forcaMuscular;
    private final String avaliacaoFuncional;
    private final String testesEspeciais;
    private final String escalasFuncionais;
    private final String diagnosticoCinesiologico;
    private final String planoTratamento;

    public Avaliacao(int id, int idPaciente, LocalDate dataAvaliacao, String doencaAtual, String historiaPregressa,
                     String inspecaoPalpacao, String adm, String forcaMuscular, String avaliacaoFuncional,
                     String testesEspeciais, String escalasFuncionais, String diagnosticoCinesiologico, String planoTratamento) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.dataAvaliacao = dataAvaliacao;
        this.doencaAtual = doencaAtual;
        this.historiaPregressa = historiaPregressa;
        this.inspecaoPalpacao = inspecaoPalpacao;
        this.adm = adm;
        this.forcaMuscular = forcaMuscular;
        this.avaliacaoFuncional = avaliacaoFuncional;
        this.testesEspeciais = testesEspeciais;
        this.escalasFuncionais = escalasFuncionais;
        this.diagnosticoCinesiologico = diagnosticoCinesiologico;
        this.planoTratamento = planoTratamento;
    }

    // Getters para todos os campos
    public int getId() { return id; }
    public int getIdPaciente() { return idPaciente; }
    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public String getDoencaAtual() { return doencaAtual; }
    public String getHistoriaPregressa() { return historiaPregressa; }
    public String getInspecaoPalpacao() { return inspecaoPalpacao; }
    public String getAdm() { return adm; }
    public String getForcaMuscular() { return forcaMuscular; }
    public String getAvaliacaoFuncional() { return avaliacaoFuncional; }
    public String getTestesEspeciais() { return testesEspeciais; }
    public String getEscalasFuncionais() { return escalasFuncionais; }
    public String getDiagnosticoCinesiologico() { return diagnosticoCinesiologico; }
    public String getPlanoTratamento() { return planoTratamento; }

    @Override
    public LocalDate getData() { return this.dataAvaliacao; }
    @Override
    public String getTipo() { return "Avaliação"; }
}