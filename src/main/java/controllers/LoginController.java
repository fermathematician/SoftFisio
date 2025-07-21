package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Usuario;

import java.io.IOException;

import java.net.URL;

import services.SessaoUsuario;
import services.AuthServiceUsuario;

public class LoginController {

    // @FXML conecta estas variáveis aos componentes com o mesmo fx:id no FXML
    @FXML private TextField loginField;
    @FXML private PasswordField senhaField;
    @FXML private Button loginButton;
    @FXML private Label mensagemLabel;

    private final AuthServiceUsuario authService;

    public LoginController() {
        this.authService = new AuthServiceUsuario();
    }

    /**
     * Este método é feito para quando clicar enter o botão de login seja ativado
     */
     @FXML
     private void initialize()
     {
        loginButton.setDefaultButton(true);
     }

    /**
     * Este método é chamado quando o botão de login é clicado (definido no onAction do FXML).
     */
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

    /**
     * Carrega a tela principal (main_view.fxml) e a exibe na mesma janela.
     */
    private void navigateToMainView() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            // Correção aqui:
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
            Stage stage = (Stage) loginButton.getScene().getWindow();
            // Correção aqui:
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