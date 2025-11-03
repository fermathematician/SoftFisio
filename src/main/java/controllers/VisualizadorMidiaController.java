package controllers;

import java.io.File;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VisualizadorMidiaController {

    @FXML private ImageView imageView;
    @FXML private MediaView mediaView;
    @FXML private StackPane mediaContainer;
    @FXML private HBox controlsBox;
    @FXML private Button playPauseButton;
    @FXML private Button rewindButton;
    @FXML private Button forwardButton;
    @FXML private Slider progressSlider;
    @FXML private Slider volumeSlider;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;

    @FXML
    public void initialize() {
        // Garantir que os controles estejam inicialmente ocultos
        if (controlsBox != null) {
            controlsBox.setVisible(false);
            controlsBox.setManaged(false);
        }
        if (mediaView != null) {
            mediaView.setVisible(false);
        }
        if (imageView != null) {
            imageView.setVisible(false);
        }
    }

    public void initData(String caminhoArquivo) {
        try {
            File file = new File(caminhoArquivo);
            String nomeArquivo = file.getName().toLowerCase();

            if (nomeArquivo.endsWith(".png") || nomeArquivo.endsWith(".jpg") || nomeArquivo.endsWith(".jpeg")) {
                // Exibir imagem
                mediaView.setVisible(false);
                controlsBox.setVisible(false);
                controlsBox.setManaged(false);

                imageView.setVisible(true);
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                imageView.setPreserveRatio(true);

                Platform.runLater(() -> {
                    Stage stage = (Stage) imageView.getScene().getWindow();
                    stage.setMaximized(true);
                    imageView.fitWidthProperty().bind(mediaContainer.widthProperty());
                    imageView.fitHeightProperty().bind(mediaContainer.heightProperty());
                });

            } else if (nomeArquivo.endsWith(".mp4") || nomeArquivo.endsWith(".mov") || nomeArquivo.endsWith(".mkv")) {
                // Exibir vídeo
                imageView.setVisible(false);
                
                controlsBox.setVisible(true);
                controlsBox.setManaged(true);
                
                mediaView.setVisible(true);

                Media media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                
                // Define o texto inicial do botão como pause
                playPauseButton.setText("⏸");
                
                // Configurar controles ANTES de iniciar o vídeo
                setupVideoControls();
                
                // Inicia o vídeo
                mediaPlayer.play();

                Platform.runLater(() -> {
                    Stage stage = (Stage) mediaView.getScene().getWindow();
                    if (stage != null) {
                        stage.setMaximized(true);
                        // Bind apenas à largura do container, deixe a altura automática
                        mediaView.fitWidthProperty().bind(mediaContainer.widthProperty());
                        mediaView.fitHeightProperty().bind(mediaContainer.heightProperty());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private void setupVideoControls() {
        playPauseButton.setOnAction(e -> {
            if (isPlaying) {
                mediaPlayer.pause();
                playPauseButton.setText("▶");
                isPlaying = false;
            } else {
                mediaPlayer.play();
                playPauseButton.setText("⏸");
                isPlaying = true;
            }
        });

        rewindButton.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(javafx.util.Duration.seconds(5)));
            }
        });

        forwardButton.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(5)));
            }
        });

        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.5);
        
        mediaPlayer.setOnReady(() -> {
            mediaPlayer.setVolume(0.5);
            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
            progressSlider.setMax(mediaPlayer.getTotalDuration().toMillis());
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!progressSlider.isValueChanging()) {
                progressSlider.setValue(newTime.toMillis());
            }
        });

        // Listener para arrastar o slider
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(javafx.util.Duration.millis(progressSlider.getValue()));
            }
        });
        
        // NOVO: Listener para clique direto no slider
        progressSlider.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                double mouseX = event.getX();
                double sliderWidth = progressSlider.getWidth();
                double percentage = mouseX / sliderWidth;
                double newValue = percentage * progressSlider.getMax();
                progressSlider.setValue(newValue);
                mediaPlayer.seek(javafx.util.Duration.millis(newValue));
            }
        });
    }

    @FXML
    private void handleClose() {
        stopAndClose();
    }

    public void stopAndClose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        if (mediaView.getScene() != null) {
            Stage stage = (Stage) mediaView.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        } else if (imageView.getScene() != null) {
            Stage stage = (Stage) imageView.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }
}