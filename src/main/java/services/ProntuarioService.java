package services;

import java.time.LocalDateTime;
import java.util.List;
import db.SessaoDAO;
import models.Sessao;

public class ProntuarioService {

    private SessaoDAO sessaoDAO;

    public ProntuarioService() {
        this.sessaoDAO = new SessaoDAO();
    }

    /**
     * Valida os dados e cadastra uma nova sessão para um paciente.
     * @param idPaciente O ID do paciente ao qual a sessão pertence.
     * @param evolucaoTexto O texto principal com a evolução da sessão.
     * @param observacoes As observações adicionais.
     * @return Uma string vazia em caso de sucesso, ou uma mensagem de erro.
     */
    public String cadastrarSessao(int idPaciente, String evolucaoTexto, String observacoes) {
        // 1. Validação da regra de negócio (ex: o texto principal não pode ser vazio)
        if (evolucaoTexto == null || evolucaoTexto.trim().isEmpty()) {
            return "O campo 'Evolução da Sessão' é obrigatório.";
        }

        // 2. Cria o objeto Sessao para ser salvo
        //    O ID é 0 porque o banco de dados irá gerá-lo automaticamente (AUTOINCREMENT)
        //    A data é o momento atual.
        Sessao novaSessao = new Sessao(
            0,
            idPaciente,
            LocalDateTime.now(),
            evolucaoTexto.trim(),
            observacoes != null ? observacoes.trim() : "" // Garante que não salve 'null'
        );

        // 3. Pede ao DAO para salvar o objeto no banco de dados
        boolean sucesso = sessaoDAO.save(novaSessao);

        return sucesso ? "" : "Ocorreu um erro inesperado ao salvar a sessão no banco de dados.";
    }

    /**
     * Busca a lista de todas as sessões de um paciente.
     * @param idPaciente O ID do paciente.
     * @return A lista de objetos Sessao.
     */
    public List<Sessao> getSessoes(int idPaciente) {
        // Por enquanto, apenas repassa a chamada para o DAO.
        // No futuro, poderia ter lógicas mais complexas aqui.
        return sessaoDAO.findByPacienteId(idPaciente);
    }

    public String deletarSessao(int idSessao) {
        if(idSessao <= 0) {
            return "O id da sessão é inválido";
        }

        boolean sucesso = sessaoDAO.delete(idSessao);

        return sucesso ? "" : "Não foi possivel deletar a sessao do banco de dados";
    }
}