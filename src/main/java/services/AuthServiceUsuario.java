// src/services/AuthService.java
package services;

import db.UsuarioDAO;
import models.Usuario;

public class AuthServiceUsuario {

    private UsuarioDAO usuarioDAO;

    public AuthServiceUsuario() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Autentica um usuário com base no login e senha.
     * @param login O login fornecido pelo usuário.
     * @param senha A senha fornecida pelo usuário.
     * @return true se a autenticação for bem-sucedida, false caso contrário.
     */
    public Usuario autenticar(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            return null; // Validação básica de entrada
        }

        Usuario usuario = usuarioDAO.findByLogin(login);

        if (usuario == null) {
            return null; // Usuário não encontrado
        }

        if (usuario.getSenha().equals(senha)) {
            return usuario;
        } else {
            return null; // Senha incorreta
        }
    }

    /**
     * Cadastra um novo usuário no sistema.
     * @param login O login desejado.
     * @param senha A senha desejada.
     * @param nomeCompleto O nome completo do usuário.
     * @return Uma string vazia em caso de sucesso, ou uma mensagem de erro.
     */
    public String cadastrar(String nomeCompleto, String login, String senha, String confirmarSenha) {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            return "O nome completo é obrigatório.";
        }

        if (login == null || login.trim().length() < 4) {
            return "O login deve ter pelo menos 4 caracteres.";
        }

        if (usuarioDAO.findByLogin(login) != null) {
            return "Este nome de usuário já está em uso.";
        }

        if (senha == null || senha.length() < 6) {
            return "A senha deve ter pelo menos 6 caracteres.";
        }
        
        if(confirmarSenha == null || !senha.equals(confirmarSenha)) {
            return "As senhas não coincidem";
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

        return sucesso ? "" : "Ocorreu um erro inesperado ao cadastrar o usuário no banco de dados.";
    }
}