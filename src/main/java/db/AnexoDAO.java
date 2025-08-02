package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import models.Anexo;

public class AnexoDAO {

    // QUERIES SQL CENTRALIZADAS
    private static final String SAVE_SQL = "INSERT INTO anexos(id_paciente, caminho_arquivo, tipo_midia, descricao, data_anexo, id_sessao_ref, id_avaliacao_ref) VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_PACIENTE_ID_SQL = "SELECT * FROM anexos WHERE id_paciente = ? ORDER BY data_anexo DESC";
    private static final String DELETE_SQL = "DELETE FROM anexos WHERE id_anexo = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM anexos WHERE id_anexo = ?";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public boolean save(Anexo anexo) {
        String sql = SAVE_SQL;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, anexo.getIdPaciente());
            pstmt.setString(2, anexo.getCaminhoArquivo());
            pstmt.setString(3, anexo.getTipoMidia());
            pstmt.setString(4, anexo.getDescricao());
            pstmt.setString(5, anexo.getDataAnexo().format(formatter));

            // Lida com IDs de referência que podem ser nulos
            if (anexo.getIdSessaoRef() != null) {
                pstmt.setInt(6, anexo.getIdSessaoRef());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            if (anexo.getIdAvaliacaoRef() != null) {
                pstmt.setInt(7, anexo.getIdAvaliacaoRef());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar anexo: " + e.getMessage());
            return false;
        }
    }

    public List<Anexo> findByPacienteId(int idPaciente) {
        String sql = FIND_BY_PACIENTE_ID_SQL;
        List<Anexo> anexos = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPaciente);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                anexos.add(new Anexo(
                    rs.getInt("id_anexo"),
                    rs.getInt("id_paciente"),
                    rs.getString("caminho_arquivo"),
                    rs.getString("tipo_midia"),
                    rs.getString("descricao"),
                    LocalDateTime.parse(rs.getString("data_anexo"), formatter),
                    (Integer) rs.getObject("id_sessao_ref"),
                    (Integer) rs.getObject("id_avaliacao_ref")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar anexos por ID de paciente: " + e.getMessage());
        }
        return anexos;
    }

    public boolean delete(int idAnexo) {
        String sql = DELETE_SQL;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAnexo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar anexo: " + e.getMessage());
            return false;
        }
    }

    public Anexo findById(int idAnexo) {
        String sql = FIND_BY_ID_SQL;
        Anexo anexo = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAnexo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                anexo = new Anexo(
                    rs.getInt("id_anexo"),
                    rs.getInt("id_paciente"),
                    rs.getString("caminho_arquivo"),
                    rs.getString("tipo_midia"),
                    rs.getString("descricao"),
                    LocalDateTime.parse(rs.getString("data_anexo"), formatter),
                    (Integer) rs.getObject("id_sessao_ref"),
                    (Integer) rs.getObject("id_avaliacao_ref")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar anexo por ID: " + e.getMessage());
        }
        return anexo;
    }
}