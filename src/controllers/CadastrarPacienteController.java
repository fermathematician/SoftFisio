package src.controllers;

import java.io.File;
import java.io.IOException;

import db.PacienteDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import src.models.Paciente;

public class CadastrarPacienteController {

    @FXML
    private TextField nameField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField cpfField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private PacienteDAO pacienteDAO;
    private int idUsuarioLogado;

    /**
     * O método initialize é chamado automaticamente pelo JavaFX
     * após o arquivo FXML ter sido completamente carregado.
     * É ideal para configurações iniciais dos componentes.
     */
    @FXML
    public void initialize() {
        pacienteDAO = new PacienteDAO();
        // Preenche o ComboBox com as opções de gênero
        genderComboBox.setItems(FXCollections.observableArrayList("Feminino", "Masculino", "Outro"));
    }

    /**
     * Este método é chamado a partir do MainViewController para passar o ID
     * do fisioterapeuta que está logado no sistema.
     * @param id O ID do usuário logado.
     */
    public void setUsuarioId(int id) {
        this.idUsuarioLogado = id;
    }

    /**
     * Chamado quando o botão "Salvar" é clicado.
     * Valida os campos, cria um novo objeto Paciente e o salva no banco.
     */
    @FXML
    private void handleSave() {
        // 1. Validação simples: verifica se o campo de nome não está vazio.
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Erro de Validação", "O campo 'Nome Completo' é obrigatório.");
            return;
        }

        // 2. Cria um novo objeto Paciente com os dados do formulário.
        // O ID 0 é um placeholder, pois o banco de dados irá gerar o ID real.
        Paciente novoPaciente = new Paciente(
            0,
            idUsuarioLogado,
            nameField.getText(),
            dobPicker.getValue(),
            cpfField.getText(),
            genderComboBox.getValue(),
            phoneField.getText(),
            emailField.getText()
        );

        // 3. Tenta salvar o paciente usando o DAO.
        boolean sucesso = pacienteDAO.save(novoPaciente);

        if (sucesso) {
            showAlert(AlertType.INFORMATION, "Sucesso", "Paciente cadastrado com sucesso!");
            closeWindow();
        } else {
            showAlert(AlertType.ERROR, "Erro no Cadastro", "Não foi possível salvar o paciente. Verifique os dados (um CPF duplicado pode ser a causa) ou contate o suporte.");
        }
    }

    /**
     * Chamado quando o botão "Cancelar" é clicado.
     * Simplesmente fecha a janela de cadastro.
     */
    @FXML
    private void handleCancel() {
        try {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            Parent registerView = FXMLLoader.load(new File("static/main_view.fxml").toURI().toURL());
            stage.setScene(new Scene(registerView, 1280, 720));
            stage.setTitle("SoftFisio - Lista de pacientes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método utilitário para exibir alertas para o usuário.
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void closeWindow() {
        // Pega o "Palco" (Stage) a partir de qualquer componente da cena, como o botão salvar.
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}