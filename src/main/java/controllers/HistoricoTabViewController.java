package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import models.HistoricoItem;
import models.Paciente;
import models.Sessao;
import services.ProntuarioService;
import ui.AlertFactory;
import ui.NavigationManager;

public class HistoricoTabViewController {

    @FXML private VBox historicoVBox;
    @FXML private Label emptyHistoryLabel;
    @FXML private Button gerarPdfButton;

    private Paciente pacienteAtual;
    private OnHistoryChangedListener historyListener;
    private ProntuarioService prontuarioService;

    public HistoricoTabViewController() {
        this.prontuarioService = new ProntuarioService();
    }

    public void initData(Paciente paciente, OnHistoryChangedListener listener) {
        this.pacienteAtual = paciente;
        this.historyListener = listener;
        carregarHistoricoCompleto();
    }

    public void carregarHistoricoCompleto() {
        historicoVBox.getChildren().clear();
        List<HistoricoItem> historico = new ArrayList<>();
        historico.addAll(prontuarioService.getSessoes(pacienteAtual.getId()));
        historico.addAll(prontuarioService.getAvaliacoes(pacienteAtual.getId()));
        historico.sort(Comparator.comparing(HistoricoItem::getData).reversed());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");

        for (HistoricoItem item : historico) {
            VBox card = createHistoryCard(item, formatter);
            historicoVBox.getChildren().add(card);
        }
        
        updateHistoryVisibility();
    }

    private void updateHistoryVisibility() {
        boolean isEmpty = historicoVBox.getChildren().isEmpty();
        emptyHistoryLabel.setVisible(isEmpty);
        emptyHistoryLabel.setManaged(isEmpty);
        historicoVBox.setVisible(!isEmpty);
        historicoVBox.setManaged(!isEmpty);
    }
    
    private VBox createHistoryCard(HistoricoItem item, DateTimeFormatter formatter) {
        VBox card = new VBox(10);
        card.getStyleClass().add("patient-card");

        Label tipoLabel = new Label(item.getTipo());
        tipoLabel.getStyleClass().add("card-title");
        Label dataLabel = new Label(item.getData().format(formatter));
        dataLabel.getStyleClass().add("card-subtitle");
        
        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        Region deleteIcon = new Region();
        deleteIcon.getStyleClass().add("delete-icon");

        HBox header = new HBox(tipoLabel, new Label(" - "), dataLabel, hSpacer, deleteIcon);
        header.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(header, new Separator());

        if (item instanceof Sessao) {
            Sessao sessao = (Sessao) item;
            // --- Ação de deletar associada ---
            deleteIcon.setOnMouseClicked(event -> handleDelete(sessao));

            WebView webView = new WebView();
            webView.addEventFilter(javafx.scene.input.ScrollEvent.ANY, event -> {
                historicoVBox.fireEvent(event.copyFor(event.getSource(), historicoVBox));
                event.consume();
            });
            webView.getEngine().loadContent(sessao.getEvolucaoTexto());
            webView.setPrefHeight(150);
            card.getChildren().add(webView);

        } else if (item instanceof Avaliacao) {
            Avaliacao avaliacao = (Avaliacao) item;
            // --- Ação de deletar associada ---
            deleteIcon.setOnMouseClicked(event -> handleDeleteA(avaliacao));

            VBox detalhesAvaliacao = new VBox(8);
            detalhesAvaliacao.getChildren().add(createCampoWebView("Queixa Principal:", avaliacao.getQueixaPrincipal()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Histórico da Doença:", avaliacao.getHistoricoDoencaAtual()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Exames Físicos:", avaliacao.getExamesFisicos()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Diagnóstico Fisioterapêutico:", avaliacao.getDiagnosticoFisioterapeutico()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Plano de Tratamento:", avaliacao.getPlanoTratamento()));
            card.getChildren().add(detalhesAvaliacao);
        }
        
        Region vSpacer = new Region();
        VBox.setVgrow(vSpacer, Priority.ALWAYS);
        card.getChildren().add(vSpacer);

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("primary-button");
        
        // --- Ação de editar associada ---
        if (item instanceof Sessao) {
            editButton.setOnAction(event -> handleEdit((Sessao)item));
        } else if (item instanceof Avaliacao) {
            editButton.setOnAction(event -> handleEditA((Avaliacao)item));
        }

        HBox bottomBar = new HBox(editButton);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        card.getChildren().add(bottomBar);

        return card;
    }

    private VBox createCampoWebView(String titulo, String htmlContent) {
        VBox campo = new VBox(2);
        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-weight: bold;");
        WebView webView = new WebView();

        webView.addEventFilter(javafx.scene.input.ScrollEvent.ANY, event -> {
            historicoVBox.fireEvent(event.copyFor(event.getSource(), historicoVBox));
            event.consume();
        });

        String contentToShow = (htmlContent == null || htmlContent.isEmpty() || htmlContent.contains("<body contenteditable=\"true\"></body>")) ? "" : htmlContent;
        webView.getEngine().loadContent(contentToShow);
        webView.setPrefHeight(75);
        campo.getChildren().addAll(tituloLabel, webView);
        return campo;
    }

    @FXML
    private void handleGerarPdf() {
        // Mova toda a lógica do método handleGerarPdf do ProntuarioViewController para cá
        System.out.println("Gerando PDF para o paciente: " + pacienteAtual.getNomeCompleto());
    }

    // =======================================================================
    // MÉTODOS DE EDITAR E DELETAR MIGRADOS
    // =======================================================================

    private void handleDelete(Sessao sessao) {
        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deseja deletar a sessão de " + sessao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?",
            "Esta ação é permanente.",
            () -> {
                String resultado = prontuarioService.deletarSessao(sessao.getId());
                if (resultado.isEmpty()) { 
                    if (historyListener != null) {
                        historyListener.onHistoryChanged();
                    }
                } else { 
                    AlertFactory.showError("Erro ao Deletar", "Não foi possível deletar a sessão: " + resultado);
                }
            }
        );
    }

    private void handleEdit(Sessao sessao) {
        try {
            String fxmlPath = "/static/formulario_sessao.fxml";
            NavigationManager.getInstance().pushHistory(fxmlPath);
            URL fxmlUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent formView = loader.load();

            SessaoController controller = loader.getController();
            controller.initData(sessao, this.pacienteAtual, this.historyListener);

            Stage stage = (Stage) historicoVBox.getScene().getWindow();
            stage.setScene(new Scene(formView, 1280, 720));
            stage.setTitle("SoftFisio - Editar Sessão");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteA(Avaliacao avaliacao) {
        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deseja deletar a avaliação de " + avaliacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?",
            "Esta ação é permanente.",
            () -> {
                String resultado = prontuarioService.deletarAvaliacao(avaliacao.getId());
                if (resultado.isEmpty()) {
                    if (historyListener != null) {
                        historyListener.onHistoryChanged();
                    }
                } else {
                    AlertFactory.showError("Erro ao Deletar", "Não foi possível deletar a avaliação: " + resultado);
                }
            }
        );
    }

    private void handleEditA(Avaliacao avaliacao) {
        try {
            String fxmlPath = "/static/formulario_avaliacao.fxml";
            NavigationManager.getInstance().pushHistory(fxmlPath);
            URL fxmlUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent formView = loader.load();

            FormularioAvaliacaoController controller = loader.getController();
            controller.configureParaEdicao(avaliacao, this.pacienteAtual, this.historyListener);

            Stage stage = (Stage) historicoVBox.getScene().getWindow();
            stage.setScene(new Scene(formView, 1280, 720));
            stage.setTitle("SoftFisio - Editar Avaliação");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}