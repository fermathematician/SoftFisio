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
import models.Usuario;

import java.io.IOException;
import java.net.URL;
import services.SessaoUsuario;
import services.AuthServiceUsuario;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField senhaField;
    @FXML private Label mensagemLabel;

    @FXML private TextField senhaTextField;
    @FXML private Region toggleSenhaIcon;
    @FXML private Button loginButton;

    private final AuthServiceUsuario authService;
    private boolean isSenhaVisible = false;

    public LoginController() {
        this.authService = new AuthServiceUsuario();
    }

    @FXML
    private void initialize() {
        senhaTextField.textProperty().bindBidirectional(senhaField.textProperty());

        toggleSenhaIcon.getStyleClass().add("icon-eye");
        
        toggleSenhaIcon.setFocusTraversable(false);

        loginButton.setDefaultButton(true);
    }

    @FXML
    private void handleToggleSenhaAction() {
        isSenhaVisible = !isSenhaVisible;

        toggleSenhaIcon.getStyleClass().removeAll("icon-eye", "icon-eye-slash");

        if (isSenhaVisible) {
            // MOSTRAR SENHA
            senhaField.setVisible(false);
            senhaField.setManaged(false);
            
            senhaTextField.setVisible(true);
            senhaTextField.setManaged(true);
            
            toggleSenhaIcon.getStyleClass().add("icon-eye-slash");
            
            senhaTextField.requestFocus();
            senhaTextField.positionCaret(senhaTextField.getText().length());

        } else {
            senhaTextField.setVisible(false);
            senhaTextField.setManaged(false);
            
            senhaField.setVisible(true);
            senhaField.setManaged(true);

            toggleSenhaIcon.getStyleClass().add("icon-eye");

            senhaField.requestFocus();
            senhaField.positionCaret(senhaField.getText().length());
        }
    }

    @FXML
    private void handleLoginButtonAction() {
        String login = loginField.getText();
        String senha = senhaField.getText();

        Usuario usuarioAutenticado = authService.autenticar(login, senha);

        if (usuarioAutenticado != null) {
            SessaoUsuario.getInstance().login(usuarioAutenticado);
            
            mensagemLabel.setText("Login bem-sucedido! Navegando...");
            mensagemLabel.setStyle("-fx-text-fill: green;");
            
            navigateToMainView();
        } else {
            mensagemLabel.setText("Login ou senha inválidos. Tente novamente.");
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    private void navigateToMainView() {
        try {
            Stage stage = (Stage) loginField.getScene().getWindow();
            URL fxmlUrl = getClass().getResource("/static/main_view.fxml");
            Parent mainView = FXMLLoader.load(fxmlUrl);
            
            stage.setScene(new Scene(mainView, 1280, 720));
            stage.setTitle("SoftFisio - Lista de pacientes");

        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao carregar a tela principal.");
        }
    }

    @FXML
    private void handleGoToRegisterButtonAction() {
        try {
            Stage stage = (Stage) loginField.getScene().getWindow();
            URL fxmlUrl = getClass().getResource("/static/register.fxml");
            Parent registerView = FXMLLoader.load(fxmlUrl);
            
            stage.setScene(new Scene(registerView, 1280, 720));
            stage.setTitle("SoftFisio - Novo Cadastro");
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao carregar a tela de cadastro.");
        }
    }
}