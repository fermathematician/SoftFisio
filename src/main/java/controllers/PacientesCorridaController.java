package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node; 
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField; 
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL; 
import java.util.List;

import db.PacienteDAO;
import models.Paciente;
import models.Usuario;
import services.SessaoUsuario;

public class PacientesCorridaController implements PatientCardController.OnPatientDeletedListener {

    @FXML private Label userNameLabel;
    @FXML private Button logoutButton;
    @FXML private Button newPatientButton;
    @FXML private TextField searchField; 
    @FXML private TilePane patientTilePane; 
    @FXML private Button verComunsButton;

    private PacienteDAO pacienteDAO;

    @FXML
    public void initialize() {
        pacienteDAO = new PacienteDAO();
        
        SessaoUsuario sessao = SessaoUsuario.getInstance();
        if (sessao.isLogado()) {
            userNameLabel.setText(sessao.getUsuarioLogado().getNomeCompleto());
        } else {
            userNameLabel.setText("Usuário Desconhecido");
        }

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPatients(newValue);
        });

        Platform.runLater(this::loadPatients);
    }

    @FXML
    private void loadPatients() {
        patientTilePane.getChildren().clear();

        SessaoUsuario sessao = SessaoUsuario.getInstance();
        if (sessao.isLogado()) {
            Usuario usuarioLogado = sessao.getUsuarioLogado();
            // Lógica principal alterada: Carregando pacientes de corrida (true)
            List<Paciente> pacientesDoUsuario = pacienteDAO.findByUsuarioId(usuarioLogado.getId(), true);

            for (Paciente paciente : pacientesDoUsuario) {
                try {
                    URL fxmlUrl = getClass().getResource("/static/patient_card.fxml");
                    if (fxmlUrl == null) {
                        System.err.println("ERRO CRÍTICO: Não foi possível encontrar 'patient_card.fxml'.");
                        continue;
                    }

                    FXMLLoader loader = new FXMLLoader(fxmlUrl);
                    VBox cardNode = loader.load();

                    cardNode.setUserData(paciente);

                    PatientCardController cardController = loader.getController();
                    cardController.setData(paciente);
                    
                    cardController.setOnPatientDeletedListener(this);

                    patientTilePane.getChildren().add(cardNode);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Cards de pacientes de corrida carregados: " + pacientesDoUsuario.size());
        } else {
            System.err.println("Nenhum usuário logado. Nenhum card para carregar.");
        }
    }
    
    private void filterPatients(String query) {
        String lowerCaseQuery = query.toLowerCase().trim();

        for (Node node : patientTilePane.getChildren()) {
            Paciente paciente = (Paciente) node.getUserData();
            
            if (paciente != null) {
                boolean isVisible = paciente.getNomeCompleto().toLowerCase().contains(lowerCaseQuery);
                
                node.setVisible(isVisible);
                node.setManaged(isVisible);
            }
        }
    }

    @FXML
    private void handleVerComuns() {
        try {
            URL fxmlUrl = getClass().getResource("/static/main_view.fxml");
            Parent mainView = FXMLLoader.load(fxmlUrl);
            
            Stage stage = (Stage) verComunsButton.getScene().getWindow();
            stage.setScene(new Scene(mainView, 1280, 720));
            stage.setTitle("SoftFisio - Meus Pacientes");
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela principal. Verifique se o arquivo 'main_view.fxml' existe.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewPatient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/cadastrar_paciente.fxml"));
            Parent cadastrarPacienteView = loader.load();

            Stage stage = (Stage) newPatientButton.getScene().getWindow();
            stage.setScene(new Scene(cadastrarPacienteView)); 
            stage.setTitle("SoftFisio - Cadastrar Paciente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstance().logout();
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Parent loginView = FXMLLoader.load(getClass().getResource("/static/login.fxml"));
            stage.setScene(new Scene(loginView, 1280, 720));
            stage.setTitle("SoftFisio - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onPatientDeleted(Paciente paciente) {
        loadPatients();
    }
}