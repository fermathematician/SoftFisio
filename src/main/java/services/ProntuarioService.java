package services;

import java.time.LocalDateTime;
import java.util.List;
import db.SessaoDAO;
import models.Sessao;
import db.AvaliacaoDAO;
import models.Avaliacao;

import models.Anexo;
import db.AnexoDAO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ProntuarioService {

    private SessaoDAO sessaoDAO;
    private AvaliacaoDAO avaliacaoDAO;
    private AnexoDAO anexoDAO;

    public ProntuarioService() {
        this.sessaoDAO = new SessaoDAO();
        this.avaliacaoDAO = new AvaliacaoDAO();
        this.anexoDAO = new AnexoDAO();
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

    public String atualizarSessao(int idSessao, int idPaciente, LocalDateTime dataSessao, String evolucaoTexto, String observacoes) {
        if(evolucaoTexto == null || evolucaoTexto.trim().isEmpty()) {
            return "O campo 'Evolucao da sessao é obrigatório";
        }

        Sessao sessaoAtualizada = new Sessao(idSessao, idPaciente, dataSessao, evolucaoTexto, observacoes);
        boolean sucesso = sessaoDAO.update(sessaoAtualizada);

        return sucesso ? "" : "Ocorreu um erro inesperado ao atualizar a sessão no banco de dados";
    }

    public String deletarSessao(int idSessao) {
        if(idSessao <= 0) {
            return "O id da sessão é inválido";
        }

        boolean sucesso = sessaoDAO.delete(idSessao);

        return sucesso ? "" : "Não foi possivel deletar a sessao do banco de dados";
    }

    // --- NOVOS MÉTODOS PARA AVALIAÇÃO ---

/**
 * Valida e cadastra uma nova avaliação para um paciente.
 * @param idPaciente O ID do paciente.
 * @param queixaPrincipal A queixa principal do paciente.
 * @param historicoDoencaAtual O histórico da doença.
 * @param examesFisicos Os exames físicos realizados.
 * @param diagnosticoFisioterapeutico O diagnóstico fisioterapêutico.
 * @param planoTratamento O plano de tratamento proposto.
 * @return Uma string vazia em caso de sucesso, ou uma mensagem de erro.
 */
public String cadastrarAvaliacao(int idPaciente, String queixaPrincipal, String historicoDoencaAtual, String examesFisicos, String diagnosticoFisioterapeutico, String planoTratamento) {
    // Validação de exemplo: a queixa principal não pode ser vazia.
    // Você pode adicionar outras regras aqui.
    if (queixaPrincipal == null || queixaPrincipal.trim().isEmpty()) {
        return "O campo 'Queixa Principal' é obrigatório na avaliação.";
    }

    Avaliacao novaAvaliacao = new Avaliacao(
        0, idPaciente, LocalDateTime.now(), queixaPrincipal, historicoDoencaAtual,
        examesFisicos, diagnosticoFisioterapeutico, planoTratamento
    );

    boolean sucesso = avaliacaoDAO.save(novaAvaliacao);
    return sucesso ? "" : "Ocorreu um erro ao salvar a avaliação no banco de dados.";
}

/**
 * Busca a lista de todas as avaliações de um paciente.
 * @param idPaciente O ID do paciente.
 * @return A lista de objetos Avaliacao.
 */
public List<Avaliacao> getAvaliacoes(int idPaciente) {
    return avaliacaoDAO.findByPacienteId(idPaciente);
}
public String deletarAvaliacao(int idAvaliacao) {
        if (idAvaliacao <= 0) {
            return "ID da avaliação inválido.";
        }
        boolean sucesso = avaliacaoDAO.delete(idAvaliacao);
        return sucesso ? "" : "Ocorreu um erro ao deletar a avaliação.";
    }

public String atualizarAvaliacao(int idAvaliacao, int idPaciente, LocalDateTime dataAvaliacao, String queixaPrincipal, 
     String historicoDoencaAtual, String examesFisicos, 
     String diagnosticoFisioterapeutico, String planoTratamento) {
        
        if (queixaPrincipal == null || queixaPrincipal.trim().isEmpty()) {
            return "O campo 'Queixa Principal' é obrigatório na avaliação.";
        }

        Avaliacao avaliacaoAtualizada = new Avaliacao(
            idAvaliacao, idPaciente, dataAvaliacao, queixaPrincipal, historicoDoencaAtual,
            examesFisicos, diagnosticoFisioterapeutico, planoTratamento
        );

        boolean sucesso = avaliacaoDAO.update(avaliacaoAtualizada);
        return sucesso ? "" : "Ocorreu um erro ao atualizar a avaliação no banco de dados.";
    }

    // --- MÉTODOS PARA ANEXOS ---

public List<Anexo> getAnexos(int idPaciente) {
    return anexoDAO.findByPacienteId(idPaciente);
}

public String adicionarAnexo(int idPaciente, File arquivoOrigem, String descricao, Integer idSessao, Integer idAvaliacao) {
        if (arquivoOrigem == null || !arquivoOrigem.exists()) {
            return "Arquivo de origem inválido.";
        }
        
        // --- NOVA LÓGICA PARA IDENTIFICAR O TIPO DE MÍDIA ---
        String nomeArquivo = arquivoOrigem.getName().toLowerCase();
        String tipoMidia;

        if (nomeArquivo.endsWith(".mp4") || nomeArquivo.endsWith(".mov") || nomeArquivo.endsWith(".avi") || nomeArquivo.endsWith(".mkv")) {
            tipoMidia = "VIDEO";
        } else if (nomeArquivo.endsWith(".png") || nomeArquivo.endsWith(".jpg") || nomeArquivo.endsWith(".jpeg") || nomeArquivo.endsWith(".gif")) {
            tipoMidia = "FOTO";
        } else {
            // Se não for um tipo conhecido, podemos recusar ou salvar como um tipo genérico
            return "Formato de arquivo não suportado.";
        }
        // --- FIM DA NOVA LÓGICA ---

        try {
            String userHome = System.getProperty("user.home");
            Path diretorioDestino = Paths.get(userHome, ".fisioterapia-app", "media", String.valueOf(idPaciente));
            Files.createDirectories(diretorioDestino);

            Path arquivoDestino = diretorioDestino.resolve(arquivoOrigem.getName());
            Files.copy(arquivoOrigem.toPath(), arquivoDestino, StandardCopyOption.REPLACE_EXISTING);
            
            // AGORA USAMOS A VARIÁVEL tipoMidia EM VEZ DE "FOTO"
            Anexo novoAnexo = new Anexo(
                0, idPaciente, arquivoDestino.toString(), tipoMidia, descricao,
                LocalDateTime.now(), idSessao, idAvaliacao
            );

            boolean sucesso = anexoDAO.save(novoAnexo);
            return sucesso ? "" : "Erro ao salvar o registro do anexo no banco de dados.";

        } catch (IOException e) {
            e.printStackTrace();
            return "Erro de I/O ao salvar o arquivo: " + e.getMessage();
        }
    }

public String deletarAnexo(int idAnexo) {
    // 1. Buscar o anexo para obter o caminho do arquivo
    Anexo anexo = anexoDAO.findById(idAnexo);
    if (anexo == null) {
        return "Anexo não encontrado no banco de dados.";
    }

    try {
        // 2. Deletar o arquivo físico do disco
        Path caminhoArquivo = Paths.get(anexo.getCaminhoArquivo());
        Files.deleteIfExists(caminhoArquivo);

        // 3. Deletar o registro do banco de dados
        boolean sucesso = anexoDAO.delete(idAnexo);
        return sucesso ? "" : "Erro ao deletar o registro do anexo do banco de dados.";

    } catch (IOException e) {
        e.printStackTrace();
        return "Erro de I/O ao deletar o arquivo: " + e.getMessage();
    }
    }
}