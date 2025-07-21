// src/models/Usuario.java
package models;

public class Usuario {
    private int id;
    private String login;
    private String senha;
    private String nomeCompleto;

    // Construtor
    public Usuario(int id, String login, String senha, String nomeCompleto) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nomeCompleto = nomeCompleto;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getNomeCompleto() { return nomeCompleto; }
}