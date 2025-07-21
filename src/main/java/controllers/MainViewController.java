package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Usuario;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell; 
import java.util.List; 

import java.net.URL;

import db.PacienteDAO;
import models.Paciente;
import services.SessaoUsuario;

public class MainViewController {

    @FXML private TableView<Paciente> patientTable;
    @FXML private TableColumn<Paciente, Integer> idColumn;
    @FXML private TableColumn<Paciente, String> nameColumn;
    @FXML private TableColumn<Paciente, String> cpfColumn;
    @FXML private TableColumn<Paciente, LocalDate> dobColumn;
    @FXML private TableColumn<Paciente, String> genderColumn;
    @FXML private TableColumn<Paciente, String> phoneColumn;
    @FXML private TableColumn<Paciente, String> emailColumn;
    @FXML private Button newPacienteButton;
    @FXML private Button logoutButton;

    private PacienteDAO pacienteDAO;
    private ObservableList<Paciente> patientList;

    // Método initialize é chamado automaticamente após o FXML ser carregado
    @FXML
    public void initialize() {
        pacienteDAO = new PacienteDAO();
        patientList = FXCollections.observableArrayList();

        // Configura as colunas da TableView para mapear as propriedades do objeto Paciente
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        cpfColumn.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("genero"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Formata a coluna de data de nascimento (LocalDate)
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        dobColumn.setCellFactory(column -> new TableCell<Paciente, LocalDate>() { // <<<<<<<< CORRIGIDO: Tipo genérico do TableCell
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) { // <<<<<<<< CORRIGIDO: Assinatura do método
                super.updateItem(item, empty); // <<<<<<<< CORRIGIDO: Chamar o super.updateItem
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // Associa a lista observável à TableView
        patientTable.setItems(patientList);

        // Carrega os pacientes quando a view é inicializada
        // É uma boa prática carregar em um Platform.runLater para evitar problemas de threading
        Platform.runLater(() -> loadPatients());
    }

    /**
     * Carrega a lista de pacientes do banco de dados e atualiza a TableView.
     */
    @FXML
    private void loadPatients() {
        SessaoUsuario sessao = SessaoUsuario.getInstance();

        if (sessao.isLogado()) {
            Usuario usuarioLogado = sessao.getUsuarioLogado();
            int loggedInUserId = usuarioLogado.getId();

            List<Paciente> pacientesDoUsuario = pacienteDAO.findByUsuarioId(loggedInUserId);
            
            patientList.clear();
            patientList.addAll(pacientesDoUsuario); 
            
            System.out.println("Pacientes carregados para o usuário ID " + loggedInUserId + ": " + patientList.size());

        } else {
            patientList.clear();
            System.err.println("Nenhum usuário logado encontrado. A lista de pacientes não pode ser carregada.");
        }
    }

    /**
     * Lida com a ação do botão "Novo Paciente".
     * Aqui você abriria uma nova janela/modal para o cadastro de um novo paciente.
     */
    @FXML
    private void handleNewPatient() {
        System.out.println("Botão 'Novo Paciente' clicado! Abrindo janela de cadastro...");
        
        try {
            Stage stage = (Stage) newPacienteButton.getScene().getWindow();
            // Correção aqui:
            URL fxmlUrl = getClass().getResource("/static/cadastrar_paciente.fxml");
            Parent cadastrarPacienteView = FXMLLoader.load(fxmlUrl);

            stage.setScene(new Scene(cadastrarPacienteView, 1280, 720));
            stage.setTitle("SoftFisio - Cadastrar Paciente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstance().logout();
        System.out.println("Usuario deslogou!");

        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            // Correção aqui:
            URL fxmlUrl = getClass().getResource("/static/login.fxml");
            Parent loginView = FXMLLoader.load(fxmlUrl);
            
            stage.setScene(new Scene(loginView, 1280, 720));
            stage.setTitle("SoftFisio - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Métodos para outras ações (editar, deletar paciente) seriam adicionados aqui
    // Ex: handleEditPatient(), handleDeletePatient()
}