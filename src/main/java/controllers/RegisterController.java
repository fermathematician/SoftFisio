package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import services.AuthServiceUsuario;
import ui.NavigationManager;

public class RegisterController {

    @FXML private TextField nomeCompletoField;
    @FXML private TextField loginField;
    @FXML private PasswordField senhaField;
    @FXML private PasswordField confirmarSenhaField;
    @FXML private Button registerButton;
    @FXML private Label mensagemLabel;

    @FXML private TextField senhaTextField;
    @FXML private Region toggleSenhaIcon;
    @FXML private TextField confirmarSenhaTextField;
    @FXML private Region toggleConfirmarSenhaIcon;

    private final AuthServiceUsuario authService;

    private boolean isSenhaVisible = false;
    private boolean isConfirmarSenhaVisible = false;

    public RegisterController() {
        this.authService = new AuthServiceUsuario();
    }

    @FXML
    private void initialize() {
        registerButton.setDefaultButton(true);

        senhaTextField.textProperty().bindBidirectional(senhaField.textProperty());
        confirmarSenhaTextField.textProperty().bindBidirectional(confirmarSenhaField.textProperty());

        toggleSenhaIcon.getStyleClass().add("icon-eye");
        toggleConfirmarSenhaIcon.getStyleClass().add("icon-eye");
        
        toggleSenhaIcon.setFocusTraversable(false);
        toggleConfirmarSenhaIcon.setFocusTraversable(false);
    }
    
    @FXML
    private void handleToggleSenhaAction() {
        isSenhaVisible = !isSenhaVisible;
        toggleIcon(isSenhaVisible, senhaField, senhaTextField, toggleSenhaIcon);
    }
    
    @FXML
    private void handleToggleConfirmarSenhaAction() {
        isConfirmarSenhaVisible = !isConfirmarSenhaVisible;
        toggleIcon(isConfirmarSenhaVisible, confirmarSenhaField, confirmarSenhaTextField, toggleConfirmarSenhaIcon);
    }
    
    private void toggleIcon(boolean isVisible, PasswordField passwordField, TextField textField, Region icon) {
        icon.getStyleClass().removeAll("icon-eye", "icon-eye-slash");
        
        textField.setManaged(isVisible);
        textField.setVisible(isVisible);
        passwordField.setManaged(!isVisible);
        passwordField.setVisible(!isVisible);

        if (isVisible) {
            icon.getStyleClass().add("icon-eye-slash");
            textField.requestFocus();
            textField.positionCaret(textField.getText().length());
        } else {
            icon.getStyleClass().add("icon-eye");
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        String nomeCompleto = nomeCompletoField.getText();
        String login = loginField.getText();
        String senha = senhaField.getText();
        String confimarSenha = confirmarSenhaField.getText();

        String resultado = authService.cadastrar(nomeCompleto, login, senha, confimarSenha);

        if (resultado.isEmpty()) {
            mensagemLabel.setText("Cadastro realizado com sucesso! Voltando para o login...");
            mensagemLabel.setStyle("-fx-text-fill: green;");
        } else {
            mensagemLabel.setText(resultado);
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleBackToLoginButtonAction() {
        try {
            String fxmlPath = NavigationManager.getInstance().getPreviousPage();

            Parent loginView = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(loginView, 1280, 720));
            stage.setTitle("SoftFisio - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}