package controllers;

import java.io.IOException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import models.Paciente;
import services.AuthServicePaciente;
import services.SessaoUsuario;

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

    private final AuthServicePaciente authService;
    private Paciente pacienteParaEditar;

    public EditarPacienteController() {
        authService = new AuthServicePaciente();
    }

    /**
     * O método initialize é chamado automaticamente pelo JavaFX
     * após o arquivo FXML ter sido completamente carregado.
     * É ideal para configurações iniciais dos componentes.
     */
    @FXML
    public void initialize() {
        saveButton.setDefaultButton(true);
        genderComboBox.setItems(FXCollections.observableArrayList("Feminino", "Masculino", "Outro"));
    }

        /**
     * NOVO MÉTODO: Recebe o paciente do card e preenche o formulário.
     * Este método será chamado pelo PatientCardController antes de exibir a tela.
     * @param paciente O paciente cujos dados devem ser editados.
     */
    public void initData(Paciente paciente) {
        this.pacienteParaEditar = paciente;

        // Preenche todos os campos do formulário com os dados atuais do paciente.
        nameField.setText(paciente.getNomeCompleto());
        cpfField.setText(paciente.getCpf());
        genderComboBox.setValue(paciente.getGenero());
        phoneField.setText(paciente.getTelefone());
        emailField.setText(paciente.getEmail());
        dobPicker.setValue(paciente.getDataNascimento());
    }


    /**
     * Chamado quando o botão "Salvar" é clicado.
     * Valida os campos, cria um novo objeto Paciente e o salva no banco.
     */
    @FXML
    private void handleSave() {
 
            String nome = nameField.getText();
            String cpf = cpfField.getText();
            String genero = genderComboBox.getValue();
            String telefone = phoneField.getText();
            String email = emailField.getText();
            LocalDate dataNascimento = dobPicker.getValue();
            
            String resultado = authService.atualizar(pacienteParaEditar.getId(), nome, cpf, genero, telefone, email, dataNascimento);

            if (resultado.isEmpty()) {
                mensagemLabel.setText("Paciente editado com sucesso!");
                mensagemLabel.setStyle("-fx-text-fill: green;");
            } else {
                mensagemLabel.setText(resultado); // Exibe a mensagem de erro vinda do serviço
                mensagemLabel.setStyle("-fx-text-fill: red;");
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
            // Correção aqui:
            URL fxmlUrl = getClass().getResource("/static/main_view.fxml");
            Parent registerView = FXMLLoader.load(fxmlUrl);
            
            stage.setScene(new Scene(registerView, 1280, 720));
            stage.setTitle("SoftFisio - Lista de pacientes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}