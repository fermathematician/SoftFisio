package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node; 
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // Import adicionado
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
import services.NavigationService;
import services.SessaoUsuario;
import javafx.stage.Modality;

public class PacientesCorridaController implements PatientCardController.OnPatientDeletedListener {

    // MODIFICAÇÃO: Adicionados FXML Fields para o ScrollPane e o Label da mensagem
    @FXML private ScrollPane scrollPane;
    @FXML private Label emptyMessageLabel;

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
            // Lógica principal: Carregando pacientes de corrida (true)
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
        
        // MODIFICAÇÃO: Chama o método para atualizar a visibilidade
        updateViewVisibility();
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
        // MODIFICAÇÃO: Chama o método para atualizar a visibilidade após filtrar
        updateViewVisibility();
    }

    /**
     * MODIFICAÇÃO: Novo método para gerenciar a visibilidade da lista ou da mensagem.
     */
    private void updateViewVisibility() {
        boolean isListInitiallyEmpty = patientTilePane.getChildren().isEmpty();

        if (isListInitiallyEmpty) {
            emptyMessageLabel.setText("Não há nenhum paciente de corrida cadastrado!");
            scrollPane.setVisible(false);
            emptyMessageLabel.setVisible(true);
            emptyMessageLabel.setManaged(true);
            return;
        }

        // Verifica se algum item está visível após a filtragem
        long visibleCount = patientTilePane.getChildren().stream().filter(Node::isVisible).count();

        if (visibleCount == 0) {
            emptyMessageLabel.setText("Nenhum paciente encontrado para a sua busca.");
            scrollPane.setVisible(false);
            emptyMessageLabel.setVisible(true);
            emptyMessageLabel.setManaged(true);
        } else {
            scrollPane.setVisible(true);
            emptyMessageLabel.setVisible(false);
            emptyMessageLabel.setManaged(false);
        }
    }

    @FXML
    private void handleVerComuns() {
        try {
            String fxmlPath = NavigationService.getInstance().getPreviousPage();

            Parent mainView = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) verComunsButton.getScene().getWindow();
            stage.setScene(new Scene(mainView, 1280, 720));
            stage.setTitle("SoftFisio - LIsta de pacientes");
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela principal. Verifique se o arquivo 'main_view.fxml' existe.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewPatient() {
        try {
            String fxmlPath = "/static/formulario_paciente.fxml";
            // Se você já renomeou os arquivos, use a linha abaixo:
            // String fxmlPath = "/static/formulario_paciente.fxml";
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            FormularioPacienteController controller = loader.getController();
            // Se você já renomeou, o nome da classe será FormularioPacienteController
            // FormularioPacienteController controller = loader.getController();
            
            controller.initData();

            // --- LÓGICA DE NOVA JANELA ---
            Stage stage = new Stage();
            stage.setTitle("SoftFisio - Cadastrar Paciente");
            stage.setScene(new Scene(root, 1280, 720));

            stage.initOwner(newPatientButton.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            
            // Remove a necessidade do NavigationService aqui
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstance().logout();
        try {   
            String fxmlPath = NavigationService.getInstance().getPreviousPage();
            fxmlPath = NavigationService.getInstance().getPreviousPage();

            Parent loginView = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
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