package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import models.Paciente;

public class PacienteDAO {

    /**
     * Salva um novo paciente no banco de dados.
     *
     * @param paciente O objeto Paciente a ser salvo.
     * @return true se o paciente foi salvo com sucesso, false caso contrário.
     */
    public boolean save(Paciente paciente) {
        String sql = "INSERT INTO pacientes(id_usuario, nome, cpf, genero, telefone, email, data_nascimento, data_cadastro) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, paciente.getIdUsuario());
            pstmt.setString(2, paciente.getNomeCompleto());
            pstmt.setString(3, paciente.getCpf());
            pstmt.setString(4, paciente.getGenero());
            pstmt.setString(5, paciente.getTelefone());
            pstmt.setString(6, paciente.getEmail());
            pstmt.setString(7, paciente.getDataNascimento().toString()); // Converte LocalDate para String
            pstmt.setString(8, paciente.getDataCadastro().toString()); // Converte LocalDateTime para String

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
        String sql = "SELECT * FROM pacientes WHERE id_paciente = ?";
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
                    LocalDate.parse(rs.getString("data_nascimento")) // Converte String para LocalDate
                    // data_cadastro será definida automaticamente no construtor ou adicionada posteriormente se necessário
                );               
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar paciente por ID: " + e.getMessage());
        }
        return paciente;
    }

    /**
     * Busca todos os pacientes associados a um determinado ID de usuário.
     *
     * @param idUsuario O ID do usuário (fisioterapeuta) para filtrar os pacientes.
     * @return Uma lista de objetos Paciente.
     */
    public List<Paciente> findByUsuarioId(int idUsuario) {
        String sql = "SELECT * FROM pacientes WHERE id_usuario = ?";
        List<Paciente> pacientes = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
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
                    LocalDate.parse(rs.getString("data_nascimento"))
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
        String sql = "UPDATE pacientes SET id_usuario = ?, nome = ?, cpf = ?, genero = ?, telefone = ?, email = ?, data_nascimento = ? WHERE id_paciente = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, paciente.getIdUsuario());
            pstmt.setString(2, paciente.getNomeCompleto());
            pstmt.setString(3, paciente.getCpf());
            pstmt.setString(4, paciente.getGenero());
            pstmt.setString(5, paciente.getTelefone());
            pstmt.setString(6, paciente.getEmail());
            pstmt.setString(7, paciente.getDataNascimento().toString());
            pstmt.setInt(8, paciente.getId()); // O ID para o WHERE clause

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
        String sql = "DELETE FROM pacientes WHERE id_paciente = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar paciente: " + e.getMessage());
            return false;
        }
    }
}