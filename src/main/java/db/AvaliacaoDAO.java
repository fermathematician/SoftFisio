package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import models.Avaliacao;
import exceptions.DataIntegrityException;

public class AvaliacaoDAO {

// QUERIES ATUALIZADAS PARA AS NOVAS COLUNAS
    private static final String SAVE_SQL = "INSERT INTO avaliacoes(id_paciente, data_avaliacao, doenca_atual, historia_pregressa, inspecao_palpacao, adm, forca_muscular, avaliacao_funcional, testes_especiais, escalas_funcionais, diagnostico_cinesiologico, plano_tratamento) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_PACIENTE_ID_SQL = "SELECT * FROM avaliacoes WHERE id_paciente = ? ORDER BY data_avaliacao DESC";
    private static final String UPDATE_SQL = "UPDATE avaliacoes SET data_avaliacao = ?, doenca_atual = ?, historia_pregressa = ?, inspecao_palpacao = ?, adm = ?, forca_muscular = ?, avaliacao_funcional = ?, testes_especiais = ?, escalas_funcionais = ?, diagnostico_cinesiologico = ?, plano_tratamento = ? WHERE id_avaliacao = ?";
    private static final String DELETE_SQL = "DELETE FROM avaliacoes WHERE id_avaliacao = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM avaliacoes WHERE id_avaliacao = ?";
    
   public boolean save(Avaliacao avaliaco) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SAVE_SQL)) {
            setParametersForSave(pstmt, avaliaco);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar avaliação: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public List<Avaliacao> findByPacienteId(int idPaciente) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_PACIENTE_ID_SQL)) {
            pstmt.setInt(1, idPaciente);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                avaliacoes.add(createAvaliacaoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliações: " + e.getMessage());
            e.printStackTrace();
        }
        return avaliacoes;
    }


    public boolean delete(int idAvaliacao) {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setInt(1, idAvaliacao);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                throw new DataIntegrityException("Não é possível excluir a avaliação, pois ela possui anexos vinculados.");
            }
            System.err.println("Erro ao deletar avaliação: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Avaliacao avaliaco) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            setParametersForUpdate(pstmt, avaliaco);
            pstmt.setInt(12, avaliaco.getId()); // WHERE id_avaliacao = ?
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar avaliação: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

     public Avaliacao findById(int idAvaliacao) {
        Avaliacao avaliacao = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            pstmt.setInt(1, idAvaliacao);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                avaliacao = createAvaliacaoFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliação por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return avaliacao;
    }
    // --- MÉTODOS AUXILIARES PARA EVITAR REPETIÇÃO ---

    private Avaliacao createAvaliacaoFromResultSet(ResultSet rs) throws SQLException {
        return new Avaliacao(
                rs.getInt("id_avaliacao"),
                rs.getInt("id_paciente"),
                LocalDate.parse(rs.getString("data_avaliacao")),
                rs.getString("doenca_atual"),
                rs.getString("historia_pregressa"),
                rs.getString("inspecao_palpacao"),
                rs.getString("adm"),
                rs.getString("forca_muscular"),
                rs.getString("avaliacao_funcional"),
                rs.getString("testes_especiais"),
                rs.getString("escalas_funcionais"),
                rs.getString("diagnostico_cinesiologico"),
                rs.getString("plano_tratamento")
        );
    }
    
    private void setParametersForSave(PreparedStatement pstmt, Avaliacao avaliacao) throws SQLException {
        pstmt.setInt(1, avaliacao.getIdPaciente());
        setCommonParameters(pstmt, avaliacao, 2);
    }
    
    private void setParametersForUpdate(PreparedStatement pstmt, Avaliacao avaliacao) throws SQLException {
        setCommonParameters(pstmt, avaliacao, 1);
    }

    private void setCommonParameters(PreparedStatement pstmt, Avaliacao avaliacao, int startIndex) throws SQLException {
        pstmt.setString(startIndex, avaliacao.getDataAvaliacao().toString());
        pstmt.setString(startIndex + 1, avaliacao.getDoencaAtual());
        pstmt.setString(startIndex + 2, avaliacao.getHistoriaPregressa());
        pstmt.setString(startIndex + 3, avaliacao.getInspecaoPalpacao());
        pstmt.setString(startIndex + 4, avaliacao.getAdm());
        pstmt.setString(startIndex + 5, avaliacao.getForcaMuscular());
        pstmt.setString(startIndex + 6, avaliacao.getAvaliacaoFuncional());
        pstmt.setString(startIndex + 7, avaliacao.getTestesEspeciais());
        pstmt.setString(startIndex + 8, avaliacao.getEscalasFuncionais());
        pstmt.setString(startIndex + 9, avaliacao.getDiagnosticoCinesiologico());
        pstmt.setString(startIndex + 10, avaliacao.getPlanoTratamento());
    }
}