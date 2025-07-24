package models;

import java.time.LocalDate; 
import java.time.LocalDateTime; 

public class Paciente {
    private int id;
    private int idUsuario;
    private String nomeCompleto;
    private String cpf; // CPF do paciente (opcional, mas recomendado para unicidade)
    private String genero; // Gênero do paciente
    private String telefone; // Telefone de contato
    private String email; // Email do paciente
    private LocalDate dataNascimento; // Data de nascimento do paciente
    private LocalDateTime dataCadastro; // Data e hora do cadastro do paciente no sistema
    private boolean pacienteCorrida;

    // Construtor
    public Paciente(int id, int idUsuario, String nomeCompleto, String cpf, String genero,
                    String telefone, String email, LocalDate dataNascimento, boolean pacienteCorrida) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.genero = genero;
        this.telefone = telefone;
        this.email = email;
        this.dataCadastro = LocalDateTime.now(); // Define a data de cadastro como o momento atual
        this.pacienteCorrida = pacienteCorrida;
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
    public boolean isPacienteCorrida() { return pacienteCorrida; }

}