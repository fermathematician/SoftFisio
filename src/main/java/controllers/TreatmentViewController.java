package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import models.Paciente;
import models.Sessao;
import services.ProntuarioService;

public class TreatmentViewController {

    // Componentes da área de informações do paciente
    @FXML private Label patientNameLabel;
    @FXML private Label patientInfoLabel;
    @FXML private Button backButton;

    // Componentes da área de nova sessão
    @FXML private TextArea newSessionTextArea;
    @FXML private Button saveSessionButton;

    // Componente que vai conter a lista de sessões
    @FXML private VBox sessionsVBox;

    private ProntuarioService prontuarioService;
    private Paciente pacienteAtual;

    public TreatmentViewController() {
        this.prontuarioService = new ProntuarioService();
    }
    
    @FXML
    public void initialize() {
        // Define o botão de salvar como padrão (ativado com Enter)
        saveSessionButton.setDefaultButton(true);
    }

    /**
     * Ponto de entrada do controlador. Recebe o paciente da tela anterior.
     */
    public void initData(Paciente paciente) {
        this.pacienteAtual = paciente;
        setupHeader();
        loadSessoes();
    }

    /**
     * Preenche o cabeçalho com as informações do paciente.
     */
    private void setupHeader() {
        patientNameLabel.setText(pacienteAtual.getNomeCompleto());
        int idade = Period.between(pacienteAtual.getDataNascimento(), LocalDateTime.now().toLocalDate()).getYears();
        patientInfoLabel.setText("CPF: " + pacienteAtual.getCpf() + " | Idade: " + idade + " anos");
    }

    /**
     * Carrega as sessões do banco de dados e as exibe na tela.
     */
    private void loadSessoes() {
        // 1. Limpa a lista atual para não duplicar conteúdo
        sessionsVBox.getChildren().clear();

        // 2. Busca as sessões do paciente através do serviço
        List<Sessao> sessoes = prontuarioService.getSessoes(pacienteAtual.getId());

        // 3. Para cada sessão na lista, cria um "card" e o adiciona ao VBox
        for (Sessao sessao : sessoes) {
            VBox sessionCard = createSessionCard(sessao);
            sessionsVBox.getChildren().add(sessionCard);
        }
    }

    /**
     * Cria um nó de interface (um "card") para representar uma única sessão.
     * @param sessao O objeto Sessao com os dados.
     * @return um VBox estilizado contendo as informações da sessão.
     */
    private VBox createSessionCard(Sessao sessao) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("patient-card");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Sessão de' dd 'de' MMMM 'de' yyyy 'às' HH:mm");
        Label dateLabel = new Label(sessao.getDataSessao().format(formatter));
        // ANTES: dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        dateLabel.getStyleClass().add("session-date-label"); // DEPOIS

        Separator separator = new Separator();
        
        Label evolutionLabel = new Label(sessao.getEvolucaoTexto());
        evolutionLabel.setWrapText(true);

        card.getChildren().addAll(dateLabel, separator, evolutionLabel);
        
        if (sessao.getObservacoesSessao() != null && !sessao.getObservacoesSessao().isEmpty()) {
            Label obsLabelTitle = new Label("Observações:");
            // ANTES: obsLabelTitle.setStyle("-fx-font-weight: bold; -fx-padding: 10px 0 0 0;");
            obsLabelTitle.getStyleClass().add("session-observation-title"); // DEPOIS

            Label obsLabel = new Label(sessao.getObservacoesSessao());
            obsLabel.setWrapText(true);
            card.getChildren().addAll(obsLabelTitle, obsLabel);
        }

        return card;
    }


    /**
     * Ação do botão "Salvar Sessão".
     */
    @FXML
    private void handleSaveSessao() {
        String textoEvolucao = newSessionTextArea.getText();
        
        // Usa o serviço para cadastrar a sessão
        String resultado = prontuarioService.cadastrarSessao(pacienteAtual.getId(), textoEvolucao, ""); // Observações podem ser adicionadas no futuro

        if (resultado.isEmpty()) {
            // Se salvou com sucesso, limpa a área de texto e recarrega a lista
            newSessionTextArea.clear();
            loadSessoes();
        } else {
            // Se deu erro, poderíamos exibir um alerta para o usuário
            System.err.println(resultado);
        }
    }

    /**
     * Ação do botão "Voltar à Lista".
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
}