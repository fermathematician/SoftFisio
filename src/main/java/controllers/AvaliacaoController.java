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


public class AvaliacaoController {
    @FXML private Label mensagemLabel;
    @FXML private TextArea queixaPrincipalArea;
    @FXML private TextArea hdaArea;
    @FXML private TextArea examesFisicosArea;
    @FXML private TextArea diagnosticoArea;
    @FXML private TextArea planoTratamentoArea;
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
    queixaPrincipalArea.setText(avaliacao.getQueixaPrincipal());
    hdaArea.setText(avaliacao.getHistoricoDoencaAtual());
    examesFisicosArea.setText(avaliacao.getExamesFisicos());
    diagnosticoArea.setText(avaliacao.getDiagnosticoFisioterapeutico());
    planoTratamentoArea.setText(avaliacao.getPlanoTratamento());
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
    String queixa = queixaPrincipalArea.getText();
    String hda = hdaArea.getText();
    String exames = examesFisicosArea.getText();
    String diagnostico = diagnosticoArea.getText();
    String plano = planoTratamentoArea.getText();
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
    queixaPrincipalArea.clear();
    hdaArea.clear();
    examesFisicosArea.clear();
    diagnosticoArea.clear();
    planoTratamentoArea.clear();
}

    
}