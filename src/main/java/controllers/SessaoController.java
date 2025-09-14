package controllers;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Paciente;
import models.Sessao;
import ui.NavigationManager;
import services.ProntuarioService;
import javafx.scene.layout.StackPane;
import com.jfoenix.controls.JFXDatePicker;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import javafx.scene.web.HTMLEditor;

public class SessaoController {
    @FXML private Label patientNameLabel;
    @FXML private Button backButton;
    @FXML private Label sessionInfoLabel;
    @FXML private StackPane evolutionPlaceholder;
    @FXML private Label mensagemLabel;
    @FXML private JFXDatePicker dataSessaoPicker;
    @FXML private Label sessionTitle;
    

    private Sessao sessao;
    private Paciente paciente;
    private OnHistoryChangedListener historyListener;
    private ProntuarioService prontuarioService;
    private HTMLEditor evolutionEditor;


    /**
     * Método para receber os dados da tela anterior.
     * @param sessao O objeto da sessão a ser editada.
     * @param paciente O paciente dono da sessão (para exibir o nome).
     */
   // Substitua o método initData
    public void initData(Sessao sessao, Paciente paciente, OnHistoryChangedListener listener) {
        this.sessao = sessao;
        this.paciente = paciente;
        this.historyListener = listener; // Linha adicionada
        inicializarEditor(); 

        // Preenche o cabeçalho
        patientNameLabel.setText(paciente.getNomeCompleto());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Sessão de' dd 'de' MMMM 'de' yyyy");
        sessionInfoLabel.setText("Editando " + sessao.getDataSessao().format(formatter));

        sessionTitle.setText("Editar Sessão");

        // Ponto principal: preenche os campos com os dados existentes
        dataSessaoPicker.setValue(sessao.getDataSessao()); // Preenche a data
        evolutionEditor.setHtmlText(sessao.getEvolucaoTexto());
    }

    public void initData(Paciente paciente, OnHistoryChangedListener listener) {
        this.sessao = null;
        this.paciente = paciente;
        this.historyListener = listener;
        inicializarEditor(); 

        patientNameLabel.setText(paciente.getNomeCompleto());
        sessionInfoLabel.setText("Cadastrar Nova Sessão");

        dataSessaoPicker.setValue(LocalDate.now());
        evolutionEditor.setHtmlText("");
        // Você precisará adicionar um fx:id="saveButton" ao seu botão de salvar no FXML para a linha abaixo funcionar
        // saveButton.setText("Salvar Sessão"); 
    }

    @FXML
    public void initialize() {
        this.prontuarioService = new ProntuarioService();
        this.sessao = null; 
    }

    @FXML
    public void handleBackButton() {
        try {
            String fxmlPath = NavigationManager.getInstance().getPreviousPage();

            URL fxmlUrl = getClass().getResource(fxmlPath);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent prontuarioView = loader.load();

            ProntuarioViewController controller = loader.getController();
        
            controller.initData(this.paciente);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(prontuarioView));
            stage.setMaximized(true);
            stage.setTitle("SoftFisio - Prontuário de " + this.paciente.getNomeCompleto());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Substitua o método handleUpdateSessao
    @FXML
    private void handleUpdateSessao() {
        String textoAtualizado = evolutionEditor.getHtmlText();
        LocalDate novaData = dataSessaoPicker.getValue();
        String resultado;

        // Lógica principal de decisão
        if (sessao == null) { 
            // MODO CRIAÇÃO
            resultado = prontuarioService.cadastrarSessao(
                paciente.getId(),
                novaData,
                textoAtualizado,
                "" // Observações
            );
        } else {
            // MODO EDIÇÃO
            resultado = prontuarioService.atualizarSessao(
                sessao.getId(),
                paciente.getId(),
                novaData,
                textoAtualizado,
                sessao.getObservacoesSessao()
            );
        }

        if (resultado.isEmpty()) {
            if (historyListener != null) {
                historyListener.onHistoryChanged(); // Avisa a tela anterior
            }
            handleBackButton(); // Fecha o formulário
        } else {
            mensagemLabel.setText(resultado);
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void inicializarEditor() {
    if (evolutionEditor == null) {
        evolutionEditor = new HTMLEditor();
        evolutionPlaceholder.getChildren().add(evolutionEditor);
    }
}
}
