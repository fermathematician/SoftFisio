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
import javafx.scene.web.HTMLEditor;

public class SessaoController {
    @FXML private Label patientNameLabel;
    @FXML private Button backButton;
    @FXML private Label sessionInfoLabel;
    @FXML private HTMLEditor evolutionEditor;
    @FXML private Label mensagemLabel;
    // Adicione esta linha junto aos outros @FXML
    @FXML private JFXDatePicker dataSessaoPicker;

    private Sessao sessao;
    private Paciente paciente;
    private OnHistoryChangedListener historyListener;
    private ProntuarioService prontuarioService;

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

        // Preenche o cabeçalho
        patientNameLabel.setText(paciente.getNomeCompleto());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Sessão de' dd 'de' MMMM 'de' yyyy");
        sessionInfoLabel.setText("Editando " + sessao.getDataSessao().format(formatter));

        // Ponto principal: preenche os campos com os dados existentes
        dataSessaoPicker.setValue(sessao.getDataSessao()); // Preenche a data
        evolutionEditor.setHtmlText(sessao.getEvolucaoTexto());
    }

public void initData(Paciente paciente, OnHistoryChangedListener listener) {
    this.sessao = null;
    this.paciente = paciente;
    this.historyListener = listener;

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
    // Pega a janela atual (a do formulário) e simplesmente a fecha.
    Stage stage = (Stage) backButton.getScene().getWindow();
    stage.close();
}

    // Substitua o método handleUpdateSessao
    @FXML
    private void handleUpdateSessao() {
        String textoAtualizado = evolutionEditor.getHtmlText();
        LocalDate novaData = dataSessaoPicker.getValue();
        String resultado;

        // Lógica principal de decisão
        if (sessao == null) { // ou sessaoParaEditar, dependendo do nome que usar
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
}
