package controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VisualizadorMidiaController {

    @FXML private ImageView imageView;
    @FXML private Button closeButton;
    @FXML private MediaView mediaView;
    private MediaPlayer mediaPlayer;

    /**
     * Recebe o caminho do arquivo de imagem e o carrega no ImageView.
     * @param caminhoArquivo O caminho absoluto para o arquivo de imagem.
     */
    public void initData(String caminhoArquivo) {
        try {
            File file = new File(caminhoArquivo);
            String nomeArquivo = file.getName().toLowerCase();

            if (nomeArquivo.endsWith(".png") || nomeArquivo.endsWith(".jpg") || nomeArquivo.endsWith(".jpeg") || nomeArquivo.endsWith(".gif")) {
                // Se for imagem, mostra o ImageView e esconde o MediaView
                mediaView.setVisible(false);
                imageView.setVisible(true);
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else if (nomeArquivo.endsWith(".mp4") || nomeArquivo.endsWith(".mov") || nomeArquivo.endsWith(".avi")) {
                // Se for vídeo, mostra o MediaView e esconde o ImageView
                imageView.setVisible(false);
                mediaView.setVisible(true);

                Media media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
             // --- ADICIONE ESTE BLOCO DE CÓDIGO ---
                mediaPlayer.setOnError(() -> {
                    System.err.println("### ERRO NO MEDIAPLAYER ###");
                    System.err.println("Ocorreu um erro ao tentar tocar a mídia.");
                    if (mediaPlayer.getError() != null) {
                        System.err.println("Detalhes: " + mediaPlayer.getError());
                        mediaPlayer.getError().printStackTrace();
                    }
                });
                // --- FIM DA ADIÇÃO ---


                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.setAutoPlay(true); // Toca o vídeo automaticamente
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar a mídia: " + caminhoArquivo);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        // Se houver um vídeo tocando, pare-o antes de fechar.
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}