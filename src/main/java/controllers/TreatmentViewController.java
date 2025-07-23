package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

    // TO DO - Arrumar o modo que o card tá sendo criado aqui, criar aquivo separado e carregar 
    private VBox createSessionCard(Sessao sessao) {
        // Container principal do card
        VBox card = new VBox(5); // Espaçamento vertical de 5px
        card.getStyleClass().add("patient-card");

        // --- BARRA SUPERIOR: Título e Ícone de Deletar ---
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Sessão de' dd 'de' MMMM 'de' yyyy 'às' HH:mm");
        Label dateLabel = new Label(sessao.getDataSessao().format(formatter));
        dateLabel.getStyleClass().add("session-date-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Espaçador para empurrar o ícone para a direita

        Region deleteIcon = new Region();
        deleteIcon.getStyleClass().add("delete-icon");
        deleteIcon.setOnMouseClicked(event -> {
            handleDelete(sessao);
        });

        topBar.getChildren().addAll(dateLabel, spacer, deleteIcon);

        // --- CONTEÚDO ---
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 5, 0));
        
        Label evolutionLabel = new Label(sessao.getEvolucaoTexto());
        evolutionLabel.setWrapText(true);

        // --- BARRA INFERIOR: Botão de Editar ---
        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_RIGHT); // Alinha o botão à direita
        bottomBar.setPadding(new Insets(15, 0, 0, 0)); // Espaço acima do botão

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("primary-button"); // Botão azul com texto branco
        editButton.setOnAction(event -> {
            System.out.println("Editar sessão ID: " + sessao.getId());
        });
        bottomBar.getChildren().add(editButton);
        
        // --- MONTAGEM FINAL DO CARD ---
        // Adiciona os elementos de conteúdo primeiro
        card.getChildren().addAll(topBar, separator, evolutionLabel);

        // Adiciona as observações se existirem
        if (sessao.getObservacoesSessao() != null && !sessao.getObservacoesSessao().isEmpty()) {
            Label obsLabelTitle = new Label("Observações:");
            obsLabelTitle.getStyleClass().add("session-observation-title");
            Label obsLabel = new Label(sessao.getObservacoesSessao());
            obsLabel.setWrapText(true);
            card.getChildren().addAll(obsLabelTitle, obsLabel);
        }

        // NOVO: Espaçador vertical para empurrar o botão para baixo
        Region vSpacer = new Region();
        VBox.setVgrow(vSpacer, Priority.ALWAYS);
        card.getChildren().add(vSpacer); // Adiciona o espaçador

        // Adiciona a barra inferior com o botão "Editar" no final de tudo
        card.getChildren().add(bottomBar);

        return card;
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
     * Ação do botão deletar
     */

     @FXML
     private void handleDelete(Sessao sessao) {
        // Mostra um diálogo de confirmação antes de deletar
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmar Exclusão");
        confirmationAlert.setHeaderText("Deseja deletar essa seção?");
        confirmationAlert.setContentText("Esta ação é permanente e não pode ser desfeita.");
        
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String resultado = prontuarioService.deletarSessao(sessao.getId());

            // Sucesso
            if (resultado.isEmpty()) { 
                loadSessoes();
            } else { 
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erro");
                errorAlert.setHeaderText("Não foi possível deletar o paciente.");
                errorAlert.setContentText(resultado);
                errorAlert.showAndWait();
            }
        }
     }

     /**
      * Ação do botão de editar
      */

      @FXML
      private void handleEdit() {
        System.out.println("Editando");
      }

}