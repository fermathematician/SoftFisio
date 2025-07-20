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

    /**
     * Cadastra um novo usuário no sistema.
     * @param login O login desejado.
     * @param senha A senha desejada.
     * @param nomeCompleto O nome completo do usuário.
     * @return Uma string vazia em caso de sucesso, ou uma mensagem de erro.
     */
    public String cadastrar(String login, String senha, String nomeCompleto) {
        // 1. Validação de entradas
        if (login == null || login.trim().length() < 4) {
            return "O login deve ter pelo menos 4 caracteres.";
        }
        if (senha == null || senha.length() < 6) {
            return "A senha deve ter pelo menos 6 caracteres.";
        }
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            return "O nome completo é obrigatório.";
        }

        // 2. Verificar se o usuário já existe (regra de negócio)
        if (usuarioDAO.findByLogin(login) != null) {
            return "Este nome de usuário já está em uso.";
        }
        
        // ATENÇÃO: Hashing de Senha (CRÍTICO PARA SEGURANÇA)
        // Em um projeto real, você NUNCA salvaria a senha em texto plano.
        // Você usaria uma biblioteca como jBCrypt para criar um "hash".
        // String senhaHasheada = BCrypt.hashpw(senha, BCrypt.gensalt());
        // Por simplicidade, vamos manter o texto plano por enquanto.
        String senhaParaSalvar = senha;

        // 3. Criar o objeto e salvar
        Usuario novoUsuario = new Usuario(0, login, senhaParaSalvar, nomeCompleto);
        boolean sucesso = usuarioDAO.save(novoUsuario);

        return sucesso ? "" : "Ocorreu um erro inesperado ao salvar no banco de dados.";
    }
}