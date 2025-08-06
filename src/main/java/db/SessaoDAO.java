package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Sessao;
import exceptions.DataIntegrityException;
import java.time.LocalDate;

public class SessaoDAO {

    // QUERIES SQL CENTRALIZADAS
    private static final String SAVE_SQL = "INSERT INTO sessoes(id_paciente, data_sessao, evolucao_texto, observacoes_sessao) VALUES(?, ?, ?, ?)";
    private static final String FIND_BY_PACIENTE_ID_SQL = "SELECT * FROM sessoes WHERE id_paciente = ? ORDER BY data_sessao DESC";
    private static final String UPDATE_SQL = "UPDATE sessoes SET id_paciente = ?, data_sessao = ?, evolucao_texto = ?, observacoes_sessao = ? WHERE id_sessao = ?";
    private static final String DELETE_SQL = "DELETE FROM sessoes WHERE id_sessao = ?";


    /**
     * Salva uma nova sessão no banco de dados.
     * @param sessao O objeto Sessao a ser salvo.
     * @return true se a sessão foi salva com sucesso, false caso contrário.
     */
    public boolean save(Sessao sessao) {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SAVE_SQL)) {

            pstmt.setInt(1, sessao.getIdPaciente());
            pstmt.setString(2, sessao.getDataSessao().toString()); // Alterado para usar toString()
            pstmt.setString(3, sessao.getEvolucaoTexto());
            pstmt.setString(4, sessao.getObservacoesSessao());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar sessão: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca todas as sessoes de um paciente específico, ordenadas pela mais recente.
     * @param idPaciente O ID do paciente para filtrar as sessoes.
     * @return Uma lista de objetos Sessao.
     */
    public List<Sessao> findByPacienteId(int idPaciente) {
    List<Sessao> sessoes = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(FIND_BY_PACIENTE_ID_SQL)) {

            pstmt.setInt(1, idPaciente);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Sessao sessao = new Sessao(
                    rs.getInt("id_sessao"),
                    rs.getInt("id_paciente"),
                    LocalDate.parse(rs.getString("data_sessao")), // Alterado para LocalDate.parse
                    rs.getString("evolucao_texto"),
                    rs.getString("observacoes_sessao")
                );
                sessoes.add(sessao);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar sessões por ID de paciente: " + e.getMessage());
        }
        return sessoes;
    }
    public boolean update(Sessao sessao) {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            pstmt.setInt(1, sessao.getIdPaciente());
            pstmt.setString(2, sessao.getDataSessao().toString()); // Alterado para usar toString()
            pstmt.setString(3, sessao.getEvolucaoTexto());
            pstmt.setString(4, sessao.getObservacoesSessao());
            pstmt.setInt(5, sessao.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar a sessao: " + e.getMessage());
            return false;
        }
    }

    // Em SessaoDAO.java

    
    public boolean delete(int idSessao) {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setInt(1, idSessao);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Verifica se o erro é de violação de chave estrangeira (ex: anexo vinculado)
            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                throw new DataIntegrityException("Não é possível excluir a sessão, pois ela possui anexos vinculados.");
            }
            // Para outros erros de SQL, loga com a mensagem correta
            System.err.println("Erro ao deletar sessão: " + e.getMessage());
            return false;
        }
    }
}