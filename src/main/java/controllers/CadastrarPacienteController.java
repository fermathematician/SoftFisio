package controllers;

import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import services.AuthServicePaciente;
import services.MaskService;
import services.NavigationService;
import services.SessaoUsuario;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CadastrarPacienteController {

    @FXML private TextField nameField;
    @FXML private TextField cpfField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private JFXDatePicker dobPicker;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Label mensagemLabel;
    @FXML private CheckBox pacienteCorridaCheckBox;

    private final AuthServicePaciente authService;

    public CadastrarPacienteController() {
        authService = new AuthServicePaciente();
    }

    @FXML
    public void initialize() {
        saveButton.setDefaultButton(true);
        genderComboBox.setItems(FXCollections.observableArrayList("Feminino", "Masculino", "Outro"));

        MaskService.applyCpfMask(cpfField);
        MaskService.applyPhoneMask(phoneField);

        configureDatePicker();
    }

    /**
     * Configura o JFXDatePicker para ser editável e ter um formato/validação de data.
     */
    private void configureDatePicker() {
        dobPicker.setEditable(true);

        final String pattern = "dd/MM/yyyy";
        dobPicker.setPromptText(pattern.toLowerCase());

        dobPicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        });
    }


    @FXML
    private void handleSave() {
        SessaoUsuario sessao = SessaoUsuario.getInstance();

        if (sessao.isLogado()) {
            int idUsuarioLogado = sessao.getUsuarioLogado().getId();

            String nome = nameField.getText();
            String cpf = cpfField.getText();
            String genero = genderComboBox.getValue();
            String telefone = phoneField.getText();
            String email = emailField.getText();
            LocalDate dataNascimento = dobPicker.getValue();

            boolean isPacienteCorrida = pacienteCorridaCheckBox.isSelected();

            String resultado = authService.cadastrar(idUsuarioLogado, nome, cpf, genero, telefone, email, dataNascimento, isPacienteCorrida);

            if (resultado.isEmpty()) {
                mensagemLabel.setText("Paciente cadastrado com sucesso!");
                mensagemLabel.setStyle("-fx-text-fill: green;");
            } else {
                mensagemLabel.setText(resultado);
                mensagemLabel.setStyle("-fx-text-fill: red;");
            }

        } else {
            mensagemLabel.setText("Erro: Nenhum usuário autenticado. Faça o login novamente.");
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