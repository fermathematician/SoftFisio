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

    private static final String SAVE_SQL = "INSERT INTO avaliacoes(id_paciente, data_avaliacao, queixa_principal, historico_doenca_atual, exames_fisicos, diagnostico_fisioterapeutico, plano_tratamento) VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_PACIENTE_ID_SQL = "SELECT * FROM avaliacoes WHERE id_paciente = ? ORDER BY data_avaliacao DESC";
    private static final String DELETE_SQL = "DELETE FROM avaliacoes WHERE id_avaliacao = ?";
    
    // CORRIGIDO: Adicionado "data_avaliacao = ?" para um total de 7 interrogações '?'
    private static final String UPDATE_SQL = "UPDATE avaliacoes SET data_avaliacao = ?, queixa_principal = ?, historico_doenca_atual = ?, exames_fisicos = ?, diagnostico_fisioterapeutico = ?, plano_tratamento = ? WHERE id_avaliacao = ?";
    
    private static final String FIND_BY_ID_SQL = "SELECT * FROM avaliacoes WHERE id_avaliacao = ?";
    
    public boolean save(Avaliacao avaliacao) {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SAVE_SQL)) {

            pstmt.setInt(1, avaliacao.getIdPaciente());
            // Para colunas TEXT, usar toString() é aceitável.
            pstmt.setString(2, avaliacao.getDataAvaliacao().toString()); 
            pstmt.setString(3, avaliacao.getQueixaPrincipal());
            pstmt.setString(4, avaliacao.getHistoricoDoencaAtual());
            pstmt.setString(5, avaliacao.getExamesFisicos());
            pstmt.setString(6, avaliacao.getDiagnosticoFisioterapeutico());
            pstmt.setString(7, avaliacao.getPlanoTratamento());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar avaliação: " + e.getMessage());
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
                avaliacoes.add(new Avaliacao(
                    rs.getInt("id_avaliacao"),
                    rs.getInt("id_paciente"),
                    LocalDate.parse(rs.getString("data_avaliacao")),
                    rs.getString("queixa_principal"),
                    rs.getString("historico_doenca_atual"),
                    rs.getString("exames_fisicos"),
                    rs.getString("diagnostico_fisioterapeutico"),
                    rs.getString("plano_tratamento")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliações por ID de paciente: " + e.getMessage());
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

    // CORRIGIDO: Método update completo e funcional
    public boolean update(Avaliacao avaliacao) {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            
            // Parâmetro 1: data_avaliacao
            pstmt.setString(1, avaliacao.getDataAvaliacao().toString());

            // Parâmetros 2 a 6: campos de texto
            pstmt.setString(2, avaliacao.getQueixaPrincipal());
            pstmt.setString(3, avaliacao.getHistoricoDoencaAtual());
            pstmt.setString(4, avaliacao.getExamesFisicos());
            pstmt.setString(5, avaliacao.getDiagnosticoFisioterapeutico());
            pstmt.setString(6, avaliacao.getPlanoTratamento());
            
            // Parâmetro 7: id_avaliacao para a cláusula WHERE
            pstmt.setInt(7, avaliacao.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar avaliação: " + e.getMessage());
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
                avaliacao = new Avaliacao(
                    rs.getInt("id_avaliacao"),
                    rs.getInt("id_paciente"),
                    LocalDate.parse(rs.getString("data_avaliacao")),
                    rs.getString("queixa_principal"),
                    rs.getString("historico_doenca_atual"),
                    rs.getString("exames_fisicos"),
                    rs.getString("diagnostico_fisioterapeutico"),
                    rs.getString("plano_tratamento")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliação por ID: " + e.getMessage());
        }
        return avaliacao;
    }
}