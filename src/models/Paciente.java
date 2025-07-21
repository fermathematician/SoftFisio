// src/models/Usuario.java
package src.models;

import java.time.LocalDate; // Importa LocalDate para lidar com datas de nascimento
import java.time.LocalDateTime; // Importa LocalDateTime para lidar com a data de cadastro

public class Paciente {
    private int id;
    private String nomeCompleto;
    private int idUsuario;
    private String cpf; // CPF do paciente (opcional, mas recomendado para unicidade)
    private String genero; // Gênero do paciente
    private String telefone; // Telefone de contato
    private String email; // Email do paciente
    private LocalDate dataNascimento; // Data de nascimento do paciente
    private LocalDateTime dataCadastro; // Data e hora do cadastro do paciente no sistema

    // Construtor
    public Paciente(int id, int idUsuario, String nomeCompleto, LocalDate dataNascimento, String cpf, String genero,
                    String telefone, String email) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.genero = genero;
        this.telefone = telefone;
        this.email = email;
        this.dataCadastro = LocalDateTime.now(); // Define a data de cadastro como o momento atual
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public int getIdUsuario() { return idUsuario; }
    public String getCpf() { return cpf; }
    public String getGenero() { return genero; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public LocalDate getDataNascimento() { return dataNascimento; }

}