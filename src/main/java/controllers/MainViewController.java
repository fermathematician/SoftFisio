package controllers;

import db.PacienteDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import models.Paciente;
import models.Usuario;
import services.SessaoUsuario;

public class MainViewController {

    // ADICIONADO: Referências aos novos componentes do FXML
    @FXML private Label userNameLabel;
    @FXML private Button logoutButton;
    @FXML private Button newPatientButton;
    @FXML private TilePane patientTilePane; // O container para os cards

    // REMOVIDO: Todas as referências à TableView e suas colunas

    private PacienteDAO pacienteDAO;

    @FXML
    public void initialize() {
        pacienteDAO = new PacienteDAO();
        
        // Configura o nome do usuário no cabeçalho
        SessaoUsuario sessao = SessaoUsuario.getInstance();
        if (sessao.isLogado()) {
            userNameLabel.setText(sessao.getUsuarioLogado().getNomeCompleto());
        } else {
            userNameLabel.setText("Usuário Desconhecido");
        }

        // Carrega os pacientes quando a view é inicializada
        Platform.runLater(this::loadPatients);
    }

    /**
     * Carrega a lista de pacientes, cria um card para cada um e os exibe no TilePane.
     */
    @FXML
    private void loadPatients() {
        // Limpa os cards existentes antes de carregar novos
        patientTilePane.getChildren().clear();

        SessaoUsuario sessao = SessaoUsuario.getInstance();
        if (sessao.isLogado()) {
            Usuario usuarioLogado = sessao.getUsuarioLogado();
            List<Paciente> pacientesDoUsuario = pacienteDAO.findByUsuarioId(usuarioLogado.getId());

            for (Paciente paciente : pacientesDoUsuario) {
                try {
                    // 1. Cria um FXMLLoader para o nosso card
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/patient_card.fxml"));
                    
                    // 2. Carrega o FXML do card. O resultado é o nó raiz (o VBox do card)
                    VBox cardNode = loader.load();

                    // 3. Pega o controller associado a este card específico
                    PatientCardController cardController = loader.getController();

                    // 4. Passa os dados do paciente para o controller do card
                    cardController.setData(paciente);

                    // 5. Adiciona o card (o VBox) ao nosso TilePane
                    patientTilePane.getChildren().add(cardNode);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Cards de pacientes carregados: " + pacientesDoUsuario.size());
        } else {
            System.err.println("Nenhum usuário logado. Nenhum card para carregar.");
        }
    }

    @FXML
    private void handleNewPatient() {
        try {
            // CORRIGIDO: Usando a forma correta de carregar recursos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/cadastrar_paciente.fxml"));
            Parent cadastrarPacienteView = loader.load();

            Stage stage = (Stage) newPatientButton.getScene().getWindow();
            stage.setScene(new Scene(cadastrarPacienteView)); // Tamanho é definido no FXML
            stage.setTitle("SoftFisio - Cadastrar Paciente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // A lógica de logout que já fizemos antes
        // ... (pode adicionar a caixa de diálogo de confirmação aqui) ...
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
}