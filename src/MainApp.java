// src/MainApp.java

import db.DatabaseManager; // Importa a classe do pacote 'db'
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o FXML do diretório 'static'
        URL fxmlUrl = new File("static/main_view.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(fxmlUrl);

        primaryStage.setTitle("FisioApp");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Inicializa o banco de dados ANTES de iniciar a interface gráfica
        DatabaseManager.initializeDatabase();
        launch(args);
    }
}