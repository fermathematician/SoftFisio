package services;

import models.Usuario;

public class SessaoUsuario {

    private static SessaoUsuario instance;

    private Usuario usuarioLogado;

    private SessaoUsuario() {}

    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    public void login(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    public boolean isLogado() {
        return this.usuarioLogado != null;
    }
}