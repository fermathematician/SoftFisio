package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider; 

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Anexo;
import models.Avaliacao;
import models.HistoricoItem;
import models.Paciente;
import models.Sessao;
import models.Usuario;
import ui.AlertFactory;
import ui.NavigationManager;
import services.ProntuarioService;
import services.SessaoUsuario;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;

public class ProntuarioViewController implements OnHistoryChangedListener {

    @FXML private Label patientNameLabel;
    @FXML private Label patientInfoLabel;
    @FXML private Button backButton;
    @FXML private BorderPane prontuarioRoot;
    @FXML private AvaliacaoTabViewController avaliacaoTabViewController;
    @FXML private VBox historicoVBox;
    @FXML private TilePane anexosTilePane;
    @FXML private Button adicionarAnexoButton;
    @FXML private Button gerarPdfButton;
    @FXML private TreatmentViewController sessoesTabContentController;
    @FXML private ScrollPane historyScrollPane;
    @FXML private Label emptyHistoryLabel;
    // Novos FXML Fields para Anexos
    @FXML private ScrollPane anexosScrollPane;
    @FXML private Label emptyAnexosLabel;
    @FXML private TabPane mainTabPane;

    private Paciente pacienteAtual;

    @Override
    public void onHistoryChanged() {
        System.out.println("DEBUG: Histórico atualizado por um evento.");
        carregarHistoricoCompleto();

        if (sessoesTabContentController != null) {
            sessoesTabContentController.loadSessoes();
        }

        // Adicione esta linha junto com as outras chamadas de atualização
        if (avaliacaoTabViewController != null) {
            avaliacaoTabViewController.loadAvaliacoes();
}
    }

    public void initData(Paciente paciente) {
        this.pacienteAtual = paciente;
        setupHeader();

        if (sessoesTabContentController != null) {
            sessoesTabContentController.initData(paciente, this);
        }

        if (avaliacaoTabViewController != null) {
            avaliacaoTabViewController.initData(paciente, this);
        }

        carregarHistoricoCompleto();
        carregarAnexos();
    }

    private void setupHeader() {
        patientNameLabel.setText(pacienteAtual.getNomeCompleto());
        int idade = Period.between(pacienteAtual.getDataNascimento(), LocalDateTime.now().toLocalDate()).getYears();
        patientInfoLabel.setText("CPF: " + pacienteAtual.getCpf() + " | Idade: " + idade + " anos");
    }

