package services;

import java.time.LocalDate;
import db.PacienteDAO;
import models.Paciente;

public class AuthServicePaciente {
    private PacienteDAO pacienteDAO;

    public AuthServicePaciente() {
        pacienteDAO = new PacienteDAO();
    }

    /**
     * Valida e cadastra um novo paciente.
     * @param idUsuario O ID do usuário que está cadastrando.
     * @param nomeCompleto O nome do paciente.
     * @param cpf O CPF do paciente.
     * @param genero O gênero do paciente.
     * @param telefone O telefone do paciente.
     * @param email O email do paciente.
     * @param dataNascimento A data de nascimento do paciente.
     * @param isPacienteCorrida Um booleano indicando se é um paciente de corrida.
     * @return Uma string vazia em caso de sucesso, ou uma mensagem de erro.
     */
    public String cadastrar(int idUsuario, String nomeCompleto, String cpf, String genero, String telefone, String email, LocalDate dataNascimento, boolean isPacienteCorrida) {
        // Validações dos campos de entrada
        if (nomeCompleto.isEmpty()) {
            return "O nome precisa ser preenchido!";
        }
        
        if (cpf.isEmpty()) {
            return "O CPF precisa ser preenchido!";
        }
        
        String cpfNumeros = cpf.replaceAll("[^0-9]", "");
        if (cpfNumeros.length() != 11) {
            return "O CPF deve conter 11 dígitos.";
        }

        if (dataNascimento == null) {
            return "A data de nascimento precisa ser selecionada!";
        }

        if (dataNascimento.isAfter(LocalDate.now())) {
            return "A data de nascimento não pode ser uma data futura!";
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

        if (!email.contains("@")) {
            return "O formato do e-mail é inválido.";
        }

        // Cria o objeto Paciente
        Paciente paciente = new Paciente(0, idUsuario, nomeCompleto, cpf, genero, telefone, email, dataNascimento, isPacienteCorrida);
        
        boolean sucesso = pacienteDAO.save(paciente);

        return sucesso ? "" : "Já existe um paciente com o CPF digitado!";
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
     * @param isPacienteCorrida O novo status de paciente de corrida.
     * @return Uma string vazia em caso de sucesso, ou uma mensagem de erro.
     */
    public String atualizar(int idPaciente, String nomeCompleto, String cpf, String genero, String telefone, String email, LocalDate dataNascimento, boolean isPacienteCorrida) {
        // 1. Validação dos campos
        if (nomeCompleto.isEmpty()) {
            return "O nome precisa ser preenchido!";
        }

        if (cpf.isEmpty()) {
            return "O CPF precisa ser preenchido!";
        }

        String cpfNumeros = cpf.replaceAll("[^0-9]", "");
        if (cpfNumeros.length() != 11) {
            return "O CPF deve conter 11 dígitos.";
        }

        if (dataNascimento == null) {
            return "A data de nascimento precisa ser selecionada!";
        }

        if (dataNascimento.isAfter(LocalDate.now())) {
            return "A data de nascimento não pode ser uma data futura!";
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

        if (!email.contains("@")) {
            return "O formato do e-mail é inválido.";
        }    

        // 2. Obter o usuário logado
        SessaoUsuario sessao = SessaoUsuario.getInstance();
        if (!sessao.isLogado()) {
            return "Erro: Usuário não está logado.";
        }
        int idUsuarioLogado = sessao.getUsuarioLogado().getId();

        // 3. Cria o objeto Paciente com os dados atualizados
        Paciente pacienteAtualizado = new Paciente(idPaciente, idUsuarioLogado, nomeCompleto, cpf, genero, telefone, email, dataNascimento, isPacienteCorrida);
        
        // 4. Chama o método 'update' do DAO
        boolean sucesso = pacienteDAO.update(pacienteAtualizado);

        // 5. Retorna o resultado
        return sucesso ? "" : "Não foi possível atualizar. O CPF informado pode já pertencer a outro paciente.";
    } 

    /**
     * Valida e solicita a exclusão de um paciente.
     * @param idPaciente O ID do paciente a ser deletado.
     * @return Uma string vazia em caso de sucesso, ou uma mensagem de erro.
     */
    public String deletar(int idPaciente) {
        if (idPaciente <= 0) {
            return "ID do paciente inválido.";
        }

        boolean sucesso = pacienteDAO.delete(idPaciente);
        return sucesso ? "" : "Não foi possível deletar o paciente do banco de dados.";
    }
}