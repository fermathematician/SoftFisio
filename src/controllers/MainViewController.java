// src/controllers/MainViewController.java
package src.controllers;

import db.PacienteDAO;
import src.models.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
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
        // Exemplo: Usando um ID de usuário fictício (1) para carregar pacientes.
        // Em um cenário real, o ID do usuário logado viria de algum serviço de autenticação.
        int loggedInUserId = 1; // Substitua pelo ID do usuário logado

        List<Paciente> pacientesDoUsuario = pacienteDAO.findByUsuarioId(loggedInUserId);
        patientList.clear(); // Limpa a lista antes de adicionar os novos dados
        patientList.addAll(pacientesDoUsuario); // Adiciona todos os pacientes encontrados
        System.out.println("Pacientes carregados: " + patientList.size()); // Log para depuração
    }

    /**
     * Lida com a ação do botão "Novo Paciente".
     * Aqui você abriria uma nova janela/modal para o cadastro de um novo paciente.
     */
    @FXML
    private void handleNewPatient() {
        System.out.println("Botão 'Novo Paciente' clicado!");
        // Exemplo: Para abrir uma nova janela, você faria algo assim:
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/new_patient_view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Novo Paciente");
            stage.initModality(Modality.APPLICATION_MODAL); // Torna a janela modal
            stage.showAndWait(); // Espera a janela ser fechada

            // Após fechar a janela de novo paciente, recarrega a lista
            loadPatients();

        } catch (IOException e) {
            e.printStackTrace();
            // Lidar com o erro ao carregar a nova view
        }
        */
    }

    // Métodos para outras ações (editar, deletar paciente) seriam adicionados aqui
    // Ex: handleEditPatient(), handleDeletePatient()
}