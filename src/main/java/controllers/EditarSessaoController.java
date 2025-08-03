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
import models.Paciente;
import models.Sessao;
import services.ProntuarioService;

import javafx.scene.control.DatePicker;
import com.jfoenix.controls.JFXDatePicker;
import java.time.LocalDate;

public class EditarSessaoController {
    @FXML private Label patientNameLabel;
    @FXML private Button backButton;
    @FXML private Label sessionInfoLabel;
    @FXML private TextArea editSessionTextArea;
    @FXML private Label mensagemLabel;
    // Adicione esta linha junto aos outros @FXML
    @FXML private JFXDatePicker dataSessaoPicker;

    private final ProntuarioService prontuarioService;
    private Sessao sessao;
    private Paciente paciente;

    public EditarSessaoController() {
        prontuarioService = new ProntuarioService();
    }

    /**
     * Método para receber os dados da tela anterior.
     * @param sessao O objeto da sessão a ser editada.
     * @param paciente O paciente dono da sessão (para exibir o nome).
     */
   // Substitua o método initData
    public void initData(Sessao sessao, Paciente paciente) {
        this.sessao = sessao;
        this.paciente = paciente;

        // Preenche o cabeçalho
        patientNameLabel.setText(paciente.getNomeCompleto());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Sessão de' dd 'de' MMMM 'de' yyyy");
        sessionInfoLabel.setText("Editando " + sessao.getDataSessao().format(formatter));

        // Ponto principal: preenche os campos com os dados existentes
        dataSessaoPicker.setValue(sessao.getDataSessao()); // Preenche a data
        editSessionTextArea.setText(sessao.getEvolucaoTexto());
    }

@FXML
public void handleBackButton() {
    try {
        // Carrega o FXML da tela de prontuário principal (com abas)
        URL fxmlUrl = getClass().getResource("/static/prontuario_view.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Pega a instância do controlador da tela de prontuário
        ProntuarioViewController controller = loader.getController();

        // Passa o paciente de volta para o controlador do prontuário
        controller.initData(this.paciente);

        // Exibe a cena correta
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle("SoftFisio - Prontuário de " + this.paciente.getNomeCompleto());

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    // Substitua o método handleUpdateSessao
    @FXML
    private void handleUpdateSessao() {
        String textoAtualizado = editSessionTextArea.getText();
        LocalDate novaData = dataSessaoPicker.getValue(); // Pega a nova data

        String resultado = prontuarioService.atualizarSessao(
            sessao.getId(),
            sessao.getIdPaciente(),
            novaData, // Usa a nova data
            textoAtualizado,
            sessao.getObservacoesSessao()
        );

        if (resultado.isEmpty()) {
            mensagemLabel.setText("Sessão editada com sucesso!");
            mensagemLabel.setStyle("-fx-text-fill: green;");

            // Opcional, mas recomendado: atualiza o título com a nova data, se ela mudou
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Sessão de' dd 'de' MMMM 'de' yyyy");
            sessionInfoLabel.setText("Editando " + novaData.format(formatter));

        } else {
            mensagemLabel.setText(resultado);
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
