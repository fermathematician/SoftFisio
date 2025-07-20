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
                    rs.getInt("id"),
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

    // Aqui você adicionaria outros métodos: save(Usuario u), update(Usuario u), delete(int id), etc.
}