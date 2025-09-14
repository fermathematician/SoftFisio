package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import com.jfoenix.controls.JFXDatePicker;

import models.Avaliacao;
import models.Paciente;
import services.ProntuarioService;
import ui.AlertFactory;
import ui.NavigationManager;

public class FormularioAvaliacaoController {

    @FXML private Button backButton;
    @FXML private Button salvarButton;
    @FXML private JFXDatePicker dataAvaliacaoPicker;
    @FXML private Label formTitleLabel;
    @FXML private Label mensagemLabel;
    @FXML private Label patientNameLabel;
    @FXML private Label subtitleLabel;
    @FXML private VBox scrollContentVBox;
    
    @FXML private StackPane doencaAtualPlaceholder;
    @FXML private StackPane historiaPregressaPlaceholder;
    @FXML private StackPane inspecaoPalpacaoPlaceholder;
    @FXML private StackPane admPlaceholder;
    @FXML private StackPane forcaMuscularPlaceholder;
    @FXML private StackPane avaliacaoFuncionalPlaceholder;
    @FXML private StackPane testesEspeciaisPlaceholder;
    @FXML private StackPane escalasFuncionaisPlaceholder;
    @FXML private StackPane diagnosticoCinesiologicoPlaceholder;
    @FXML private StackPane planoTratamentoPlaceholder;

    private HTMLEditor doencaAtualEditor;
    private HTMLEditor historiaPregressaEditor;
    private HTMLEditor inspecaoPalpacaoEditor;
    private HTMLEditor admEditor;
    private HTMLEditor forcaMuscularEditor;
    private HTMLEditor avaliacaoFuncionalEditor;
    private HTMLEditor testesEspeciaisEditor;
    private HTMLEditor escalasFuncionaisEditor;
    private HTMLEditor diagnosticoCinesiologicoEditor;
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
        this.historyListener = listener;
        this.avaliacaoParaEditar = null;

