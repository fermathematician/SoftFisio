package ui; 

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import java.util.Optional;

public class AlertFactory {

    /**
     * Construtor privado para impedir a instanciação da classe.
     */
    private AlertFactory() {}

    /**
     * Exibe um diálogo de confirmação estilizado.
     * @param title O título da janela.
     * @param header O texto do cabeçalho.
     * @param content O texto de conteúdo.
     * @param onConfirm Ação a ser executada se o usuário clicar em "OK".
     */
    public static void showConfirmation(String title, String header, String content, Runnable onConfirm) {
        Alert alert = createStyledAlert(AlertType.CONFIRMATION, title, header, content);

        // Adiciona classes de estilo específicas para os botões de confirmação
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok-button");

        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("cancel-button");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            onConfirm.run();
        }
    }

    /**
     * Exibe um diálogo de sucesso estilizado.
     * @param title O título da janela.
     * @param content O texto de conteúdo.
     */
    public static void showSuccess(String title, String content) {
        Alert alert = createStyledAlert(AlertType.INFORMATION, title, null, content);
        
        // Adiciona classe de estilo para o botão OK
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok-button");
        
        alert.showAndWait();
    }

    /**
     * Exibe um diálogo de erro estilizado.
     * @param title O título da janela.
     * @param content O texto de conteúdo.
     */
    public static void showError(String title, String content) {
        Alert alert = createStyledAlert(AlertType.ERROR, title, null, content);
        
        // Adiciona uma classe de estilo ao painel para estilizar botões de erro
        alert.getDialogPane().getStyleClass().add("error-dialog");
        
        alert.showAndWait();
    }


    /**
     * Método auxiliar privado que cria e estiliza um Alert base.
     */
    private static Alert createStyledAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Carrega a folha de estilos CSS
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            AlertFactory.class.getResource("/static/css/alert-style.css").toExternalForm());
            
        // Remove o ícone gráfico padrão para um look mais limpo
        alert.setGraphic(null); 

        return alert;
    }
}