package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.Paciente;
import services.ProntuarioService;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;


public class AvaliacaoViewController {

    @FXML private TextArea queixaPrincipalArea;
    @FXML private TextArea hdaArea;
    @FXML private TextArea examesFisicosArea;
    @FXML private TextArea diagnosticoArea;
    @FXML private TextArea planoTratamentoArea;
    @FXML private Button salvarButton;
    @FXML private Label mensagemLabel;
    @FXML private DatePicker dataAvaliacaoPicker;

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

        LocalDate dataSelecionada = dataAvaliacaoPicker.getValue(); // Pega a data

        // Passa a data selecionada para o serviço
        String resultado = prontuarioService.cadastrarAvaliacao(
            pacienteAtual.getId(),
            dataSelecionada, // Novo parâmetro
            queixaPrincipalArea.getText(),
            hdaArea.getText(),
            examesFisicosArea.getText(),
            diagnosticoArea.getText(),
            planoTratamentoArea.getText()
        );

        if (resultado.isEmpty()) {
            setMensagem("Avaliação salva com sucesso!", false);
            limparCampos(); // Limpa os campos após salvar

            if (historyListener != null) {
                historyListener.onHistoryChanged();
            }
        } else {
            setMensagem(resultado, true);
        }
    }

    // Adicione este método à classe
    @FXML
    public void initialize() {
        // Define a data de hoje como padrão
        dataAvaliacaoPicker.setValue(LocalDate.now());
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
    
    // Adicione este método à classe
    private void limparCampos() {
        dataAvaliacaoPicker.setValue(LocalDate.now());
        queixaPrincipalArea.clear();
        hdaArea.clear();
        examesFisicosArea.clear();
        diagnosticoArea.clear();
        planoTratamentoArea.clear();
    }
}