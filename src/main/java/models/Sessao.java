// Em models/Sessao.java
package models;

import java.time.LocalDate;

public class Sessao implements HistoricoItem {

    private final int id;
    private final int idPaciente;
    private final LocalDate dataSessao; // Alterado para LocalDate
    private final String evolucaoTexto;
    private final String observacoesSessao;

    public Sessao(int id, int idPaciente, LocalDate dataSessao, String evolucaoTexto, String observacoesSessao) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.dataSessao = dataSessao;
        this.evolucaoTexto = evolucaoTexto;
        this.observacoesSessao = observacoesSessao;
    }

    // Getters
    public int getId() { return id; }
    public int getIdPaciente() { return idPaciente; }
    public LocalDate getDataSessao() { return dataSessao; } // Alterado para LocalDate
    public String getEvolucaoTexto() { return evolucaoTexto; }
    public String getObservacoesSessao() { return observacoesSessao; }

    @Override
    public LocalDate getData() { // Método da interface implementado
        return this.dataSessao;
    }

    @Override
    public String getTipo() {
        return "Sessão";
    }
}