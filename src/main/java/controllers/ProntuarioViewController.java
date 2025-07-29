package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.Paciente;

import java.util.ArrayList;
import java.util.Comparator;
import models.HistoricoItem;
import models.Avaliacao;
import models.Sessao;
import services.ProntuarioService;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;

import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import java.io.File;
import models.Anexo;
import javafx.geometry.Insets;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Modality;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import javafx.stage.FileChooser;

import models.Usuario;
import services.SessaoUsuario;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.borders.Border;
import java.time.format.DateTimeFormatter;

public class ProntuarioViewController implements OnHistoryChangedListener {

    //--- Componentes da tela "Pai" ---
    @FXML private Label patientNameLabel;
    @FXML private Label patientInfoLabel;
    @FXML private Button backButton;
    @FXML private BorderPane prontuarioRoot;
    @FXML private AvaliacaoViewController avaliacaoTabContentController;
    @FXML private VBox historicoVBox;
    @FXML private TilePane anexosTilePane;
    @FXML private Button adicionarAnexoButton;
    @FXML private Button gerarPdfButton;

    //--- Injeção do conteúdo e do controller da aba "Sessões" ---
    // O fx:id do <fx:include> é "sessoesTabContent"
    // O JavaFX injeta seu controller em uma variável com o sufixo "Controller"
    @FXML private TreatmentViewController sessoesTabContentController;

    private Paciente pacienteAtual;

    /**
     * Ponto de entrada chamado pelo PatientCardController.
     * Recebe os dados do paciente e os distribui para a tela pai e os filhos.
     */
@Override
public void onHistoryChanged() {
    System.out.println("DEBUG: Histórico atualizado por um evento.");
    carregarHistoricoCompleto();
}

public void initData(Paciente paciente) {
    this.pacienteAtual = paciente;
    setupHeader();

    if (sessoesTabContentController != null) {
        sessoesTabContentController.initData(paciente, this); // Passa o listener
    }

    if (avaliacaoTabContentController != null) {
        avaliacaoTabContentController.initData(paciente, this); // Passa o listener
    }

    carregarHistoricoCompleto();
    carregarAnexos();
}

    /**
     * Preenche o nome e as informações do paciente no cabeçalho.
     */
    private void setupHeader() {
        patientNameLabel.setText(pacienteAtual.getNomeCompleto());
        int idade = Period.between(pacienteAtual.getDataNascimento(), LocalDateTime.now().toLocalDate()).getYears();
        patientInfoLabel.setText("CPF: " + pacienteAtual.getCpf() + " | Idade: " + idade + " anos");
    }

    /**
     * Ação do botão "Voltar à Lista" do cabeçalho principal.
     */
    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            URL fxmlUrl = getClass().getResource("/static/main_view.fxml");
            Parent mainView = FXMLLoader.load(fxmlUrl);
            
