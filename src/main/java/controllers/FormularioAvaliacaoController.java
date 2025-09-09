package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import models.Paciente;
import services.ProntuarioService;
import java.time.LocalDate;
import javafx.scene.layout.StackPane;

import com.jfoenix.controls.JFXDatePicker;
import models.Avaliacao;
import javafx.scene.web.HTMLEditor;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.AlertFactory;
import ui.NavigationManager;




public class FormularioAvaliacaoController {
    @FXML private Label mensagemLabel;
    @FXML private StackPane examesFisicosPlaceholder;
    @FXML private StackPane diagnosticoPlaceholder;
    @FXML private StackPane planoTratamentoPlaceholder;
    @FXML private StackPane queixaPrincipalPlaceholder;
    @FXML private StackPane hdaPlaceholder;
    @FXML private Button salvarButton;
    @FXML private JFXDatePicker dataAvaliacaoPicker;
    @FXML private Button backButton;
    @FXML private Label patientNameLabel;
    private HTMLEditor queixaPrincipalEditor;
    private HTMLEditor hdaEditor;
    private HTMLEditor examesFisicosEditor;
    private HTMLEditor diagnosticoEditor;  
    private HTMLEditor planoTratamentoEditor;
    private ProntuarioService prontuarioService;
    private Paciente pacienteAtual;
    private OnHistoryChangedListener historyListener;
    private Avaliacao avaliacaoParaEditar;



    public FormularioAvaliacaoController() {
        this.prontuarioService = new ProntuarioService();
    }

  public void configureParaCriacao(Paciente paciente, OnHistoryChangedListener listener) {
      inicializarEditores(); 
      this.pacienteAtual = paciente;
      patientNameLabel.setText(paciente.getNomeCompleto());
      this.historyListener = listener;
      this.avaliacaoParaEditar = null; // Garante que estamos no modo de criação

      // Configura a UI
      salvarButton.setText("Salvar Avaliação");
      limparCampos(); // Limpa e reseta o formulário
  }

    public void configureParaEdicao(Avaliacao avaliacao, Paciente paciente, OnHistoryChangedListener listener) {
        this.pacienteAtual = paciente;
        this.historyListener = listener;
        patientNameLabel.setText(paciente.getNomeCompleto());
        this.avaliacaoParaEditar = avaliacao; // Define o objeto a ser editado
        inicializarEditores(); 

        // Configura a UI
        salvarButton.setText("Salvar Alterações");

        // Preenche o formulário com os dados da avaliação
        dataAvaliacaoPicker.setValue(avaliacao.getDataAvaliacao());
        queixaPrincipalEditor.setHtmlText(avaliacao.getQueixaPrincipal());
        hdaEditor.setHtmlText(avaliacao.getHistoricoDoencaAtual());
        examesFisicosEditor.setHtmlText(avaliacao.getExamesFisicos());
        diagnosticoEditor.setHtmlText(avaliacao.getDiagnosticoFisioterapeutico());
        planoTratamentoEditor.setHtmlText(avaliacao.getPlanoTratamento());
    }

    private void setMensagem(String mensagem, boolean isError) {
        mensagemLabel.setText(mensagem);
        mensagemLabel.setVisible(true);
        mensagemLabel.setManaged(true);
        if (isError) {
            mensagemLabel.setStyle("-fx-text-fill: red;");
        } else {
            mensagemLabel.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    private void handleSalvarAvaliacao() {
        if (pacienteAtual == null) {
            setMensagem("Erro: Paciente não carregado.", true);
            return;
        }

        LocalDate data = dataAvaliacaoPicker.getValue();
        String queixa = queixaPrincipalEditor.getHtmlText();
        String hda = hdaEditor.getHtmlText();
        String exames = examesFisicosEditor.getHtmlText();
        String diagnostico = diagnosticoEditor.getHtmlText();
        String plano = planoTratamentoEditor.getHtmlText();
        String resultado;

        // Lógica de decisão: Criar ou Atualizar?
        if (avaliacaoParaEditar == null) {
            // MODO CRIAÇÃO
            resultado = prontuarioService.cadastrarAvaliacao(pacienteAtual.getId(), data, queixa, hda, exames, diagnostico, plano);
        } else {
            // MODO EDIÇÃO
            resultado = prontuarioService.atualizarAvaliacao(avaliacaoParaEditar.getId(), pacienteAtual.getId(), data, queixa, hda, exames, diagnostico, plano);
        }

        if (resultado.isEmpty()) {
            setMensagem("Avaliação salva com sucesso!", false);
            if (historyListener != null) {
                historyListener.onHistoryChanged();
            }
            // Após salvar, reseta o formulário para o modo de criação
            configureParaCriacao(pacienteAtual, historyListener); 
        } else {
            setMensagem(resultado, true);
        }
    }

    private void limparCampos() {
        dataAvaliacaoPicker.setValue(LocalDate.now());
        queixaPrincipalEditor.setHtmlText("");
        hdaEditor.setHtmlText("");
        examesFisicosEditor.setHtmlText("");
        diagnosticoEditor.setHtmlText("");
        planoTratamentoEditor.setHtmlText("");
    }
  
  private void inicializarEditores() {

      // Usa o "carregamento lento": só cria os editores se eles ainda não existirem.
      if (queixaPrincipalEditor == null) {
          queixaPrincipalEditor = new HTMLEditor();
          queixaPrincipalPlaceholder.getChildren().add(queixaPrincipalEditor);

          hdaEditor = new HTMLEditor();
          hdaPlaceholder.getChildren().add(hdaEditor);

          examesFisicosEditor = new HTMLEditor();
          examesFisicosPlaceholder.getChildren().add(examesFisicosEditor);

          diagnosticoEditor = new HTMLEditor();
          diagnosticoPlaceholder.getChildren().add(diagnosticoEditor);

          planoTratamentoEditor = new HTMLEditor();
          planoTratamentoPlaceholder.getChildren().add(planoTratamentoEditor);
      }
  }

  @FXML
private void handleBackButton() {
    try {
        // Pega o caminho da tela anterior no histórico de navegação
        String fxmlPath = NavigationManager.getInstance().getPreviousPage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent prontuarioView = loader.load();

        // Pega o controller da tela de prontuário para recarregar os dados
        ProntuarioViewController controller = loader.getController();
        controller.initData(this.pacienteAtual); // Passa o paciente de volta

        // Pega o palco (janela) atual e muda a cena para a tela anterior
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(prontuarioView, 1280, 720));
        stage.setTitle("SoftFisio - Prontuário de " + this.pacienteAtual.getNomeCompleto());

    } catch (IOException e) {
        e.printStackTrace();
        AlertFactory.showError("Erro de Navegação", "Não foi possível voltar para a tela de prontuário.");
    }
}
}