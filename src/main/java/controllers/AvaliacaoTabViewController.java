package controllers;

import java.io.IOException;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import models.Avaliacao;
import models.Paciente;
import services.ProntuarioService;
import ui.AlertFactory;
import ui.NavigationManager;

public class AvaliacaoTabViewController {

    @FXML private VBox avaliacoesVBox;
    @FXML private Button novaAvaliacaoButton;
    @FXML private Label emptyAvaliacoesLabel;

    private ProntuarioService prontuarioService;
    private Paciente pacienteAtual;
    private OnHistoryChangedListener historyListener;

    public AvaliacaoTabViewController() {
        this.prontuarioService = new ProntuarioService();
    }

    public void initData(Paciente paciente, OnHistoryChangedListener listener) {
        this.pacienteAtual = paciente;
        this.historyListener = listener;
        loadAvaliacoes();
    }

    public void loadAvaliacoes() {
        avaliacoesVBox.getChildren().clear();
        List<Avaliacao> avaliacoes = prontuarioService.getAvaliacoes(pacienteAtual.getId());
        for (Avaliacao avaliacao : avaliacoes) {
            VBox avaliacaoCard = createAvaliacaoCard(avaliacao);
            avaliacoesVBox.getChildren().add(avaliacaoCard);
        }
        updateAvaliacoesVisibility();
    }

    private void updateAvaliacoesVisibility() {
        boolean isEmpty = avaliacoesVBox.getChildren().isEmpty();
        emptyAvaliacoesLabel.setVisible(isEmpty);
        emptyAvaliacoesLabel.setManaged(isEmpty);
        avaliacoesVBox.setVisible(!isEmpty); // Alterado de scrollPane para avaliacoesVBox
        avaliacoesVBox.setManaged(!isEmpty); // Alterado de scrollPane para avaliacoesVBox
    }

    private VBox createAvaliacaoCard(Avaliacao avaliacao) {
        VBox card = new VBox(5);
        card.getStyleClass().add("patient-card");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Avaliação de' dd 'de' MMMM 'de' yyyy");
        Label dateLabel = new Label(avaliacao.getDataAvaliacao().format(formatter));
        dateLabel.getStyleClass().add("session-date-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Region deleteIcon = new Region();
        deleteIcon.getStyleClass().add("delete-icon");
        deleteIcon.setOnMouseClicked(event -> handleDelete(avaliacao));

        topBar.getChildren().addAll(dateLabel, spacer, deleteIcon);

        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 5, 0));
        
        VBox detalhesVBox = new VBox(8);
        detalhesVBox.getChildren().add(createCampoWebView("Doença Atual (HDA):", avaliacao.getDoencaAtual()));
        detalhesVBox.getChildren().add(createCampoWebView("História Pregressa:", avaliacao.getHistoriaPregressa()));
        detalhesVBox.getChildren().add(createCampoWebView("Inspeção e Palpação:", avaliacao.getInspecaoPalpacao()));
        detalhesVBox.getChildren().add(createCampoWebView("ADM:", avaliacao.getAdm()));
        detalhesVBox.getChildren().add(createCampoWebView("Força Muscular:", avaliacao.getForcaMuscular()));
        detalhesVBox.getChildren().add(createCampoWebView("Avaliação Funcional:", avaliacao.getAvaliacaoFuncional()));
        detalhesVBox.getChildren().add(createCampoWebView("Testes Especiais:", avaliacao.getTestesEspeciais()));
        detalhesVBox.getChildren().add(createCampoWebView("Escalas Funcionais:", avaliacao.getEscalasFuncionais()));
        detalhesVBox.getChildren().add(createCampoWebView("Diagnóstico Cinesiológico:", avaliacao.getDiagnosticoCinesiologico()));
        detalhesVBox.getChildren().add(createCampoWebView("Plano de Tratamento:", avaliacao.getPlanoTratamento()));

        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(15, 0, 0, 0));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("primary-button");
        editButton.setOnAction(event -> handleEdit(avaliacao));
        bottomBar.getChildren().add(editButton);

        card.getChildren().addAll(topBar, separator, detalhesVBox, bottomBar);
        return card;
    }

    private VBox createCampoWebView(String titulo, String htmlContent) {
        VBox campo = new VBox(2);
        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-weight: bold;");

        WebView webView = new WebView();

        webView.addEventFilter(javafx.scene.input.ScrollEvent.ANY, event -> {
            avaliacoesVBox.fireEvent(event.copyFor(event.getSource(), avaliacoesVBox));
            event.consume();
        });
      
        webView.setContextMenuEnabled(false);

        String contentToShow = (htmlContent == null || htmlContent.isEmpty() || htmlContent.contains("<body contenteditable=\"true\"></body>")) 
                                ? "<i>Não informado</i>" : htmlContent;

        webView.getEngine().loadContent(contentToShow);
        
        webView.setPrefHeight(250); 

        campo.getChildren().addAll(tituloLabel, webView);
        return campo;
    }
  
    @FXML
    private void handleDelete(Avaliacao avaliacao) {
        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deseja deletar a avaliação de " + avaliacao.getDataAvaliacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?",
            "Esta ação é permanente.",
            () -> {
                String resultado = prontuarioService.deletarAvaliacao(avaliacao.getId());
                if (resultado.isEmpty()) { 
                    loadAvaliacoes(); // Atualiza a lista de avaliações
                    if (historyListener != null) {
                        historyListener.onHistoryChanged(); // Avisa o prontuário para atualizar a lista de histórico
                    }
                } else { 
                    AlertFactory.showError("Erro ao Deletar", "Não foi possível deletar a avaliação: " + resultado);
                }
            }
        );
    }
    
    @FXML
    private void handleEdit(Avaliacao avaliacao) {
        try {
            // Navega para a tela de formulário, passando a avaliação para edição
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/formulario_avaliacao.fxml"));
            Parent formView = loader.load();
            String fxmlPath = "/static/formulario_avaliacao.fxml";

            NavigationManager.getInstance().pushHistory(fxmlPath);


            // Pega o controller do formulário e o configura para o modo de edição
            FormularioAvaliacaoController formController = loader.getController();
            formController.configureParaEdicao(avaliacao, pacienteAtual, historyListener);

            Stage stage = (Stage) novaAvaliacaoButton.getScene().getWindow();
            // A navegação para uma nova "cena" (tela cheia) é uma opção
            stage.setScene(new Scene(formView));
            stage.setMaximized(true);
            stage.setTitle("SoftFisio - Editar Avaliação");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNovaAvaliacao() {
        try {
            // Navega para a tela de formulário em modo de criação
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/formulario_avaliacao.fxml"));
            Parent formView = loader.load();
            String fxmlPath = "/static/formulario_avaliacao.fxml";

            NavigationManager.getInstance().pushHistory(fxmlPath);

            // Pega o controller do formulário e o configura para o modo de criação
            FormularioAvaliacaoController formController = loader.getController();
            formController.configureParaCriacao(pacienteAtual, historyListener);
            
            Stage stage = (Stage) novaAvaliacaoButton.getScene().getWindow();
            stage.setScene(new Scene(formView));
            stage.setMaximized(true);
            stage.setTitle("SoftFisio - Nova Avaliação");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}