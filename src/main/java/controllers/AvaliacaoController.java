package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.Paciente;
import services.ProntuarioService;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import com.jfoenix.controls.JFXDatePicker;
import javafx.stage.Modality;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Avaliacao;
import javafx.scene.web.HTMLEditor;


public class AvaliacaoController {
    @FXML private Label mensagemLabel;
    @FXML private HTMLEditor queixaPrincipalEditor;
    @FXML private HTMLEditor hdaEditor;
    @FXML private HTMLEditor examesFisicosEditor;
    @FXML private HTMLEditor diagnosticoEditor;
    @FXML private HTMLEditor planoTratamentoEditor;
    @FXML private Button salvarButton;
    @FXML private JFXDatePicker dataAvaliacaoPicker;
    private ProntuarioService prontuarioService;
    private Paciente pacienteAtual;
    private OnHistoryChangedListener historyListener;
    private Avaliacao avaliacaoParaEditar;

    public AvaliacaoController() {
        this.prontuarioService = new ProntuarioService();
    }

    // Este método substitui o initData
public void configureParaCriacao(Paciente paciente, OnHistoryChangedListener listener) {
    this.pacienteAtual = paciente;
    this.historyListener = listener;
    this.avaliacaoParaEditar = null; // Garante que estamos no modo de criação

    // Configura a UI
    salvarButton.setText("Salvar Avaliação");
    limparCampos(); // Limpa e reseta o formulário
}

public void configureParaEdicao(Avaliacao avaliacao, Paciente paciente, OnHistoryChangedListener listener) {
    this.pacienteAtual = paciente;
    this.historyListener = listener;
    this.avaliacaoParaEditar = avaliacao; // Define o objeto a ser editado

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

    
}