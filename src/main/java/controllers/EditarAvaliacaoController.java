package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import com.jfoenix.controls.JFXDatePicker;

public class EditarAvaliacaoController {

    // Componentes do Cabeçalho
    @FXML private Label patientNameLabel;
    @FXML private Label evaluationInfoLabel;
    @FXML private Button backButton;

    // Componentes do Formulário
    @FXML private TextArea queixaPrincipalArea;
    @FXML private TextArea hdaArea;
    @FXML private TextArea examesFisicosArea;
    @FXML private TextArea diagnosticoArea;
    @FXML private TextArea planoTratamentoArea;
    @FXML private Button salvarButton;
    @FXML private Label mensagemLabel;
    // Adicione esta linha junto aos outros @FXML
    @FXML private JFXDatePicker dataAvaliacaoPicker;



    private ProntuarioService prontuarioService;
    private Avaliacao avaliacaoAtual;
    private Paciente paciente;

    public EditarAvaliacaoController() {
        this.prontuarioService = new ProntuarioService();
    }

    // Substitua o método initData
    public void initData(Avaliacao avaliacao, Paciente paciente) {
        this.avaliacaoAtual = avaliacao;
        this.paciente = paciente;

        // Preenche o cabeçalho
        patientNameLabel.setText(paciente.getNomeCompleto());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Avaliação de' dd 'de' MMMM 'de' yyyy");
        evaluationInfoLabel.setText("Editando " + avaliacao.getData().format(formatter)); // Use getData() da interface

        // Preenche os campos do formulário
        dataAvaliacaoPicker.setValue(avaliacao.getDataAvaliacao()); // Preenche a data
        populateForm();
    }

    private void populateForm() {
        queixaPrincipalArea.setText(avaliacaoAtual.getQueixaPrincipal());
        hdaArea.setText(avaliacaoAtual.getHistoricoDoencaAtual());
        examesFisicosArea.setText(avaliacaoAtual.getExamesFisicos());
        diagnosticoArea.setText(avaliacaoAtual.getDiagnosticoFisioterapeutico());
        planoTratamentoArea.setText(avaliacaoAtual.getPlanoTratamento());
    }

 // Substitua o método handleSalvarAlteracoes
    @FXML
    private void handleSalvarAlteracoes() {
        LocalDate novaData = dataAvaliacaoPicker.getValue(); // Pega a nova data

        String resultado = prontuarioService.atualizarAvaliacao(
            avaliacaoAtual.getId(),
            avaliacaoAtual.getIdPaciente(),
            novaData, // Usa a nova data
            queixaPrincipalArea.getText(),
            hdaArea.getText(),
            examesFisicosArea.getText(),
            diagnosticoArea.getText(),
            planoTratamentoArea.getText()
        );

        if (resultado.isEmpty()) {
            setMensagem("Avaliação atualizada com sucesso!", false);
            // Atualiza o título com a nova data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Avaliação de' dd 'de' MMMM 'de' yyyy");
            evaluationInfoLabel.setText("Editando " + novaData.format(formatter));
        } else {
            setMensagem(resultado, true);
        }
    }

    @FXML
    private void handleBackButton() {
        voltarParaProntuario();
    }

    private void voltarParaProntuario() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
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
