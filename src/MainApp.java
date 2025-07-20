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
        // Altere 'main_view.fxml' para 'login.fxml'
        URL fxmlUrl = new File("static/login.fxml").toURI().toURL(); 
        Parent root = FXMLLoader.load(fxmlUrl);

        primaryStage.setTitle("SoftFisio - Login"); // Altere o título inicial
        primaryStage.setScene(new Scene(root, 1280, 720)); // Ajuste o tamanho da janela
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Inicializa o banco de dados antes de tudo
        DatabaseManager.initializeDatabase();
        launch(args);
    }
}