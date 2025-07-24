package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
// 1. ADICIONADO: Import do CheckBox
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import models.Paciente;
import services.AuthServicePaciente;

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

    // 2. ADICIONADO: Referência para o CheckBox
    @FXML private CheckBox pacienteCorridaCheckBox;

    private final AuthServicePaciente authService;
    private Paciente pacienteParaEditar;
    private boolean isPatientCorridaView;

    public EditarPacienteController() {
        authService = new AuthServicePaciente();
        isPatientCorridaView = false;
    }

    @FXML
    public void initialize() {
        saveButton.setDefaultButton(true);
        genderComboBox.setItems(FXCollections.observableArrayList("Feminino", "Masculino", "Outro"));
    }

    public void initData(Paciente paciente, boolean isPacienteCorridaView) {
        this.pacienteParaEditar = paciente;
        this.isPatientCorridaView = isPacienteCorridaView;

        // Preenche todos os campos do formulário com os dados atuais do paciente.
        nameField.setText(paciente.getNomeCompleto());
        cpfField.setText(paciente.getCpf());
        genderComboBox.setValue(paciente.getGenero());
        phoneField.setText(paciente.getTelefone());
        emailField.setText(paciente.getEmail());
        dobPicker.setValue(paciente.getDataNascimento());

        // 3. ADICIONADO: Preenche o CheckBox com o status atual do paciente
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
        
        // 4. ADICIONADO: Captura o valor atualizado do CheckBox
        boolean isPacienteCorrida = pacienteCorridaCheckBox.isSelected();
        
        // 5. ATUALIZADO: Passa o valor do CheckBox (isPacienteCorrida) em vez de 'false'
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
            if(isPatientCorridaView == true) {
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                URL fxmlUrl = getClass().getResource("/static/pacientes_corrida.fxml");

                Parent pacienteCorridaView = FXMLLoader.load(fxmlUrl);
            
                stage.setScene(new Scene(pacienteCorridaView, 1280, 720));
                stage.setTitle("SoftFisio - Lista de pacientes corrida");
            }else {
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                URL fxmlUrl = getClass().getResource("/static/main_view.fxml");

                Parent mainView = FXMLLoader.load(fxmlUrl);
            
                stage.setScene(new Scene(mainView, 1280, 720));
                stage.setTitle("SoftFisio - Lista de pacientes");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}