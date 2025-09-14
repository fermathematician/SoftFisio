package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane; // Import StackPane
import javafx.scene.layout.VBox; // Import VBox
import models.Anexo;
import java.io.File;
import java.util.function.Consumer;

public class AnexoCardController {

    @FXML private ImageView thumbnailImageView;
    @FXML private Label descriptionLabel;
    @FXML private StackPane cardRoot; // Adicione o fx:id ao StackPane no FXML
    @FXML private VBox anexoContent; // Adicione o fx:id ao VBox no FXML

    private Anexo anexo;
    private Consumer<Anexo> deleteHandler;
    private Consumer<Anexo> viewHandler; // Novo handler para o clique de visualização

    // Método para popular o card com os dados de um anexo
    public void setData(Anexo anexo) {
        this.anexo = anexo;
        // Usa o nome do arquivo como descrição principal
        descriptionLabel.setText(new File(anexo.getCaminhoArquivo()).getName());

        // Lógica movida para cá: decide se mostra imagem ou ícone de vídeo
        if (anexo.getTipoMidia().equalsIgnoreCase("FOTO")) {
            try {
                File file = new File(anexo.getCaminhoArquivo());
                Image image = new Image(file.toURI().toString(), 200, 150, true, true); // Carrega com tamanho definido
                thumbnailImageView.setImage(image);
                thumbnailImageView.setFitHeight(150);
                thumbnailImageView.setFitWidth(200);
                thumbnailImageView.setPreserveRatio(true);
            } catch (Exception e) {
                // Em caso de erro, você pode colocar uma imagem de erro padrão
                // Image errorImage = new Image(getClass().getResourceAsStream("/assets/error-image.png"));
                // thumbnailImageView.setImage(errorImage);
                System.err.println("Erro ao carregar imagem do anexo: " + anexo.getCaminhoArquivo());
            }
        } else if (anexo.getTipoMidia().equalsIgnoreCase("VIDEO")) {
            // Se for vídeo, esconde o ImageView e coloca um Region com o estilo no lugar
            anexoContent.getChildren().remove(thumbnailImageView); // Remove o ImageView
            Region videoIcon = new Region();
            videoIcon.getStyleClass().add("video-icon");
            videoIcon.setPrefSize(200, 150);
            anexoContent.getChildren().add(0, videoIcon); 
        }

        // Adiciona o evento de clique ao card inteiro para abrir o visualizador
        cardRoot.setOnMouseClicked(event -> {
            if (viewHandler != null) {
                viewHandler.accept(anexo);
            }
        });
    }

    // Define o "callback" para a exclusão
    public void setDeleteHandler(Consumer<Anexo> handler) {
        this.deleteHandler = handler;
    }

    // Define o "callback" para a visualização
    public void setViewHandler(Consumer<Anexo> handler) {
        this.viewHandler = handler;
    }

    @FXML
    void handleDeleteAnexo(MouseEvent event) {
        // Impede que o clique no ícone de lixeira também abra o visualizador
        event.consume(); 
        
        if (deleteHandler != null) {
            deleteHandler.accept(anexo);
        }
    }
}