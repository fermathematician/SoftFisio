import db.DatabaseManager; 
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.of("pt", "BR"));
        URL fxmlUrl = getClass().getResource("/static/login.fxml");

        if (fxmlUrl == null) {
            throw new RuntimeException("Não foi possível encontrar o recurso FXML. Verifique o caminho: /static/login.fxml");
        }

        Parent root = FXMLLoader.load(fxmlUrl);

        primaryStage.setTitle("SoftFisio - Login"); // Altere o título inicial
        primaryStage.setScene(new Scene(root, 1280, 720)); // Ajuste o tamanho da janela
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            // Inicializa o banco de dados antes de tudo
            DatabaseManager.initializeDatabase();
            launch(args);
        } catch (Exception e) {
            System.err.println("### ERRO FATAL NA APLICAÇÃO ###");
            System.err.println("A aplicação encontrou um erro e não pôde ser iniciada.");
            e.printStackTrace(); // <-- ISTO VAI MOSTRAR A CAUSA REAL DO PROBLEMA
        }
    }
}