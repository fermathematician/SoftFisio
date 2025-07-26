package models;

import java.time.LocalDateTime;

public class Anexo {

    private final int id;
    private final int idPaciente;
    private final String caminhoArquivo;
    private final String tipoMidia;
    private final String descricao;
    private final LocalDateTime dataAnexo;
    private final Integer idSessaoRef;      // Usamos Integer para permitir valor nulo
    private final Integer idAvaliacaoRef;   // Usamos Integer para permitir valor nulo

    public Anexo(int id, int idPaciente, String caminhoArquivo, String tipoMidia, String descricao, 
                 LocalDateTime dataAnexo, Integer idSessaoRef, Integer idAvaliacaoRef) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.caminhoArquivo = caminhoArquivo;
        this.tipoMidia = tipoMidia;
        this.descricao = descricao;
        this.dataAnexo = dataAnexo;
        this.idSessaoRef = idSessaoRef;
        this.idAvaliacaoRef = idAvaliacaoRef;
    }

    // Getters
    public int getId() { return id; }
    public int getIdPaciente() { return idPaciente; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public String getTipoMidia() { return tipoMidia; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataAnexo() { return dataAnexo; }
    public Integer getIdSessaoRef() { return idSessaoRef; }
    public Integer getIdAvaliacaoRef() { return idAvaliacaoRef; }
}