            stage.setScene(new Scene(mainView, 1280, 720));
            stage.setTitle("SoftFisio - Lista de pacientes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Sessao sessao) {
    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmationAlert.setTitle("Confirmar Exclusão");
    confirmationAlert.setHeaderText("Deseja deletar a sessão de " + sessao.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?");
    confirmationAlert.setContentText("Esta ação é permanente.");

    Optional<ButtonType> result = confirmationAlert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        ProntuarioService prontuarioService = new ProntuarioService();
        String resultado = prontuarioService.deletarSessao(sessao.getId());
        if (resultado.isEmpty()) { 
            carregarHistoricoCompleto(); // Recarrega a lista
        } else { 
            // Mostra alerta de erro
        }
    }
}

private void handleEdit(Sessao sessao) {
    try {
        URL fxmlUrl = getClass().getResource("/static/editar_sessao.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        EditarSessaoController controller = loader.getController();
        controller.initData(sessao, this.pacienteAtual);

        Stage stage = (Stage) historicoVBox.getScene().getWindow();
        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle("SoftFisio - Editar Sessão");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private VBox createCampo(String titulo, String texto) {
    VBox campo = new VBox(2);
    Label tituloLabel = new Label(titulo);
    tituloLabel.setStyle("-fx-font-weight: bold;"); // Deixa o título em negrito

    Label textoLabel = new Label(texto);
    textoLabel.setWrapText(true);

    campo.getChildren().addAll(tituloLabel, textoLabel);
    return campo;
}

private void handleDeleteA(Avaliacao avaliacao) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmar Exclusão");
        confirmationAlert.setHeaderText("Deseja deletar a avaliação de " + avaliacao.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "?");
        confirmationAlert.setContentText("Esta ação é permanente.");
        
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ProntuarioService prontuarioService = new ProntuarioService();
            String resultado = prontuarioService.deletarAvaliacao(avaliacao.getId());
            if (resultado.isEmpty()) { 
                carregarHistoricoCompleto(); // Recarrega a lista
            } else { 
                System.err.println(resultado);
            }
        }
    }

    private void handleEditA(Avaliacao avaliacao) {
    try {
        URL fxmlUrl = getClass().getResource("/static/editar_avaliacao.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Pega o controller da nova tela de edição
        EditarAvaliacaoController controller = loader.getController();

        // Passa os dados necessários: a avaliação a ser editada e o paciente atual
        controller.initData(avaliacao, this.pacienteAtual);

        // Exibe a nova cena
        Stage stage = (Stage) historicoVBox.getScene().getWindow();
        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle("SoftFisio - Editar Avaliação");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


   // SUBSTITUA O MÉTODO INTEIRO POR ESTE
private void carregarHistoricoCompleto() {
    // 1. Limpa o histórico anterior
    historicoVBox.getChildren().clear();

    // 2. Busca as listas
    ProntuarioService prontuarioService = new ProntuarioService();
    List<HistoricoItem> historico = new ArrayList<>();
    historico.addAll(prontuarioService.getSessoes(pacienteAtual.getId()));
    historico.addAll(prontuarioService.getAvaliacoes(pacienteAtual.getId()));

    // 3. Ordena a lista
    historico.sort(Comparator.comparing(HistoricoItem::getDataHora).reversed());

    // 4. Cria os cards
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'às' HH:mm");

    for (HistoricoItem item : historico) {
        VBox card = new VBox(10);
        card.getStyleClass().add("patient-card");

        // Cabeçalho do Card
        Label tipoLabel = new Label(item.getTipo());
        tipoLabel.getStyleClass().add("card-title");
        Label dataLabel = new Label(item.getDataHora().format(formatter));
        dataLabel.getStyleClass().add("card-subtitle");
        HBox header = new HBox(tipoLabel, new Label(" - "), dataLabel);
        card.getChildren().add(header);

        // Conteúdo específico do Card
        if (item instanceof Sessao) {
            Sessao sessao = (Sessao) item;
            Label conteudo = new Label(sessao.getEvolucaoTexto());
            conteudo.setWrapText(true);

            // --- MUDANÇA 2: Adicionando botões de ação para Sessão ---
            Region vSpacer = new Region();
            VBox.setVgrow(vSpacer, Priority.ALWAYS);

            Region deleteIcon = new Region();
            deleteIcon.getStyleClass().add("delete-icon");
            deleteIcon.setOnMouseClicked(event -> handleDelete(sessao));

            Button editButton = new Button("Editar");
            editButton.getStyleClass().add("primary-button");
            editButton.setOnAction(event -> handleEdit(sessao));

            HBox bottomBar = new HBox(10, vSpacer, editButton, deleteIcon);
            bottomBar.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(new Separator(), conteudo, bottomBar);

        } else if (item instanceof Avaliacao) {
            Avaliacao avaliacao = (Avaliacao) item;
            VBox detalhesAvaliacao = new VBox(8); // Aumentando o espaçamento

            // Helper para criar os campos de forma padronizada
            // Você pode adicionar um `if` para não mostrar campos vazios, se preferir
            detalhesAvaliacao.getChildren().add(createCampo("Queixa Principal:", avaliacao.getQueixaPrincipal()));
            detalhesAvaliacao.getChildren().add(createCampo("Histórico da Doença:", avaliacao.getHistoricoDoencaAtual()));
            detalhesAvaliacao.getChildren().add(createCampo("Exames Físicos:", avaliacao.getExamesFisicos()));
            detalhesAvaliacao.getChildren().add(createCampo("Diagnóstico Fisioterapêutico:", avaliacao.getDiagnosticoFisioterapeutico()));
            detalhesAvaliacao.getChildren().add(createCampo("Plano de Tratamento:", avaliacao.getPlanoTratamento()));

                    // --- TRECHO NOVO ADICIONADO AQUI ---
            Region vSpacer = new Region();
            VBox.setVgrow(vSpacer, Priority.ALWAYS);

            Region deleteIcon = new Region();
            deleteIcon.getStyleClass().add("delete-icon");
            deleteIcon.setOnMouseClicked(event -> handleDeleteA(avaliacao)); // Chama o novo método handleDelete

            Button editButton = new Button("Editar");
            editButton.getStyleClass().add("primary-button");
            editButton.setOnAction(event -> handleEditA(avaliacao)); // Chama o novo método handleEdit

            HBox bottomBar = new HBox(10, vSpacer, editButton, deleteIcon);
            bottomBar.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(new Separator(), detalhesAvaliacao, bottomBar);
        }

        historicoVBox.getChildren().add(card);
    }
}

// SUBSTITUA O MÉTODO INTEIRO POR ESTE
private void carregarAnexos() {
    anexosTilePane.getChildren().clear();
    ProntuarioService service = new ProntuarioService();
    List<Anexo> anexos = service.getAnexos(pacienteAtual.getId());

    for (Anexo anexo : anexos) {
        VBox cardAnexo = new VBox(5);
        cardAnexo.getStyleClass().add("patient-card");
        cardAnexo.setPadding(new Insets(10));
        cardAnexo.setPrefWidth(220);
        cardAnexo.setAlignment(Pos.CENTER);

        // --- LÓGICA ATUALIZADA ---
        if (anexo.getTipoMidia().equalsIgnoreCase("FOTO")) {
            try {
                File file = new File(anexo.getCaminhoArquivo());
                Image image = new Image(file.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(150);
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);

                imageView.setOnMouseClicked(event -> abrirVisualizador(anexo));
                
                cardAnexo.getChildren().add(imageView);

            } catch (Exception e) {
                Label erroLabel = new Label("Erro ao carregar imagem");
                cardAnexo.getChildren().add(erroLabel);
                e.printStackTrace();
            }
        } else if (anexo.getTipoMidia().equalsIgnoreCase("VIDEO")) {
            // Se for vídeo, cria um placeholder visual
            Region videoIcon = new Region();
            videoIcon.getStyleClass().add("video-icon"); // Estilo que vamos criar no CSS
            videoIcon.setPrefSize(200, 150);
            
            videoIcon.setOnMouseClicked(event -> abrirVisualizador(anexo)); // Também abre o visualizador

            cardAnexo.getChildren().add(videoIcon);
        }
        // --- FIM DA LÓGICA ATUALIZADA ---

        Label nomeArquivo = new Label(new File(anexo.getCaminhoArquivo()).getName());
        nomeArquivo.setStyle("-fx-font-weight: bold;");
        nomeArquivo.setWrapText(true);

        Label descricao = new Label(anexo.getDescricao());
        descricao.setWrapText(true);
        
        cardAnexo.getChildren().addAll(new Separator(), nomeArquivo, descricao);
        anexosTilePane.getChildren().add(cardAnexo);
    }
}

// MÉTODO "handleAdicionarAnexo" renomeado e um novo método "abrirVisualizador" foi criado
// para evitar duplicação de código
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

        // --- LÓGICA DE FILTROS ATUALIZADA ---
        
        // 1. Cria um novo filtro combinado que será o padrão
        FileChooser.ExtensionFilter combinedFilter = new FileChooser.ExtensionFilter(
            "Todas as Mídias Suportadas (*.png, *.jpg, *.mp4, ...)", 
            "*.png", "*.jpg", "*.jpeg", "*.gif", 
            "*.mp4", "*.mov", "*.avi"
        );

        // Cria os filtros específicos
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Imagens (*.png, *.jpg, *.gif)", "*.png", "*.jpg", "*.jpeg", "*.gif");
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Vídeos (*.mp4, *.mov, *.avi)", "*.mp4", "*.mov", "*.avi");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("Todos os Arquivos (*.*)", "*.*");

        // 2. Adiciona o filtro combinado PRIMEIRO na lista para que ele seja o padrão
        fileChooser.getExtensionFilters().addAll(combinedFilter, imageFilter, videoFilter, allFilter);
        // --- FIM DA LÓGICA DE FILTROS ---

        Stage stage = (Stage) adicionarAnexoButton.getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(stage);

        if (arquivoSelecionado != null) {
            String descricao = "Anexo adicionado em " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            ProntuarioService service = new ProntuarioService();
            String resultado = service.adicionarAnexo(pacienteAtual.getId(), arquivoSelecionado, descricao, null, null);

            if (resultado.isEmpty()) {
                carregarAnexos();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro ao Adicionar Anexo");
                alert.setHeaderText("Não foi possível salvar o anexo.");
                alert.setContentText(resultado);
                alert.showAndWait();
            }
        }
    }

    /**
     * Cria um parágrafo para o PDF com um título em negrito e o texto do conteúdo.
     * @param titulo O título do campo (ex: "Nome:").
     * @param texto O conteúdo do campo.
     * @return um Parágrafo estilizado para o iText.
     */
    private Paragraph criarCampoPdf(String titulo, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null; // Não adiciona o campo se o texto estiver vazio
        }
        Paragraph p = new Paragraph();
        Text tituloBold = new Text(titulo).setBold();
        p.add(tituloBold);
        p.add(" " + texto); // Adiciona um espaço entre o título e o texto
        return p;
    }


    @FXML
    public void handleGerarPdf() {
        Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuarioLogado();
        if (usuarioLogado == null) {
            new Alert(Alert.AlertType.ERROR, "Nenhum usuário logado. Não é possível gerar o relatório.").showAndWait();
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
                document.setMargins(30, 30, 30, 30); // Define margens para o documento

                // --- 1. CABEÇALHO ---
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

                // --- 2. IDENTIFICAÇÃO DO PACIENTE ---
                Paragraph idTitulo = new Paragraph("IDENTIFICAÇÃO")
                        .setBold()
                        .setMarginTop(20);
                document.add(idTitulo);
                document.add(criarCampoPdf("Nome:", pacienteAtual.getNomeCompleto()));
                
                DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                document.add(criarCampoPdf("Data de Nascimento:", pacienteAtual.getDataNascimento().format(formatterData)));
                document.add(criarCampoPdf("Sexo:", pacienteAtual.getGenero()));
                document.add(criarCampoPdf("Telefone:", pacienteAtual.getTelefone()));


                // --- 3. HISTÓRICO COMPLETO ---
                Paragraph historicoTitulo = new Paragraph("HISTÓRICO COMPLETO")
                        .setBold()
                        .setMarginTop(25)
                        .setMarginBottom(10);
                document.add(historicoTitulo);

                // Reutiliza a lógica para buscar e ordenar o histórico
                ProntuarioService prontuarioService = new ProntuarioService();
                List<HistoricoItem> historico = new ArrayList<>();
                historico.addAll(prontuarioService.getSessoes(pacienteAtual.getId()));
                historico.addAll(prontuarioService.getAvaliacoes(pacienteAtual.getId()));
                historico.sort(Comparator.comparing(HistoricoItem::getDataHora).reversed());

                DateTimeFormatter formatterItem = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

                for (HistoricoItem item : historico) {
                    // Adiciona uma linha separadora antes de cada item do histórico
                    document.add(new Paragraph(" ").setBorderTop(new SolidBorder(0.5f)).setMarginTop(10).setMarginBottom(10));

                    Paragraph tipoData = new Paragraph()
                            .add(new Text(item.getTipo().toUpperCase()).setBold())
                            .add(" - " + item.getDataHora().format(formatterItem));
                    document.add(tipoData);

                    if (item instanceof Sessao) {
                        Sessao sessao = (Sessao) item;
                        document.add(criarCampoPdf("Evolução:", sessao.getEvolucaoTexto()));
                    } else if (item instanceof Avaliacao) {
                        Avaliacao avaliacao = (Avaliacao) item;
                        document.add(criarCampoPdf("Queixa Principal:", avaliacao.getQueixaPrincipal()));
                        document.add(criarCampoPdf("Histórico da Doença:", avaliacao.getHistoricoDoencaAtual()));
                        document.add(criarCampoPdf("Exames Físicos:", avaliacao.getExamesFisicos()));
                        document.add(criarCampoPdf("Diagnóstico Fisioterapêutico:", avaliacao.getDiagnosticoFisioterapeutico()));
                        document.add(criarCampoPdf("Plano de Tratamento:", avaliacao.getPlanoTratamento()));
                    }
                }
                
                // Fecha o documento para salvar
                document.close();

                // Mostra um alerta de sucesso
                new Alert(Alert.AlertType.INFORMATION, "PDF gerado com sucesso em: " + file.getAbsolutePath()).showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Ocorreu um erro ao gerar o PDF: " + e.getMessage()).showAndWait();
            }
        } else {
            System.out.println("Geração de PDF cancelada pelo usuário.");
        }
    }
}