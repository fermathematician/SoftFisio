package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// --- Imports de iTextPDF e outros ---
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import models.Avaliacao;
import models.HistoricoItem;
import models.Paciente;
import models.Sessao;
import models.Usuario;
import services.ProntuarioService;
import services.SessaoUsuario;
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

     @FXML
    public void handleGerarPdf() {
        Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuarioLogado();
        if (usuarioLogado == null) {
            AlertFactory.showError("Erro de Autenticação", "Nenhum usuário logado. Não é possível gerar o relatório.");
            return;
        }

        List<HistoricoItem> historico = new ArrayList<>();
        historico.addAll(prontuarioService.getSessoes(pacienteAtual.getId()));
        historico.addAll(prontuarioService.getAvaliacoes(pacienteAtual.getId()));

        if (historico.isEmpty()) {
            AlertFactory.showError("Atenção", "Não é possível gerar o relatório pois o paciente não possui nenhum histórico.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório em PDF");
        fileChooser.setInitialFileName("relatorio_" + pacienteAtual.getNomeCompleto().replaceAll("\\s+", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos PDF (*.pdf)", "*.pdf"));
        
        // --- AJUSTE IMPORTANTE: Pegando o Stage a partir de um elemento da aba de histórico ---
        Stage stage = (Stage) historicoVBox.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                document.setMargins(30, 30, 30, 30);

                ConverterProperties converterProperties = new ConverterProperties();
                DefaultFontProvider fontProvider = new DefaultFontProvider();
                converterProperties.setFontProvider(fontProvider);

                Paragraph nomeFisio = new Paragraph(usuarioLogado.getNomeCompleto())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold();
                document.add(nomeFisio);

                Paragraph tituloRelatorio = new Paragraph("RELATÓRIO FISIOTERAPÊUTICO")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(14)
                        .setMarginTop(10);
                document.add(tituloRelatorio);
                
                Paragraph idTitulo = new Paragraph("IDENTIFICAÇÃO")
                        .setBold()
                        .setMarginTop(20);
                document.add(idTitulo);
                document.add(criarCampoPdf("Nome:", pacienteAtual.getNomeCompleto()));
                DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                document.add(criarCampoPdf("Data de Nascimento:", pacienteAtual.getDataNascimento().format(formatterData)));
                document.add(criarCampoPdf("Sexo:", pacienteAtual.getGenero()));
                document.add(criarCampoPdf("Telefone:", pacienteAtual.getTelefone()));

                Paragraph historicoTitulo = new Paragraph("HISTÓRICO COMPLETO")
                        .setBold()
                        .setMarginTop(25)
                        .setMarginBottom(10);
                document.add(historicoTitulo);
                
                historico.sort(Comparator.comparing(HistoricoItem::getData).reversed());
                DateTimeFormatter formatterItem = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
                for (HistoricoItem item : historico) {
                    document.add(new Paragraph(" ").setBorderTop(new SolidBorder(0.5f)).setMarginTop(10).setMarginBottom(10));
                    Paragraph tipoData = new Paragraph()
                            .add(new Text(item.getTipo().toUpperCase()).setBold())
                            .add(" - " + item.getData().format(formatterItem));
                    document.add(tipoData);
                    
                    if (item instanceof Sessao) {
                        Sessao sessao = (Sessao) item;
                        adicionarCampoHtmlAoPdf(document, "Evolução:", sessao.getEvolucaoTexto(), converterProperties);
                    } else if (item instanceof Avaliacao) {
                        Avaliacao avaliacao = (Avaliacao) item;
                        adicionarCampoHtmlAoPdf(document, "Queixa Principal:", avaliacao.getQueixaPrincipal(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Histórico da Doença:", avaliacao.getHistoricoDoencaAtual(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Exames Físicos:", avaliacao.getExamesFisicos(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Diagnóstico Fisioterapêutico:", avaliacao.getDiagnosticoFisioterapeutico(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Plano de Tratamento:", avaliacao.getPlanoTratamento(), converterProperties);
                    }
                }
                document.close();
                AlertFactory.showSuccess("PDF Gerado", "O arquivo foi salvo com sucesso em:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                AlertFactory.showError("Erro ao Gerar PDF", "Ocorreu um erro: " + e.getMessage());
            }
        }
    }

    private Paragraph criarCampoPdf(String titulo, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        Paragraph p = new Paragraph();
        Text tituloBold = new Text(titulo).setBold();
        p.add(tituloBold);
        p.add(" " + texto);
        return p;
    }

    private void adicionarCampoHtmlAoPdf(Document document, String titulo, String htmlContent, ConverterProperties converterProperties) {
        if (htmlContent == null || htmlContent.trim().isEmpty() || htmlContent.contains("<body contenteditable=\"true\"></body>")) {
            return;
        }

        Paragraph pTitulo = new Paragraph();
        Text tituloBold = new Text(titulo).setBold();
        pTitulo.add(tituloBold);
        document.add(pTitulo);

        String htmlCorrigido = corrigirHtmlParaPdf(htmlContent);

        List<IElement> elements = HtmlConverter.convertToElements(htmlCorrigido, converterProperties);

        for (IElement element : elements) {
            document.add((com.itextpdf.layout.element.BlockElement) element);
        }
    }

    private String corrigirHtmlParaPdf(String html) {
        if (html == null) {
            return "";
        }
        String correctedHtml = html
            .replace("<font size=\"1\">", "<span style=\"font-size: 8pt;\">")
            .replace("<font size=\"2\">", "<span style=\"font-size: 10pt;\">")
            .replace("<font size=\"3\">", "<span style=\"font-size: 12pt;\">")
            .replace("<font size=\"4\">", "<span style=\"font-size: 14pt;\">")
            .replace("<font size=\"5\">", "<span style=\"font-size: 18pt;\">")
            .replace("<font size=\"6\">", "<span style=\"font-size: 24pt;\">")
            .replace("<font size=\"7\">", "<span style=\"font-size: 36pt;\">")
            .replace("</font>", "</span>")
    
            .replaceAll("font-size: -webkit-xxx-large", "font-size: 36pt");

        return correctedHtml;
    
    }
}