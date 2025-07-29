import db.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.NavigationService; 

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Locale.setDefault(new Locale("pt", "BR"));

        String initialFxmlPath = "/static/login.fxml";

        NavigationService.getInstance().startHistoryWith(initialFxmlPath);

        try {
            URL fxmlUrl = getClass().getResource(initialFxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Não foi possível encontrar o recurso FXML. Verifique o caminho: " + initialFxmlPath);
            }

            Parent root = FXMLLoader.load(fxmlUrl);

            primaryStage.setTitle("SoftFisio - Login");
            primaryStage.setScene(new Scene(root, 1280, 720));
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("### ERRO FATAL AO INICIAR A INTERFACE GRÁFICA ###");
            System.err.println("A aplicação não pôde carregar a tela inicial.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();
            launch(args);
        } catch (Exception e) {
            System.err.println("### ERRO FATAL NA APLICAÇÃO ###");
            System.err.println("A aplicação encontrou um erro e não pôde ser iniciada.");
            e.printStackTrace();
        }
    }
}