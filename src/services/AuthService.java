// src/services/AuthService.java
package src.services;

import db.UsuarioDAO;
import src.models.Usuario;

public class AuthService {

    private UsuarioDAO usuarioDAO;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Autentica um usuário com base no login e senha.
     * @param login O login fornecido pelo usuário.
     * @param senha A senha fornecida pelo usuário.
     * @return true se a autenticação for bem-sucedida, false caso contrário.
     */
    public boolean autenticar(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            return false; // Validação básica de entrada
        }

        Usuario usuario = usuarioDAO.findByLogin(login);

        if (usuario == null) {
            return false; // Usuário não encontrado
        }

        // ATENÇÃO: Comparação de senha em texto plano.
        // Em um projeto real, use uma biblioteca de hashing para comparar.
        // Ex: return BCrypt.checkpw(senha, usuario.getSenha());
        return usuario.getSenha().equals(senha);
    }
}