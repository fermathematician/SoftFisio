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

import services.SessaoUsuario;
import services.AuthServiceUsuario;
import ui.NavigationManager;

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
            
            try {
                String fxmlPath = "/static/main_view.fxml";

                NavigationManager.getInstance().pushHistory(fxmlPath);

                Parent mainView = FXMLLoader.load(getClass().getResource(fxmlPath));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(mainView, 1280, 720));
                stage.setTitle("SoftFisio - Lista de pacientes");
            }catch (IOException e){
                e.printStackTrace();
                mensagemLabel.setText("Erro ao carregar a tela principal.");
                mensagemLabel.setStyle("-fx-text-fill: red;");
            }

        } else {
            mensagemLabel.setText("Login ou senha inválidos. Tente novamente.");
            mensagemLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleGoToRegisterButtonAction() {
        try {
            String fxmlPath = "/static/register.fxml";

            NavigationManager.getInstance().pushHistory(fxmlPath);

            Parent RegisterView = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(RegisterView, 1280, 720));
            stage.setTitle("SoftFisio - Cadastro");
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao carregar a tela de cadastro.");
        }
    }
}