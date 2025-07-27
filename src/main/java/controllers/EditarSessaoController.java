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

public class EditarSessaoController {
    @FXML private Label patientNameLabel;
    @FXML private Button backButton;
    @FXML private Label sessionInfoLabel;
    @FXML private TextArea editSessionTextArea;
    @FXML private Label mensagemLabel;

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
    public void initData(Sessao sessao, Paciente paciente) {
        this.sessao = sessao;
        this.paciente = paciente;

        // Preenche o cabeçalho
        patientNameLabel.setText(paciente.getNomeCompleto());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Sessão de' dd 'de' MMMM 'de' yyyy");
        sessionInfoLabel.setText("Editando " + sessao.getDataSessao().format(formatter));

        // Ponto principal: preenche a TextArea com o texto existente
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

    @FXML
    private void handleUpdateSessao() {
        // Lógica para pegar o texto editado e salvar no banco de dados
        String textoAtualizado = editSessionTextArea.getText();
        
        String resultado = prontuarioService.atualizarSessao(sessao.getId(), sessao.getIdPaciente(), sessao.getDataSessao(), textoAtualizado, sessao.getObservacoesSessao());

        if (resultado.isEmpty()) {
            mensagemLabel.setText("Sessão editada com sucesso!");
            mensagemLabel.setStyle("-fx-text-fill: green;");
        } else {
            mensagemLabel.setText(resultado); // Exibe a mensagem de erro vinda do serviço
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
