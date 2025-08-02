package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import db.exceptions.DataIntegrityException;

import models.Paciente;

public class PacienteDAO {

    // QUERIES SQL CENTRALIZADAS
    private static final String SAVE_SQL = "INSERT INTO pacientes(id_usuario, nome, cpf, genero, telefone, email, data_nascimento, data_cadastro, paciente_corrida) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM pacientes WHERE id_paciente = ?";
    private static final String FIND_BY_USUARIO_ID_SQL = "SELECT * FROM pacientes WHERE id_usuario = ? AND paciente_corrida = ?";
    private static final String UPDATE_SQL = "UPDATE pacientes SET id_usuario = ?, nome = ?, cpf = ?, genero = ?, telefone = ?, email = ?, data_nascimento = ?, paciente_corrida = ? WHERE id_paciente = ?";
    private static final String DELETE_SQL = "DELETE FROM pacientes WHERE id_paciente = ?";

    /**
     * Salva um novo paciente no banco de dados.
     *
     * @param paciente O objeto Paciente a ser salvo.
     * @return true se o paciente foi salvo com sucesso, false caso contrário.
     */
    public boolean save(Paciente paciente) {
        String sql = SAVE_SQL;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, paciente.getIdUsuario());
            pstmt.setString(2, paciente.getNomeCompleto());
            pstmt.setString(3, paciente.getCpf());
            pstmt.setString(4, paciente.getGenero());
            pstmt.setString(5, paciente.getTelefone());
            pstmt.setString(6, paciente.getEmail());
            pstmt.setString(7, paciente.getDataNascimento().toString());
            pstmt.setString(8, paciente.getDataCadastro().toString());
            pstmt.setBoolean(9, paciente.isPacienteCorrida());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Código 19 é a violação de restrição UNIQUE (CPF já existe, se definido como UNIQUE)
            if (e.getErrorCode() == 19 && paciente.getCpf() != null && !paciente.getCpf().isEmpty()) {
                System.err.println("Erro ao salvar paciente: O CPF '" + paciente.getCpf() + "' já existe.");
            } else {
                System.err.println("Erro ao salvar paciente: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Busca um paciente pelo seu ID.
     *
     * @param id O ID do paciente a ser buscado.
     * @return O objeto Paciente encontrado ou null se não for encontrado.
     */
    public Paciente findById(int id) {
        String sql = FIND_BY_ID_SQL;
        Paciente paciente = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                paciente = new Paciente(
                    rs.getInt("id_paciente"),
                    rs.getInt("id_usuario"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("genero"),
                    rs.getString("telefone"),
                    rs.getString("email"),
                    LocalDate.parse(rs.getString("data_nascimento")),
                    rs.getBoolean("paciente_corrida")
                );               
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar paciente por ID: " + e.getMessage());
        }
        return paciente;
    }

    /**
     * Busca todos os pacientes associados a um determinado ID de usuário,
     * filtrando pelo status de "paciente de corrida".
     *
     * @param idUsuario O ID do usuário (fisioterapeuta) para filtrar os pacientes.
     * @param isPacienteCorrida O status de corrida para o filtro (true para pacientes de corrida, false para comuns).
     * @return Uma lista de objetos Paciente que correspondem ao filtro.
     */
    public List<Paciente> findByUsuarioId(int idUsuario, boolean isPacienteCorrida) { 
        // ALTERADO: Adicionada a condição para a coluna 'paciente_corrida'
        String sql = FIND_BY_USUARIO_ID_SQL;
        List<Paciente> pacientes = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            pstmt.setBoolean(2, isPacienteCorrida); 

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Paciente paciente = new Paciente(
                    rs.getInt("id_paciente"),
                    rs.getInt("id_usuario"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("genero"),
                    rs.getString("telefone"),
                    rs.getString("email"),
                    LocalDate.parse(rs.getString("data_nascimento")),
                    rs.getBoolean("paciente_corrida")
                );
                pacientes.add(paciente);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pacientes por ID de usuário: " + e.getMessage());
        }
        return pacientes;
    }

    /**
     * Atualiza as informações de um paciente existente.
     * O ID do paciente no objeto deve ser válido.
     *
     * @param paciente O objeto Paciente com as informações atualizadas.
     * @return true se o paciente foi atualizado com sucesso, false caso contrário.
     */
    public boolean update(Paciente paciente) {
        String sql = UPDATE_SQL;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, paciente.getIdUsuario());
            pstmt.setString(2, paciente.getNomeCompleto());
            pstmt.setString(3, paciente.getCpf());
            pstmt.setString(4, paciente.getGenero());
            pstmt.setString(5, paciente.getTelefone());
            pstmt.setString(6, paciente.getEmail());
            pstmt.setString(7, paciente.getDataNascimento().toString());
            pstmt.setBoolean(8, paciente.isPacienteCorrida());
            pstmt.setInt(9, paciente.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
             // Código 19 é a violação de restrição UNIQUE (CPF já existe para outro paciente)
            if (e.getErrorCode() == 19 && paciente.getCpf() != null && !paciente.getCpf().isEmpty()) {
                System.err.println("Erro ao atualizar paciente: O CPF '" + paciente.getCpf() + "' já existe para outro paciente.");
            } else {
                System.err.println("Erro ao atualizar paciente: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Deleta um paciente pelo seu ID.
     *
     * @param id O ID do paciente a ser deletado.
     * @return true se o paciente foi deletado com sucesso, false caso contrário.
     */
    
    public boolean delete(int id) {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Verifica se o erro é de violação de chave estrangeira
            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                throw new DataIntegrityException("Não é possível excluir o paciente, pois ele possui sessões ou avaliações vinculadas.");
            }
            // Para outros erros de SQL, apenas loga no console
            System.err.println("Erro ao deletar paciente: " + e.getMessage());
            return false;
        }
    }
}