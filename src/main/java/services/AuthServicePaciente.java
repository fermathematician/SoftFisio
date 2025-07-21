package services;

import java.time.LocalDate;

import db.PacienteDAO;
import models.Paciente;

public class AuthServicePaciente {
    private PacienteDAO pacienteDAO;

    public AuthServicePaciente() {
        pacienteDAO = new PacienteDAO();
    }

    public String cadastrar(int idUsuario, String nomeCompleto,  String cpf, String genero, String telefone, String email, LocalDate dataNascimento) {
        if(nomeCompleto.isEmpty()) {
            return "O nome precisa ser preenchido!";
        }
        
        if(cpf.isEmpty()) {
            return "O cpf precisa ser preenchido!";
        }

        if(genero == null) {
            return "O gênero precisa ser selecionado!";
        }

        if(telefone.isEmpty()) {
            return "O telefone precisa ser preenchido!";
        }

        if(email.isEmpty()) {
            return "O email precisa ser preechido!";
        }
       
        if(dataNascimento == null) {
            return "A data de nascimento precisa ser selecionada!";
        }

        Paciente paciente = new Paciente(0, idUsuario, nomeCompleto, cpf, genero, telefone, email, dataNascimento);
        boolean sucesso = pacienteDAO.save(paciente);

        return sucesso ? "" : "Já existe um paciente com o cpf digitado!";
    }
}
