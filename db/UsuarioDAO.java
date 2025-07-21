// db/UsuarioDAO.java
package db;

import src.models.Usuario; // Importa a classe de modelo
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario findByLogin(String login) {
        String sql = "SELECT * FROM usuarios WHERE login = ?";
        Usuario usuario = null;

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("nome_completo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return usuario;
    }

    public boolean save(Usuario usuario) {
        // A senha deve ser "hasheada" antes de chegar aqui.
        // O AuthService será responsável por isso.
        String sql = "INSERT INTO usuarios(login, senha, nome_completo) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getLogin());
            pstmt.setString(2, usuario.getSenha());
            pstmt.setString(3, usuario.getNomeCompleto());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Retorna true se a inserção foi bem-sucedida

        } catch (SQLException e) {
            // Código 19 é a violação de restrição UNIQUE (login já existe)
            if (e.getErrorCode() == 19) {
                System.err.println("Erro ao salvar usuário: O login '" + usuario.getLogin() + "' já existe.");
            } else {
                System.err.println("Erro ao salvar usuário: " + e.getMessage());
            }
            return false;
        }
    }


    // Aqui você adicionaria outros métodos: save(Usuario u), update(Usuario u), delete(int id), etc.
}