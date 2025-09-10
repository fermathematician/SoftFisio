package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import models.Anexo;
import models.Paciente;
import ui.AlertFactory;
import ui.NavigationManager;
import services.ProntuarioService;

public class ProntuarioViewController implements OnHistoryChangedListener {

    // --- CAMPOS FXML GERAIS E DE OUTRAS ABAS ---
    @FXML private Label patientNameLabel;
    @FXML private Label patientInfoLabel;
    @FXML private Button backButton;
    @FXML private BorderPane prontuarioRoot;
    @FXML private TabPane mainTabPane;
    
    // --- CONTROLLERS DAS ABAS INCLUÍDAS ---
    @FXML private TreatmentViewController sessoesTabContentController;
    @FXML private AvaliacaoTabViewController avaliacaoTabViewController;
    @FXML private HistoricoTabViewController historicoTabViewController; // NOVO CONTROLLER

    // --- CAMPOS FXML DA ABA DE ANEXOS ---
    @FXML private TilePane anexosTilePane;
    @FXML private Button adicionarAnexoButton;
    @FXML private ScrollPane anexosScrollPane;
    @FXML private Label emptyAnexosLabel;
    
    private Paciente pacienteAtual;

    @Override
    public void onHistoryChanged() {
        System.out.println("DEBUG: Histórico atualizado por um evento.");

        // Delega a atualização para cada controller de aba
        if (sessoesTabContentController != null) {
            sessoesTabContentController.loadSessoes();
        }
        if (avaliacaoTabViewController != null) {
            avaliacaoTabViewController.loadAvaliacoes();
        }
        if (historicoTabViewController != null) {
            historicoTabViewController.carregarHistoricoCompleto();
        }
    }

    public void initData(Paciente paciente) {
        this.pacienteAtual = paciente;
        setupHeader();

        // Inicializa os dados em cada controller de aba
        if (sessoesTabContentController != null) {
            sessoesTabContentController.initData(paciente, this);
        }
        if (avaliacaoTabViewController != null) {
            avaliacaoTabViewController.initData(paciente, this);
        }
        if (historicoTabViewController != null) {
            historicoTabViewController.initData(paciente, this);
        }

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

            if (fxmlPath.equals("/static/main_view.fxml")) {
                stage.setTitle("SoftFisio - Lista de Pacientes");
            } else {
                stage.setTitle("SoftFisio - Pacientes de corrida");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =======================================================================
    // MÉTODOS DA ABA DE ANEXOS (Permanecem aqui)
    // =======================================================================

    private void carregarAnexos() {
        anexosTilePane.getChildren().clear();
        ProntuarioService service = new ProntuarioService();
        List<Anexo> anexos = service.getAnexos(pacienteAtual.getId());

        for (Anexo anexo : anexos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/static/anexo_card.fxml"));
                Node anexoCardNode = loader.load();
                AnexoCardController cardController = loader.getController();
                cardController.setData(anexo);
                cardController.setDeleteHandler(anexoParaDeletar -> handleDeletarAnexo(anexoParaDeletar, anexoCardNode));
                cardController.setViewHandler(this::abrirVisualizador);
                anexosTilePane.getChildren().add(anexoCardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateAnexosVisibility();
    }
    
    private void handleDeletarAnexo(Anexo anexo, Node cardNode) {
        String nomeArquivo = new File(anexo.getCaminhoArquivo()).getName();
        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deseja realmente deletar o anexo '" + nomeArquivo + "'?",
            "Esta ação é permanente e não pode ser desfeita.",
            () -> {
                ProntuarioService service = new ProntuarioService();
                String resultado = service.deletarAnexo(anexo.getId());
                if (resultado.isEmpty()) {
                    anexosTilePane.getChildren().remove(cardNode);
                    updateAnexosVisibility();
                } else {
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
        // ... (configuração dos filtros de extensão)
        Stage stage = (Stage) adicionarAnexoButton.getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(stage);
        if (arquivoSelecionado != null) {
            String descricao = "Anexo adicionado em " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            ProntuarioService service = new ProntuarioService();
            String resultado = service.adicionarAnexo(pacienteAtual.getId(), arquivoSelecionado, descricao, null, null);
            if (resultado.isEmpty()) {
                carregarAnexos();
            } else {
                AlertFactory.showError("Erro ao Adicionar Anexo", "Não foi possível salvar o anexo: " + resultado);
            }
        }
    }
}