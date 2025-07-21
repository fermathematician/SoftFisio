package src.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import src.services.AuthServicePaciente;
import src.services.SessaoUsuario;
import src.models.Paciente;

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

    private int idUsuarioLogado;

    private final AuthServicePaciente authService;

    public CadastrarPacienteController() {
        authService = new AuthServicePaciente();
    }

    /**
     * O método initialize é chamado automaticamente pelo JavaFX
     * após o arquivo FXML ter sido completamente carregado.
     * É ideal para configurações iniciais dos componentes.
     */
    @FXML
    public void initialize() {
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
        SessaoUsuario sessao = SessaoUsuario.getInstance();

        if (sessao.isLogado()) {
            int idUsuarioLogado = sessao.getUsuarioLogado().getId();

            String nome = nameField.getText();
            String cpf = cpfField.getText();
            String genero = genderComboBox.getValue();
            String telefone = phoneField.getText();
            String email = emailField.getText();
            LocalDate dataNascimento = dobPicker.getValue();
            
            String resultado = authService.cadastrar(idUsuarioLogado, nome, cpf, genero, telefone, email, dataNascimento);

            if (resultado.isEmpty()) {
                mensagemLabel.setText("Paciente cadastrado com sucesso!");
                mensagemLabel.setStyle("-fx-text-fill: green;");
            } else {
                mensagemLabel.setText(resultado); // Exibe a mensagem de erro vinda do serviço
                mensagemLabel.setStyle("-fx-text-fill: red;");
            }

        } else {
            mensagemLabel.setText("Erro: Nenhum usuário autenticado. Faça o login novamente.");
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
            Parent registerView = FXMLLoader.load(new File("static/main_view.fxml").toURI().toURL());
            stage.setScene(new Scene(registerView, 1280, 720));
            stage.setTitle("SoftFisio - Lista de pacientes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        // Pega o "Palco" (Stage) a partir de qualquer componente da cena, como o botão salvar.
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}