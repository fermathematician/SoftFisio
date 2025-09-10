package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import models.Paciente;
import models.Sessao;
import ui.AlertFactory;
import ui.NavigationManager;
import services.ProntuarioService;
import javafx.scene.web.WebView;

public class TreatmentViewController {

    @FXML private VBox sessionsVBox;
    @FXML private Button novaSessaoButton;
    @FXML private Label emptySessionsLabel;

    private ProntuarioService prontuarioService;
    private Paciente pacienteAtual;
    private OnHistoryChangedListener historyListener;

    public TreatmentViewController() {
        this.prontuarioService = new ProntuarioService();
    }


    public void initData(Paciente paciente, OnHistoryChangedListener listener) {
        this.pacienteAtual = paciente;
        this.historyListener = listener;
        loadSessoes();
    }

    public void loadSessoes() {
        sessionsVBox.getChildren().clear();
        List<Sessao> sessoes = prontuarioService.getSessoes(pacienteAtual.getId());
        for (Sessao sessao : sessoes) {
            VBox sessionCard = createSessionCard(sessao);
            sessionsVBox.getChildren().add(sessionCard);
        }
        updateSessionsVisibility();
    }

    private void updateSessionsVisibility() {
        boolean isEmpty = sessionsVBox.getChildren().isEmpty();
        emptySessionsLabel.setVisible(isEmpty);
        emptySessionsLabel.setManaged(isEmpty);
        sessionsVBox.setVisible(!isEmpty);
        sessionsVBox.setManaged(!isEmpty);
    }

    private VBox createSessionCard(Sessao sessao) {
        VBox card = new VBox(5);
        card.getStyleClass().add("patient-card");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Sessão de' dd 'de' MMMM 'de' yyyy");
        Label dateLabel = new Label(sessao.getDataSessao().format(formatter));
        dateLabel.getStyleClass().add("session-date-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Region deleteIcon = new Region();
        deleteIcon.getStyleClass().add("delete-icon");
        deleteIcon.setOnMouseClicked(event -> handleDelete(sessao));

        topBar.getChildren().addAll(dateLabel, spacer, deleteIcon);

        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 5, 0));

        // Cria um WebView para renderizar o HTML
        WebView webView = new WebView();
            webView.addEventFilter(javafx.scene.input.ScrollEvent.ANY, event -> {
            // Cria um novo evento de scroll com os mesmos parâmetros do original,
            // mas direcionado para o VBox principal das sessões (sessionsVBox).
            sessionsVBox.fireEvent(event.copyFor(event.getSource(), sessionsVBox));
            
            // Consome o evento original para que o WebView não tente processá-lo.
            event.consume();
        });

        // Carrega o conteúdo HTML salvo no banco
        webView.getEngine().loadContent(sessao.getEvolucaoTexto());
        // Ajusta a altura máxima para não ocupar muito espaço
        webView.setMaxHeight(150);

        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(15, 0, 0, 0));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("primary-button");
        editButton.setOnAction(event -> handleEdit(sessao));
        bottomBar.getChildren().add(editButton);

        card.getChildren().addAll(topBar, separator, webView);

        if (sessao.getObservacoesSessao() != null && !sessao.getObservacoesSessao().isEmpty()) {
            Label obsLabelTitle = new Label("Observações:");
            obsLabelTitle.getStyleClass().add("session-observation-title");
            Label obsLabel = new Label(sessao.getObservacoesSessao());
            obsLabel.setWrapText(true);
            card.getChildren().addAll(obsLabelTitle, obsLabel);
        }

        Region vSpacer = new Region();
        VBox.setVgrow(vSpacer, Priority.ALWAYS);
        card.getChildren().add(vSpacer);

        card.getChildren().add(bottomBar);

        return card;
    }


    @FXML
    private void handleDelete(Sessao sessao) {
        // <<--- CÓDIGO ALTERADO PARA FICAR IGUAL AO DO PRONTUÁRIO ---<<<
        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deseja deletar a sessão de " + sessao.getDataSessao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?",
            "Esta ação é permanente.",
            () -> { // Ação a ser executada se o usuário clicar em "OK"
                String resultado = prontuarioService.deletarSessao(sessao.getId());
                if (resultado.isEmpty()) { 
                    loadSessoes();
                    if (historyListener != null) {
                        historyListener.onHistoryChanged();
                    }
                } else { 
                    AlertFactory.showError("Erro ao Deletar", "Não foi possível deletar a sessão: " + resultado);
                }
            }
        );
    }
    

    @FXML
    private void handleEdit(Sessao sessao) {
        try {
            String fxmlPath = "/static/formulario_sessao.fxml";

            NavigationManager.getInstance().pushHistory(fxmlPath);

            URL fxmlUrl = getClass().getResource(fxmlPath);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent newPatient = loader.load();

            SessaoController controller = loader.getController();
            controller.initData(sessao, pacienteAtual, historyListener);

            Stage stage = (Stage) novaSessaoButton.getScene().getWindow();
            stage.setScene(new Scene(newPatient, 1280, 720));
            stage.setTitle("SoftFisio - Editar Sessão");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNovaSessao() {
        try {
            String fxmlPath = "/static/formulario_sessao.fxml";
            
            NavigationManager.getInstance().pushHistory(fxmlPath);

            URL fxmlUrl = getClass().getResource(fxmlPath);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent newSession = loader.load();

            SessaoController cadastrarSessaoController = loader.getController();
            cadastrarSessaoController.initData(pacienteAtual, historyListener);

            Stage stage = (Stage) novaSessaoButton.getScene().getWindow();
            stage.setScene(new Scene(newSession, 1280, 720));
            stage.setTitle("SoftFisio - Nova Sessão");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}