package controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class VisualizadorMidiaController {

    @FXML private ImageView imageView;
    @FXML private Button closeButton;

    /**
     * Recebe o caminho do arquivo de imagem e o carrega no ImageView.
     * @param caminhoArquivo O caminho absoluto para o arquivo de imagem.
     */
    public void initData(String caminhoArquivo) {
        try {
            File file = new File(caminhoArquivo);
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem: " + caminhoArquivo);
            e.printStackTrace();
        }
    }

    /**
     * Fecha a janela (Stage) do visualizador.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}