        patientNameLabel.setText(paciente.getNomeCompleto());
        subtitleLabel.setText("Cadastrando Nova Avaliação");
        formTitleLabel.setText("Cadastrar Avaliação");
        salvarButton.setText("Salvar Avaliação");
        limparCampos();
    }

    public void configureParaEdicao(Avaliacao avaliacao, Paciente paciente, OnHistoryChangedListener listener) {
        inicializarEditores();
        this.pacienteAtual = paciente;
        this.historyListener = listener;
        this.avaliacaoParaEditar = avaliacao;

        patientNameLabel.setText(paciente.getNomeCompleto());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Editando Avaliação de' dd 'de' MMMM 'de' yyyy");
        subtitleLabel.setText(avaliacao.getDataAvaliacao().format(formatter));
        formTitleLabel.setText("Editar Avaliação");
        salvarButton.setText("Salvar Alterações");

        dataAvaliacaoPicker.setValue(avaliacao.getDataAvaliacao());
        doencaAtualEditor.setHtmlText(avaliacao.getDoencaAtual());
        historiaPregressaEditor.setHtmlText(avaliacao.getHistoriaPregressa());
        inspecaoPalpacaoEditor.setHtmlText(avaliacao.getInspecaoPalpacao());
        admEditor.setHtmlText(avaliacao.getAdm());
        forcaMuscularEditor.setHtmlText(avaliacao.getForcaMuscular());
        avaliacaoFuncionalEditor.setHtmlText(avaliacao.getAvaliacaoFuncional());
        testesEspeciaisEditor.setHtmlText(avaliacao.getTestesEspeciais());
        escalasFuncionaisEditor.setHtmlText(avaliacao.getEscalasFuncionais());
        diagnosticoCinesiologicoEditor.setHtmlText(avaliacao.getDiagnosticoCinesiologico());
        planoTratamentoEditor.setHtmlText(avaliacao.getPlanoTratamento());
    }

    @FXML
    private void handleSalvarAvaliacao() {
        LocalDate data = dataAvaliacaoPicker.getValue();
        String doencaAtual = doencaAtualEditor.getHtmlText();
        String historiaPregressa = historiaPregressaEditor.getHtmlText();
        String inspecaoPalpacao = inspecaoPalpacaoEditor.getHtmlText();
        String adm = admEditor.getHtmlText();
        String forcaMuscular = forcaMuscularEditor.getHtmlText();
        String avaliacaoFuncional = avaliacaoFuncionalEditor.getHtmlText();
        String testesEspeciais = testesEspeciaisEditor.getHtmlText();
        String escalasFuncionais = escalasFuncionaisEditor.getHtmlText();
        String diagnosticoCinesiologico = diagnosticoCinesiologicoEditor.getHtmlText();
        String planoTratamento = planoTratamentoEditor.getHtmlText();
        
        String resultado;
        if (avaliacaoParaEditar == null) {
            resultado = prontuarioService.cadastrarAvaliacao(pacienteAtual.getId(), data, doencaAtual, historiaPregressa, inspecaoPalpacao, adm, forcaMuscular, avaliacaoFuncional, testesEspeciais, escalasFuncionais, diagnosticoCinesiologico, planoTratamento);
        } else {
            resultado = prontuarioService.atualizarAvaliacao(avaliacaoParaEditar.getId(), pacienteAtual.getId(), data, doencaAtual, historiaPregressa, inspecaoPalpacao, adm, forcaMuscular, avaliacaoFuncional, testesEspeciais, escalasFuncionais, diagnosticoCinesiologico, planoTratamento);
        }

        if (resultado.isEmpty()) {
            if (historyListener != null) historyListener.onHistoryChanged();
            handleBackButton();
        } else {
            setMensagem(resultado, true);
        }
    }

    
    private void inicializarEditores() {
        if (doencaAtualEditor == null) {
            doencaAtualEditor = new HTMLEditor();
            doencaAtualEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            doencaAtualPlaceholder.getChildren().add(doencaAtualEditor);

            historiaPregressaEditor = new HTMLEditor();
            historiaPregressaEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            historiaPregressaPlaceholder.getChildren().add(historiaPregressaEditor);
            
            inspecaoPalpacaoEditor = new HTMLEditor();
            inspecaoPalpacaoEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            inspecaoPalpacaoPlaceholder.getChildren().add(inspecaoPalpacaoEditor);
            
            admEditor = new HTMLEditor();
            admEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            admPlaceholder.getChildren().add(admEditor);
            
            forcaMuscularEditor = new HTMLEditor();
            forcaMuscularEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            forcaMuscularPlaceholder.getChildren().add(forcaMuscularEditor);
            
            avaliacaoFuncionalEditor = new HTMLEditor();
            avaliacaoFuncionalEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            avaliacaoFuncionalPlaceholder.getChildren().add(avaliacaoFuncionalEditor);
            
            testesEspeciaisEditor = new HTMLEditor();
            testesEspeciaisEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            testesEspeciaisPlaceholder.getChildren().add(testesEspeciaisEditor);
            
            escalasFuncionaisEditor = new HTMLEditor();
            escalasFuncionaisEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            escalasFuncionaisPlaceholder.getChildren().add(escalasFuncionaisEditor);
            
            diagnosticoCinesiologicoEditor = new HTMLEditor();
            diagnosticoCinesiologicoEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            diagnosticoCinesiologicoPlaceholder.getChildren().add(diagnosticoCinesiologicoEditor);
            
            planoTratamentoEditor = new HTMLEditor();
            planoTratamentoEditor.addEventFilter(javafx.scene.input.ScrollEvent.ANY, e -> { scrollContentVBox.fireEvent(e.copyFor(e.getSource(), scrollContentVBox)); e.consume(); });
            planoTratamentoPlaceholder.getChildren().add(planoTratamentoEditor);
        }
    }

    private void limparCampos() {
        dataAvaliacaoPicker.setValue(LocalDate.now());
        doencaAtualEditor.setHtmlText("<b>Início dos sintomas:</b><br><b>Mecanismo da lesão:</b><br><b>Tempo de evolução:</b><br><b>Fatores de melhora:</b><br><b>Fatores de piora:</b><br><b>Padrão da dor:</b><br><b>Irradiação:</b><br><b>Intensidade (EVA 0-10):</b><br><b>Limitações funcionais relatadas:</b>");
        historiaPregressaEditor.setHtmlText("<b>Doenças associadas:</b><br><b>Cirurgias prévias:</b><br><b>Lesões ortopédicas anteriores:</b><br><b>Uso de medicamentos:</b><br><b>Prática de atividade física:</b><br><b>Hábitos (Tabagismo, Etilismo):</b><br><b>Histórico familiar relevante:</b>");
        inspecaoPalpacaoEditor.setHtmlText("<b>Postura:</b><br><b>Marcha:</b><br><b>Tônus muscular:</b><br><b>Edema:</b><br><b>Alterações de pele:</b><br><b>Sensibilidade tátil e térmica:</b>");
        admEditor.setHtmlText("<b>Goniometria (Comparar lado afetado e contralateral):</b><br><b>Coluna (cervical, torácica, lombar):</b><br><b>Ombro (flexão, extensão, abdução, rotação int/ext):</b><br><b>Cotovelo e punho:</b><br><b>Quadril, joelho, tornozelo e pé:</b>");
        forcaMuscularEditor.setHtmlText("<b>Principais grupos musculares de MMSS e MMII (Escala de Oxford 0-5):</b><br><b>Core (reto abdominal, oblíquos, transverso, paravertebrais):</b>");
        avaliacaoFuncionalEditor.setHtmlText("<b>Mobilidade e Controle Motor:</b><br><b>Força e Potência:</b><br><b>Velocidade e Agilidade:</b><br><b>Habilidade Específica do Esporte:</b><br><b>Resistência e Capacidade Aeróbia:</b>");
        testesEspeciaisEditor.setHtmlText("<b>Tornozelo/Pé:</b><br><b>Joelho:</b><br><b>Quadril:</b><br><b>Coluna lombar:</b><br><b>Coluna cervical:</b><br><b>Ombro:</b><br><b>Cotovelo:</b><br><b>Punho/Mão:</b>");
        escalasFuncionaisEditor.setHtmlText("<b>EVA:</b><br><b>Oswestry:</b><br><b>DASH:</b><br><b>LEFS:</b><br><b>KOOS:</b><br><b>HOOS:</b>");
        diagnosticoCinesiologicoEditor.setHtmlText("");
        planoTratamentoEditor.setHtmlText("<b>Frequência das sessões:</b><br><b>Recursos e técnicas planejadas:</b>");
    }

    @FXML
    private void handleBackButton() {
        try {
            String fxmlPath = NavigationManager.getInstance().getPreviousPage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent prontuarioView = loader.load();
            ProntuarioViewController controller = loader.getController();
            controller.initData(this.pacienteAtual);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(prontuarioView));
            stage.setMaximized(true);
            stage.setTitle("SoftFisio - Prontuário de " + this.pacienteAtual.getNomeCompleto());
        } catch (IOException e) {
            e.printStackTrace();
            AlertFactory.showError("Erro de Navegação", "Não foi possível voltar para a tela de prontuário.");
        }
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
}