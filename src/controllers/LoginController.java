// src/controllers/LoginController.java
package src.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import src.services.AuthService;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField senhaField;
    @FXML private Label mensagemLabel;

    private AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    private void handleLoginButtonAction() {
        String login = loginField.getText();
        String senha = senhaField.getText();

        boolean autenticado = authService.autenticar(login, senha);

        if (autenticado) {
            mensagemLabel.setText("Login bem-sucedido!");
            mensagemLabel.setStyle("-fx-text-fill: green;");
            // Aqui você navegaria para a tela principal
            // Ex: SceneManager.changeScene("dashboard.fxml");
        } else {
            mensagemLabel.setText("Login ou senha inválidos.");
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    }
}