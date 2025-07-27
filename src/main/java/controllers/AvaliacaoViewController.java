package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.Paciente;
import services.ProntuarioService;

public class AvaliacaoViewController {

    @FXML private TextArea queixaPrincipalArea;
    @FXML private TextArea hdaArea;
    @FXML private TextArea examesFisicosArea;
    @FXML private TextArea diagnosticoArea;
    @FXML private TextArea planoTratamentoArea;
    @FXML private Button salvarButton;
    @FXML private Label mensagemLabel;

    private ProntuarioService prontuarioService;
    private Paciente pacienteAtual;
    private OnHistoryChangedListener historyListener;

    public AvaliacaoViewController() {
        this.prontuarioService = new ProntuarioService();
    }

    public void initData(Paciente paciente, OnHistoryChangedListener listener) {
        this.pacienteAtual = paciente;
        this.historyListener = listener;
        // No futuro, este método também pode carregar uma avaliação existente para edição.
    }

    @FXML
    private void handleSalvarAvaliacao() {
        if (pacienteAtual == null) {
            setMensagem("Erro: Paciente não carregado.", true);
            return;
        }

        String resultado = prontuarioService.cadastrarAvaliacao(
            pacienteAtual.getId(),
            queixaPrincipalArea.getText(),
            hdaArea.getText(),
            examesFisicosArea.getText(),
            diagnosticoArea.getText(),
            planoTratamentoArea.getText()
        );

        if (resultado.isEmpty()) {
            setMensagem("Avaliação salva com sucesso!", false);

            if (historyListener != null) {
                historyListener.onHistoryChanged(); // AVISA O PAI QUE HOUVE MUDANÇA
                }
            // Opcional: Limpar os campos após salvar
            // limparCampos(); 
        } else {
            setMensagem(resultado, true); // Exibe o erro vindo do serviço
        }
    }

    private void setMensagem(String mensagem, boolean isError) {
        mensagemLabel.setText(mensagem);
        mensagemLabel.setVisible(true);
        mensagemLabel.setManaged(true);
        if (isError) {
            mensagemLabel.setStyle("-fx-text-fill: red;");
        } else {
            mensagemLabel.setStyle("-fx-text-fill: green;");
        }
    }
    
    private void limparCampos() {
        queixaPrincipalArea.clear();
        hdaArea.clear();
        examesFisicosArea.clear();
        diagnosticoArea.clear();
        planoTratamentoArea.clear();
    }
}