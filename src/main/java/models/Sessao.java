package models;

import java.time.LocalDateTime;

public class Sessao {

    private final int id;
    private final int idPaciente;
    private final LocalDateTime dataSessao;
    private final String evolucaoTexto;
    private final String observacoesSessao;

    // Construtor para criar um objeto Sessao com todos os seus dados
    public Sessao(int id, int idPaciente, LocalDateTime dataSessao, String evolucaoTexto, String observacoesSessao) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.dataSessao = dataSessao;
        this.evolucaoTexto = evolucaoTexto;
        this.observacoesSessao = observacoesSessao;
    }

    // Métodos "Getters" para permitir que outras partes do código leiam os dados
    // de forma segura.
    public int getId() {
        return id;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public LocalDateTime getDataSessao() {
        return dataSessao;
    }

    public String getEvolucaoTexto() {
        return evolucaoTexto;
    }

    public String getObservacoesSessao() {
        return observacoesSessao;
    }
}