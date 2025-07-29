package controllers;

import java.io.IOException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import models.Paciente;
import services.AuthServicePaciente;
import services.MaskService;
import services.NavigationService; 

public class EditarPacienteController {

    @FXML private TextField nameField;
    @FXML private TextField cpfField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker dobPicker;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Label mensagemLabel;
    @FXML private CheckBox pacienteCorridaCheckBox;

    private final AuthServicePaciente authService;
    private Paciente pacienteParaEditar;

    public EditarPacienteController() {
        authService = new AuthServicePaciente();
    }

    @FXML
    public void initialize() {
        saveButton.setDefaultButton(true);
        genderComboBox.setItems(FXCollections.observableArrayList("Feminino", "Masculino", "Outro"));

        // MODIFICAÇÃO: Aplica as máscaras aos campos de texto
        MaskService.applyCpfMask(cpfField);
        MaskService.applyPhoneMask(phoneField);
    }

    public void initData(Paciente paciente) {
        this.pacienteParaEditar = paciente;

        // Preenche todos os campos do formulário com os dados atuais do paciente.
        nameField.setText(paciente.getNomeCompleto());
        cpfField.setText(paciente.getCpf());
        genderComboBox.setValue(paciente.getGenero());
        phoneField.setText(paciente.getTelefone());
        emailField.setText(paciente.getEmail());
        dobPicker.setValue(paciente.getDataNascimento());
        pacienteCorridaCheckBox.setSelected(paciente.isPacienteCorrida());
    }

    @FXML
    private void handleSave() {
        String nome = nameField.getText();
        String cpf = cpfField.getText();
        String genero = genderComboBox.getValue();
        String telefone = phoneField.getText();
        String email = emailField.getText();
        LocalDate dataNascimento = dobPicker.getValue();
        boolean isPacienteCorrida = pacienteCorridaCheckBox.isSelected();
        
        String resultado = authService.atualizar(pacienteParaEditar.getId(), nome, cpf, genero, telefone, email, dataNascimento, isPacienteCorrida);

        if (resultado.isEmpty()) {
            mensagemLabel.setText("Paciente editado com sucesso!");
            mensagemLabel.setStyle("-fx-text-fill: green;");
        } else {
            mensagemLabel.setText(resultado);
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    } 

    @FXML
    private void handleCancel() {
        try {
            String fxmlPath = NavigationService.getInstance().getPreviousPage();

            Parent patientsView = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(patientsView, 1280, 720));

            if(fxmlPath.equals("/static/main_view.fxml")) {
                stage.setTitle("SoftFisio - Lista de Pacientes");
            }else {
                stage.setTitle("SoftFisio - Pacientes de corrida");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}