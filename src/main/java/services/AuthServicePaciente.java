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


    /**
     * Valida e atualiza os dados de um paciente existente.
     * @param idPaciente O ID original do paciente que está sendo editado.
     * @param nomeCompleto O novo nome do paciente.
     * @param cpf O novo CPF do paciente.
     * @param genero O novo gênero do paciente.
     * @param telefone O novo telefone do paciente.
     * @param email O novo email do paciente.
     * @param dataNascimento A nova data de nascimento do paciente.
     * @return Uma string vazia em caso de sucesso, ou uma mensagem de erro.
     */
    public String atualizar(int idPaciente, String nomeCompleto, String cpf, String genero, String telefone, String email, LocalDate dataNascimento) {
        // 1. Validação dos campos (a mesma do cadastro)
        if (nomeCompleto.isEmpty()) {
            return "O nome precisa ser preenchido!";
        }
        if (cpf.isEmpty()) {
            return "O cpf precisa ser preenchido!";
        }
        if (genero == null) {
            return "O gênero precisa ser selecionado!";
        }
        if (telefone.isEmpty()) {
            return "O telefone precisa ser preenchido!";
        }
        if (email.isEmpty()) {
            return "O email precisa ser preenchido!";
        }
        if (dataNascimento == null) {
            return "A data de nascimento precisa ser selecionada!";
        }

        // 2. Precisamos saber qual usuário está fazendo a alteração.
        //    Pegamos essa informação da sessão.
        SessaoUsuario sessao = SessaoUsuario.getInstance();
        if (!sessao.isLogado()) {
            return "Erro: Usuário não está logado.";
        }
        int idUsuarioLogado = sessao.getUsuarioLogado().getId();

        // 3. Cria um novo objeto Paciente com os dados atualizados.
        //    É crucial usar o 'idPaciente' original para que o DAO saiba qual registro atualizar.
        Paciente pacienteAtualizado = new Paciente(idPaciente, idUsuarioLogado, nomeCompleto, cpf, genero, telefone, email, dataNascimento);
        
        // 4. Chama o método 'update' do DAO, que já existe.
        boolean sucesso = pacienteDAO.update(pacienteAtualizado);

        // 5. Retorna o resultado para o controlador.
        return sucesso ? "" : "Não foi possível atualizar. O CPF informado pode já pertencer a outro paciente.";
    } 
}
