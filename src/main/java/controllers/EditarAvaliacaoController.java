package controllers;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import models.Avaliacao;
import models.Paciente;
import services.ProntuarioService;

public class EditarAvaliacaoController {

    @FXML private TextArea queixaPrincipalArea;
    @FXML private TextArea hdaArea;
    @FXML private TextArea examesFisicosArea;
    @FXML private TextArea diagnosticoArea;
    @FXML private TextArea planoTratamentoArea;
    @FXML private Button salvarButton;
    @FXML private Button cancelarButton;
    @FXML private Label mensagemLabel;

    private ProntuarioService prontuarioService;
    private Avaliacao avaliacaoAtual;
    private Paciente paciente;

    public EditarAvaliacaoController() {
        this.prontuarioService = new ProntuarioService();
    }

    public void initData(Avaliacao avaliacao, Paciente paciente) {
        this.avaliacaoAtual = avaliacao;
        this.paciente = paciente;
        populateForm();
    }

    private void populateForm() {
        queixaPrincipalArea.setText(avaliacaoAtual.getQueixaPrincipal());
        hdaArea.setText(avaliacaoAtual.getHistoricoDoencaAtual());
        examesFisicosArea.setText(avaliacaoAtual.getExamesFisicos());
        diagnosticoArea.setText(avaliacaoAtual.getDiagnosticoFisioterapeutico());
        planoTratamentoArea.setText(avaliacaoAtual.getPlanoTratamento());
    }

    @FXML
    private void handleSalvarAlteracoes() {
        String resultado = prontuarioService.atualizarAvaliacao(
            avaliacaoAtual.getId(),
            avaliacaoAtual.getIdPaciente(),
            avaliacaoAtual.getDataAvaliacao(),
            queixaPrincipalArea.getText(),
            hdaArea.getText(),
            examesFisicosArea.getText(),
            diagnosticoArea.getText(),
            planoTratamentoArea.getText()
        );

        if (resultado.isEmpty()) {
            setMensagem("Avaliação atualizada com sucesso!", false);
        } else {
            setMensagem(resultado, true);
        }
    }

    @FXML
    private void handleCancelar() {
        voltarParaProntuario();
    }

    private void voltarParaProntuario() {
        try {
            Stage stage = (Stage) cancelarButton.getScene().getWindow();
            URL fxmlUrl = getClass().getResource("/static/prontuario_view.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            ProntuarioViewController controller = loader.getController();
            controller.initData(this.paciente);

            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("SoftFisio - Prontuário de " + this.paciente.getNomeCompleto());
        } catch (IOException e) {
            e.printStackTrace();
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
}