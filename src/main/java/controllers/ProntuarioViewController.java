package controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Period;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import models.Paciente;
import ui.NavigationManager;

public class ProntuarioViewController implements OnHistoryChangedListener {

    @FXML private BorderPane prontuarioRoot;
    @FXML private Label patientNameLabel;
    @FXML private Label patientInfoLabel;
    @FXML private Button backButton;
    @FXML private TabPane mainTabPane;
    
    @FXML private TreatmentViewController sessoesTabContentController;
    @FXML private AvaliacaoTabViewController avaliacaoTabViewController;
    @FXML private HistoricoTabViewController historicoTabViewController;
    @FXML private AnexosTabViewController anexosTabViewController;
    
    
    private Paciente pacienteAtual;

    @Override
    public void onHistoryChanged() {
        System.out.println("DEBUG: Evento de atualização recebido. Recarregando abas...");
        if (sessoesTabContentController != null) sessoesTabContentController.loadSessoes();
        if (avaliacaoTabViewController != null) avaliacaoTabViewController.loadAvaliacoes();
        if (historicoTabViewController != null) historicoTabViewController.carregarHistoricoCompleto();
    }

    public void initData(Paciente paciente) {
        this.pacienteAtual = paciente;
        setupHeader();
        
        if (sessoesTabContentController != null) sessoesTabContentController.initData(paciente, this);
        if (avaliacaoTabViewController != null) avaliacaoTabViewController.initData(paciente, this);
        if (historicoTabViewController != null) historicoTabViewController.initData(paciente, this);
        if (anexosTabViewController != null) anexosTabViewController.initData(paciente);
    }

    private void setupHeader() {
        patientNameLabel.setText(pacienteAtual.getNomeCompleto());
        int idade = Period.between(pacienteAtual.getDataNascimento(), LocalDateTime.now().toLocalDate()).getYears();
        patientInfoLabel.setText("CPF: " + pacienteAtual.getCpf() + " | Idade: " + idade + " anos");
    }

    @FXML
    private void handleBackButton() {
        try {
            String fxmlPath = NavigationManager.getInstance().getPreviousPage();
            Parent previousView = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(previousView));
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}