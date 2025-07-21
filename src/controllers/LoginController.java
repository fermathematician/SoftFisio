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
import src.models.Usuario;
import src.services.SessaoUsuario;
import src.services.AuthServiceUsuario;

import java.io.File;
import java.io.IOException;

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
            // Pega a "janela" atual a partir de qualquer componente da tela
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Carrega o FXML da tela principal
            Parent mainView = FXMLLoader.load(new File("static/main_view.fxml").toURI().toURL());

            // Cria uma nova cena e a define na janela
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
            Parent registerView = FXMLLoader.load(new File("static/register.fxml").toURI().toURL());
            stage.setScene(new Scene(registerView, 1280, 720));
            stage.setTitle("SoftFisio - Novo Cadastro");
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao carregar a tela de cadastro.");
        }
    }
}