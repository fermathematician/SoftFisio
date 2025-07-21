// src/controllers/MainViewController.java
package src.controllers;

import db.PacienteDAO;
import src.models.Paciente;
import src.services.SessaoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import src.models.Usuario;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell; // <<<<<<<< ADICIONAR ESTA IMPORTAÇÃO
import java.util.List; // <<<<<<<< ADICIONAR ESTA IMPORTAÇÃO

public class MainViewController {

    @FXML
    private TableView<Paciente> patientTable;
    @FXML
    private TableColumn<Paciente, Integer> idColumn;
    @FXML
    private TableColumn<Paciente, String> nameColumn;
    @FXML
    private TableColumn<Paciente, String> cpfColumn;
    @FXML
    private TableColumn<Paciente, LocalDate> dobColumn;
    @FXML
    private TableColumn<Paciente, String> genderColumn;
    @FXML
    private TableColumn<Paciente, String> phoneColumn;
    @FXML
    private TableColumn<Paciente, String> emailColumn;

    @FXML private Button newPacienteButton;

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
     * Atualmente, está buscando todos os pacientes (como se fosse um admin ou único fisioterapeuta).
     * Em um sistema real com login, você passaria o ID do usuário logado.
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
            Parent cadastrarPacienteView = FXMLLoader.load(new File("static/cadastrar_paciente.fxml").toURI().toURL());
            stage.setScene(new Scene(cadastrarPacienteView, 1280, 720));
            stage.setTitle("SoftFisio - Cadastrar Paciente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Métodos para outras ações (editar, deletar paciente) seriam adicionados aqui
    // Ex: handleEditPatient(), handleDeletePatient()
}