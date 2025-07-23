package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import models.Sessao;

public class SessaoDAO {

    // Formato padrão para salvar e ler datas do banco de dados
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Salva uma nova sessão no banco de dados.
     * @param sessao O objeto Sessao a ser salvo.
     * @return true se a sessão foi salva com sucesso, false caso contrário.
     */
    public boolean save(Sessao sessao) {
        String sql = "INSERT INTO sessoes(id_paciente, data_sessao, evolucao_texto, observacoes_sessao) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessao.getIdPaciente());
            // Converte o LocalDateTime para uma String formatada antes de salvar
            pstmt.setString(2, sessao.getDataSessao().format(formatter));
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
        // Ordena por data_sessao DESC para que as sessões mais recentes apareçam primeiro
        String sql = "SELECT * FROM sessoes WHERE id_paciente = ? ORDER BY data_sessao DESC";
        List<Sessao> sessoes = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPaciente);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Sessao sessao = new Sessao(
                    rs.getInt("id_sessao"),
                    rs.getInt("id_paciente"),
                    // Converte a String do banco de dados de volta para um LocalDateTime
                    LocalDateTime.parse(rs.getString("data_sessao"), formatter),
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

    // Futuramente, podemos adicionar métodos update() e delete() aqui.
}