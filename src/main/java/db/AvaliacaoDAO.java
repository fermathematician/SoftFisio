package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import models.Avaliacao;

public class AvaliacaoDAO {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public boolean save(Avaliacao avaliacao) {
        String sql = "INSERT INTO avaliacoes(id_paciente, data_avaliacao, queixa_principal, historico_doenca_atual, exames_fisicos, diagnostico_fisioterapeutico, plano_tratamento) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, avaliacao.getIdPaciente());
            pstmt.setString(2, avaliacao.getDataAvaliacao().format(formatter));
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
        String sql = "SELECT * FROM avaliacoes WHERE id_paciente = ? ORDER BY data_avaliacao DESC";
        List<Avaliacao> avaliacoes = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPaciente);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                avaliacoes.add(new Avaliacao(
                    rs.getInt("id_avaliacao"),
                    rs.getInt("id_paciente"),
                    LocalDateTime.parse(rs.getString("data_avaliacao"), formatter),
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
        String sql = "DELETE FROM avaliacoes WHERE id_avaliacao = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAvaliacao);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar avaliação: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Avaliacao avaliacao) {
        String sql = "UPDATE avaliacoes SET queixa_principal = ?, historico_doenca_atual = ?, " +
                     "exames_fisicos = ?, diagnostico_fisioterapeutico = ?, plano_tratamento = ? " +
                     "WHERE id_avaliacao = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, avaliacao.getQueixaPrincipal());
            pstmt.setString(2, avaliacao.getHistoricoDoencaAtual());
            pstmt.setString(3, avaliacao.getExamesFisicos());
            pstmt.setString(4, avaliacao.getDiagnosticoFisioterapeutico());
            pstmt.setString(5, avaliacao.getPlanoTratamento());
            pstmt.setInt(6, avaliacao.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar avaliação: " + e.getMessage());
            return false;
        }
    }

    public Avaliacao findById(int idAvaliacao) {
        String sql = "SELECT * FROM avaliacoes WHERE id_avaliacao = ?";
        Avaliacao avaliacao = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAvaliacao);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                avaliacao = new Avaliacao(
                    rs.getInt("id_avaliacao"),
                    rs.getInt("id_paciente"),
                    LocalDateTime.parse(rs.getString("data_avaliacao"), formatter),
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