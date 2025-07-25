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

public class ProntuarioViewController {

    //--- Componentes da tela "Pai" ---
    @FXML private Label patientNameLabel;
    @FXML private Label patientInfoLabel;
    @FXML private Button backButton;
    @FXML private BorderPane prontuarioRoot;
    @FXML private AvaliacaoViewController avaliacaoTabContentController;
    @FXML private VBox historicoVBox;

    //--- Injeção do conteúdo e do controller da aba "Sessões" ---
    // O fx:id do <fx:include> é "sessoesTabContent"
    // O JavaFX injeta seu controller em uma variável com o sufixo "Controller"
    @FXML private TreatmentViewController sessoesTabContentController;

    private Paciente pacienteAtual;

    /**
     * Ponto de entrada chamado pelo PatientCardController.
     * Recebe os dados do paciente e os distribui para a tela pai e os filhos.
     */
public void initData(Paciente paciente) {
    this.pacienteAtual = paciente;
    setupHeader();

    if (sessoesTabContentController != null) {
        sessoesTabContentController.initData(paciente);
    }

    // Adicione esta nova parte
    if (avaliacaoTabContentController != null) {
        avaliacaoTabContentController.initData(paciente);
    }

    carregarHistoricoCompleto();
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
}