package src.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import src.services.AuthService;

import java.io.File;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField nomeCompletoField;
    @FXML private TextField loginField;
    @FXML private PasswordField senhaField;
    @FXML private Button registerButton;
    @FXML private Label mensagemLabel;

    private final AuthService authService;

    public RegisterController() {
        this.authService = new AuthService();
    }

    /**
     * Este método é feito para quando clicar enter o botão de registrar seja ativado
     */

     @FXML
     private void initialize()
     {
        registerButton.setDefaultButton(true);
     }

    @FXML
    private void handleRegisterButtonAction() {
        String nomeCompleto = nomeCompletoField.getText();
        String login = loginField.getText();
        String senha = senhaField.getText();

        String resultado = authService.cadastrar(login, senha, nomeCompleto);

        if (resultado.isEmpty()) {
            mensagemLabel.setText("Cadastro realizado com sucesso!");
            mensagemLabel.setStyle("-fx-text-fill: green;");
        } else {
            mensagemLabel.setText(resultado); // Exibe a mensagem de erro do serviço
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleBackToLoginButtonAction() {
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Parent loginView = FXMLLoader.load(new File("static/login.fxml").toURI().toURL());
            stage.setScene(new Scene(loginView, 1280, 720));
            stage.setTitle("SoftFisio - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}