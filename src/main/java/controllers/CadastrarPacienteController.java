package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.net.URL;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
// ADICIONADO: Import do CheckBox
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import services.AuthServicePaciente;
import services.SessaoUsuario;

public class CadastrarPacienteController {

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

    private boolean isPacienteCorridaView;

    private final AuthServicePaciente authService;

    public CadastrarPacienteController() {
        authService = new AuthServicePaciente();
        isPacienteCorridaView = false;
    }

    public void setIsPacienteCorridaView(boolean isPacienteCorridaView) {
        this.isPacienteCorridaView = isPacienteCorridaView;
    }

    @FXML
    public void initialize() {
        saveButton.setDefaultButton(true);
        genderComboBox.setItems(FXCollections.observableArrayList("Feminino", "Masculino", "Outro"));
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

            // ADICIONADO: Captura o valor do CheckBox (true se marcado, false se não)
            boolean isPacienteCorrida = pacienteCorridaCheckBox.isSelected();
            
            // ADICIONADO: Passa o valor do CheckBox para o método do serviço
            String resultado = authService.cadastrar(idUsuarioLogado, nome, cpf, genero, telefone, email, dataNascimento, isPacienteCorrida);

            if (resultado.isEmpty()) {
                mensagemLabel.setText("Paciente cadastrado com sucesso!");
                mensagemLabel.setStyle("-fx-text-fill: green;");
                // Você pode adicionar um código para limpar os campos aqui, se desejar
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
            if(isPacienteCorridaView == true) {
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