    @FXML
    private void handleBackButton() {
        try {
            String fxmlPath = NavigationManager.getInstance().getPreviousPage();
            Parent patientsView = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(patientsView, 1280, 720));

            if(fxmlPath.equals("/static/main_view.fxml")) {
                stage.setTitle("SoftFisio - Lista de Pacientes");
            }else {
                stage.setTitle("SoftFisio - Pacientes de corrida");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarHistoricoCompleto() {
        historicoVBox.getChildren().clear();
        ProntuarioService prontuarioService = new ProntuarioService();
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
            deleteIcon.setOnMouseClicked(event -> handleDelete(sessao));

            // Cria um WebView para a evolução da sessão
            WebView webView = new WebView();
            webView.getEngine().loadContent(sessao.getEvolucaoTexto());
            webView.setPrefHeight(150); // Altura preferencial para o conteúdo
            card.getChildren().add(webView);

        } else if (item instanceof Avaliacao) {
            Avaliacao avaliacao = (Avaliacao) item;
            deleteIcon.setOnMouseClicked(event -> handleDeleteA(avaliacao));

            // Cria um VBox para organizar os campos da avaliação
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

    private void updateHistoryVisibility() {
        boolean isEmpty = historicoVBox.getChildren().isEmpty();
        emptyHistoryLabel.setVisible(isEmpty);
        emptyHistoryLabel.setManaged(isEmpty);
        historyScrollPane.setVisible(!isEmpty);
        historyScrollPane.setManaged(!isEmpty);
    }
    
    private void carregarAnexos() {
        // 1. Limpa a tela
        anexosTilePane.getChildren().clear();

        // 2. Instancia o serviço e busca os dados (seu padrão)
        ProntuarioService service = new ProntuarioService();
        List<Anexo> anexos = service.getAnexos(pacienteAtual.getId());

        // 3. Itera sobre a lista, carrega o FXML do card e o configura
        for (Anexo anexo : anexos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/anexo_card.fxml")); // Verifique o caminho
                Node anexoCardNode = loader.load();

                AnexoCardController cardController = loader.getController();

                // Popula o card com os dados. O controller agora sabe como se exibir.
                cardController.setData(anexo);

                // Configura o que fazer ao clicar em DELETAR
                cardController.setDeleteHandler(anexoParaDeletar -> {
                    handleDeletarAnexo(anexoParaDeletar, anexoCardNode);
                });

                // Configura o que fazer ao clicar em VISUALIZAR (no card em si)
                cardController.setViewHandler(anexoParaVisualizar -> {
                    abrirVisualizador(anexoParaVisualizar);
                });

                // Adiciona o card pronto à tela
                anexosTilePane.getChildren().add(anexoCardNode);

            } catch (IOException e) {
                System.err.println("Erro ao carregar o FXML do card de anexo: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 4. Atualiza a visibilidade da UI
        updateAnexosVisibility();
    }

    
    private void handleDeletarAnexo(Anexo anexo, Node cardNode) {
        String nomeArquivo = new File(anexo.getCaminhoArquivo()).getName();

        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deseja realmente deletar o anexo '" + nomeArquivo + "'?",
            "Esta ação é permanente e não pode ser desfeita.",
            () -> { // Ação a ser executada no "OK"
                
                // 1. Crie uma instância do serviço aqui
                ProntuarioService service = new ProntuarioService();

                // 2. Chame o método e guarde o resultado (que é uma String)
                String resultado = service.deletarAnexo(anexo.getId());

                // 3. Verifique se a string de resultado está vazia (indicando sucesso)
                if (resultado.isEmpty()) {
                    // Se a exclusão no banco for bem-sucedida, remove o card da tela
                    anexosTilePane.getChildren().remove(cardNode);
                    // Atualiza a visibilidade, caso este fosse o último anexo
                    updateAnexosVisibility();
                } else {
                    // Se a string não estiver vazia, ela contém a mensagem de erro
                    AlertFactory.showError("Erro", "Ocorreu um erro ao tentar deletar o anexo: " + resultado);
                }
            }
        );
    }



    private void updateAnexosVisibility() {
        boolean isEmpty = anexosTilePane.getChildren().isEmpty();
        emptyAnexosLabel.setVisible(isEmpty);
        emptyAnexosLabel.setManaged(isEmpty);
        anexosScrollPane.setVisible(!isEmpty);
        anexosScrollPane.setManaged(!isEmpty);
    }

    private void handleDelete(Sessao sessao) {
        // <<--- CÓDIGO ALTERADO ---<<<
        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deseja deletar a sessão de " + sessao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?",
            "Esta ação é permanente.",
            () -> { // Ação a ser executada se o usuário clicar em "OK"
                ProntuarioService prontuarioService = new ProntuarioService();
                String resultado = prontuarioService.deletarSessao(sessao.getId());
                if (resultado.isEmpty()) { 
                    onHistoryChanged();
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
            Parent newPatient = loader.load();

            SessaoController controller = loader.getController();
            controller.initData(sessao, this.pacienteAtual, this);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(newPatient, 1280, 720));
            stage.setTitle("SoftFisio - Editar Sessão");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private VBox createCampo(String titulo, String texto) {
        VBox campo = new VBox(2);
        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-weight: bold;");
        Label textoLabel = new Label(texto);
        textoLabel.setWrapText(true);
        campo.getChildren().addAll(tituloLabel, textoLabel);
        return campo;
    }

    private void handleDeleteA(Avaliacao avaliacao) {
        // <<--- CÓDIGO ALTERADO ---<<<
        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deseja deletar a avaliação de " + avaliacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?",
            "Esta ação é permanente.",
            () -> {
                ProntuarioService prontuarioService = new ProntuarioService();
                String resultado = prontuarioService.deletarAvaliacao(avaliacao.getId());
                if (resultado.isEmpty()) {
                    onHistoryChanged();
                } else {
                    AlertFactory.showError("Erro ao Deletar", "Não foi possível deletar a avaliação: " + resultado);
                }
            }
        );
    }

    private void handleEditA(Avaliacao avaliacao) {
    // Ação mais simples e intuitiva: apenas muda para a aba de Avaliações.
    // O usuário verá a lista e poderá clicar em "Editar" no card desejado.
    if (mainTabPane != null) {
        // O índice '1' assume que "Avaliação" é a segunda aba (0 = Sessões, 1 = Avaliação)
        mainTabPane.getSelectionModel().select(1);
    }
}

    private void abrirVisualizador(Anexo anexo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/VisualizadorMidia.fxml"));
            Parent root = loader.load();
            VisualizadorMidiaController controller = loader.getController();
            controller.initData(anexo.getCaminhoArquivo());
            Stage stage = new Stage();
            stage.setTitle("Visualizador de Anexo");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(anexosTilePane.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdicionarAnexo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Anexo");
        FileChooser.ExtensionFilter combinedFilter = new FileChooser.ExtensionFilter(
            "Todas as Mídias Suportadas (*.png, *.jpg, *.mp4, ...)", 
            "*.png", "*.jpg", "*.jpeg", "*.gif", 
            "*.mp4", "*.mov", "*.avi"
        );
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Imagens (*.png, *.jpg, *.gif)", "*.png", "*.jpg", "*.jpeg", "*.gif");
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Vídeos (*.mp4, *.mov, *.avi)", "*.mp4", "*.mov", "*.avi");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("Todos os Arquivos (*.*)", "*.*");
        fileChooser.getExtensionFilters().addAll(combinedFilter, imageFilter, videoFilter, allFilter);
        Stage stage = (Stage) adicionarAnexoButton.getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(stage);
        if (arquivoSelecionado != null) {
            String descricao = "Anexo adicionado em " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            ProntuarioService service = new ProntuarioService();
            String resultado = service.adicionarAnexo(pacienteAtual.getId(), arquivoSelecionado, descricao, null, null);
            if (resultado.isEmpty()) {
                carregarAnexos();
            } else {
                // <<--- CÓDIGO ALTERADO ---<<<
                AlertFactory.showError("Erro ao Adicionar Anexo", "Não foi possível salvar o anexo: " + resultado);
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

    @FXML
    public void handleGerarPdf() {
        Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuarioLogado();
        if (usuarioLogado == null) {
            AlertFactory.showError("Erro de Autenticação", "Nenhum usuário logado. Não é possível gerar o relatório.");
            return;
        }

        ProntuarioService prontuarioService = new ProntuarioService();
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
        Stage stage = (Stage) prontuarioRoot.getScene().getWindow();
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
        } else {
            System.out.println("Geração de PDF cancelada pelo usuário.");
        }
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
            return null;
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

   private VBox createCampoWebView(String titulo, String htmlContent) {
        VBox campo = new VBox(2);
        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-weight: bold;");

        WebView webView = new WebView();

 
        String contentToShow = (htmlContent == null || htmlContent.isEmpty() || htmlContent.contains("<body contenteditable=\"true\"></body>")) ? "" : htmlContent;
        webView.getEngine().loadContent(contentToShow);

        webView.setPrefHeight(75); // Altura padrão para cada campo

        campo.getChildren().addAll(tituloLabel, webView);
        return campo; // <-- SEMPRE retorna um VBox válido
    }
}