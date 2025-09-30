package controllers;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
import javafx.scene.control.ScrollPane;
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
    @FXML private ScrollPane historyScrollPane; // <-- ADICIONE ESTA LINHA


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

        // --- CABEÇALHO DO CARD ---
        
        // DECLARAÇÕES MOVIDAS PARA O TOPO (CORREÇÃO)
        Label tipoLabel = new Label(item.getTipo());
        tipoLabel.getStyleClass().add("card-title");
        
        Label dataLabel = new Label(item.getData().format(formatter));
        dataLabel.getStyleClass().add("card-subtitle");
        
        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);
        
        Region deleteIcon = new Region();
        deleteIcon.getStyleClass().add("delete-icon");

        // AGORA AS VARIÁVEIS JÁ EXISTEM QUANDO O HBOX É CRIADO
        HBox header = new HBox(tipoLabel, new Label(" - "), dataLabel, hSpacer, deleteIcon);
        header.setAlignment(Pos.CENTER_LEFT);
        
        card.getChildren().addAll(header, new Separator());

        // --- CORPO DO CARD ---
        if (item instanceof Sessao) {
            Sessao sessao = (Sessao) item;
            deleteIcon.setOnMouseClicked(event -> handleDelete(sessao));
            card.getChildren().add(createCampoWebView("Evolução:", sessao.getEvolucaoTexto()));
        } else if (item instanceof Avaliacao) {
            Avaliacao avaliacao = (Avaliacao) item;
            deleteIcon.setOnMouseClicked(event -> handleDeleteA(avaliacao));
            VBox detalhesAvaliacao = new VBox(8);
            detalhesAvaliacao.getChildren().add(createCampoWebView("Doença Atual (HDA):", avaliacao.getDoencaAtual()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("História Pregressa:", avaliacao.getHistoriaPregressa()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Inspeção e Palpação:", avaliacao.getInspecaoPalpacao()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("ADM:", avaliacao.getAdm()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Força Muscular:", avaliacao.getForcaMuscular()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Avaliação Funcional:", avaliacao.getAvaliacaoFuncional()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Testes Especiais:", avaliacao.getTestesEspeciais()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Escalas Funcionais:", avaliacao.getEscalasFuncionais()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Diagnóstico Cinesiológico:", avaliacao.getDiagnosticoCinesiologico()));
            detalhesAvaliacao.getChildren().add(createCampoWebView("Plano de Tratamento:", avaliacao.getPlanoTratamento()));
            card.getChildren().add(detalhesAvaliacao);
        }
        
        // --- RODAPÉ DO CARD ---
        Region vSpacer = new Region();
        VBox.setVgrow(vSpacer, Priority.ALWAYS);
        card.getChildren().add(vSpacer);

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("primary-button");
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
        webView.setContextMenuEnabled(false);
        
        // Adiciona o filtro de scroll para uma melhor experiência de usuário
        webView.addEventFilter(javafx.scene.input.ScrollEvent.ANY, event -> {
                // O historicoVBox precisa ser o fx:id do VBox que contém os cards
                if (historicoVBox != null) {
                    historicoVBox.fireEvent(event.copyFor(event.getSource(), historicoVBox));
                }
                event.consume();
        });

        String contentToShow = (htmlContent == null || htmlContent.isEmpty() || htmlContent.trim().equals("<body contenteditable=\"true\"></body>")) 
                                ? "<i>Não informado</i>" 
                                : htmlContent;

        webView.getEngine().loadContent(contentToShow);
        
        // AQUI ESTÁ A ÚNICA MUDANÇA: VOLTAMOS PARA UMA ALTURA FIXA
        webView.setPrefHeight(200); // Altura preferencial como era antes

        campo.getChildren().addAll(tituloLabel, webView);
        return campo;
    }
    private void handleDelete(Sessao sessao) {
        AlertFactory.showConfirmation("Confirmar Exclusão", "Deseja deletar a sessão de " + sessao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?", "Esta ação é permanente.", () -> {
            String resultado = prontuarioService.deletarSessao(sessao.getId());
            if (resultado.isEmpty() && historyListener != null) {
                historyListener.onHistoryChanged();
            } else if (!resultado.isEmpty()) {
                AlertFactory.showError("Erro ao Deletar", "Não foi possível deletar a sessão: " + resultado);
            }
        });
    }

    private void handleEdit(Sessao sessao) {
        try {
            String fxmlPath = "/static/formulario_sessao.fxml";
            NavigationManager.getInstance().pushHistory(fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent formView = loader.load();
            SessaoController controller = loader.getController();
            controller.initData(sessao, this.pacienteAtual, this.historyListener);
            Stage stage = (Stage) historicoVBox.getScene().getWindow();
            stage.setScene(new Scene(formView));
            stage.setMaximized(true);
            stage.setTitle("SoftFisio - Editar Sessão");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteA(Avaliacao avaliacao) {
        AlertFactory.showConfirmation("Confirmar Exclusão", "Deseja deletar a avaliação de " + avaliacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?", "Esta ação é permanente.", () -> {
            String resultado = prontuarioService.deletarAvaliacao(avaliacao.getId());
            if (resultado.isEmpty() && historyListener != null) {
                historyListener.onHistoryChanged();
            } else if (!resultado.isEmpty()) {
                AlertFactory.showError("Erro ao Deletar", "Não foi possível deletar a avaliação: " + resultado);
            }
        });
    }

    private void handleEditA(Avaliacao avaliacao) {
        try {
            String fxmlPath = "/static/formulario_avaliacao.fxml";
            NavigationManager.getInstance().pushHistory(fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent formView = loader.load();
            FormularioAvaliacaoController controller = loader.getController();
            controller.configureParaEdicao(avaliacao, this.pacienteAtual, this.historyListener);
            Stage stage = (Stage) historicoVBox.getScene().getWindow();
            stage.setScene(new Scene(formView));
            stage.setMaximized(true);
            stage.setTitle("SoftFisio - Editar Avaliação");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGerarPdf() {
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
        Stage stage = (Stage) historicoVBox.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PdfWriter writer = new PdfWriter(file); PdfDocument pdf = new PdfDocument(writer); Document document = new Document(pdf)) {
                document.setMargins(30, 30, 30, 30);
                
                ConverterProperties converterProperties = new ConverterProperties();
                DefaultFontProvider fontProvider = new DefaultFontProvider();
                converterProperties.setFontProvider(fontProvider);

                document.add(new Paragraph(usuarioLogado.getNomeCompleto()).setTextAlignment(TextAlignment.CENTER).setBold());
                document.add(new Paragraph("RELATÓRIO FISIOTERAPÊUTICO").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(14).setMarginTop(10));
                
                document.add(new Paragraph("IDENTIFICAÇÃO").setBold().setMarginTop(20));
                document.add(criarCampoPdf("Nome:", pacienteAtual.getNomeCompleto()));
                document.add(criarCampoPdf("Data de Nascimento:", pacienteAtual.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                document.add(criarCampoPdf("Sexo:", pacienteAtual.getGenero()));
                document.add(criarCampoPdf("Telefone:", pacienteAtual.getTelefone()));

                document.add(new Paragraph("HISTÓRICO COMPLETO").setBold().setMarginTop(25).setMarginBottom(10));
                
                historico.sort(Comparator.comparing(HistoricoItem::getData)); // PDF em ordem cronológica
                DateTimeFormatter formatterItem = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
                for (HistoricoItem item : historico) {
                    document.add(new Paragraph(" ").setBorderTop(new SolidBorder(0.5f)).setMarginTop(10).setMarginBottom(10));
                    document.add(new Paragraph().add(new Text(item.getTipo().toUpperCase()).setBold()).add(" - " + item.getData().format(formatterItem)));
                    
                    if (item instanceof Sessao) {
                        adicionarCampoHtmlAoPdf(document, "Evolução:", ((Sessao) item).getEvolucaoTexto(), converterProperties);
                    } else if (item instanceof Avaliacao) {
                        Avaliacao a = (Avaliacao) item;
                        
                        adicionarCampoHtmlAoPdf(document, "Doença Atual (HDA):", a.getDoencaAtual(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "História Pregressa:", a.getHistoriaPregressa(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Inspeção e Palpação:", a.getInspecaoPalpacao(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "ADM:", a.getAdm(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Força Muscular:", a.getForcaMuscular(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Avaliação Funcional:", a.getAvaliacaoFuncional(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Testes Especiais:", a.getTestesEspeciais(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Escalas Funcionais:", a.getEscalasFuncionais(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Diagnóstico Cinesiológico:", a.getDiagnosticoCinesiologico(), converterProperties);
                        adicionarCampoHtmlAoPdf(document, "Plano de Tratamento:", a.getPlanoTratamento(), converterProperties);
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
        if (texto == null || texto.trim().isEmpty()) return null;
        return new Paragraph().add(new Text(titulo).setBold()).add(" " + texto);
    }

    private void adicionarCampoHtmlAoPdf(Document document, String titulo, String htmlContent, ConverterProperties converterProperties) {
        if (htmlContent == null || htmlContent.trim().isEmpty() || htmlContent.contains("<body contenteditable=\"true\"></body>")) {
            return;
        }

        document.add(new Paragraph().add(new Text(titulo).setBold()));
        
        List<IElement> elements = HtmlConverter.convertToElements(corrigirHtmlParaPdf(htmlContent), converterProperties);
        for (IElement element : elements) {
            document.add((com.itextpdf.layout.element.BlockElement) element);
        }
    }

    private String corrigirHtmlParaPdf(String html) {
        if (html == null) return "";
        return html.replace("<font size=\"1\">", "<span style=\"font-size: 8pt;\">")
                   .replace("<font size=\"2\">", "<span style=\"font-size: 10pt;\">")
                   .replace("<font size=\"3\">", "<span style=\"font-size: 12pt;\">")
                   .replace("<font size=\"4\">", "<span style=\"font-size: 14pt;\">")
                   .replace("<font size=\"5\">", "<span style=\"font-size: 18pt;\">")
                   .replace("<font size=\"6\">", "<span style=\"font-size: 24pt;\">")
                   .replace("<font size=\"7\">", "<span style=\"font-size: 36pt;\">")
                   .replace("</font>", "</span>")
                   .replaceAll("font-size: -webkit-xxx-large", "font-size: 36pt");
    }
}