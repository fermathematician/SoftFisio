package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

import models.Paciente;
import services.AlertFactory; // Importa a nova classe
import services.AuthServicePaciente;
import services.NavigationService;
import javafx.stage.Modality;

public class PatientCardController {

    public interface OnPatientDeletedListener {
        void onPatientDeleted(Paciente paciente);
    }

    @FXML private Label patientNameLabel;
    @FXML private Label patientCpfLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label dobLabel;
    @FXML private Region deleteIcon;
    @FXML private Button editButton;
    @FXML private Button viewRecordButton; 

    private Paciente paciente;
    private AuthServicePaciente authServicePaciente; 
    private OnPatientDeletedListener deletionListener;    

    public PatientCardController() {
        this.authServicePaciente = new AuthServicePaciente();
    }
    
    public void setOnPatientDeletedListener(OnPatientDeletedListener listener) {
        this.deletionListener = listener;
    }
    
    public void setData(Paciente paciente) {
        if (paciente != null) {
            this.paciente = paciente;
            patientNameLabel.setText(paciente.getNomeCompleto());
            patientCpfLabel.setText(paciente.getCpf());
            phoneLabel.setText(paciente.getTelefone());
            emailLabel.setText(paciente.getEmail());

            if (paciente.getDataNascimento() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
                dobLabel.setText(paciente.getDataNascimento().format(formatter));
            } else {
                dobLabel.setText("Não informada");
            }
        }
    }


    @FXML
    private void handleViewRecord() {
        try {
            String fxmlPath = "/static/prontuario_view.fxml";
            NavigationService.getInstance().pushHistory(fxmlPath);

            URL fxmlUrl = getClass().getResource(fxmlPath);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent prontuarioView = loader.load();

            ProntuarioViewController controller = loader.getController();
        
            controller.initData(this.paciente);

            Stage stage = (Stage) viewRecordButton.getScene().getWindow();
            stage.setScene(new Scene(prontuarioView, 1280, 720));
            stage.setTitle("SoftFisio - Prontuário de " + this.paciente.getNomeCompleto());
        } catch (IOException e) {
            System.err.println("### ERRO DE IO AO CARREGAR A TELA DE PRONTUÁRIO ###");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("### UM ERRO INESPERADO OCORREU ###");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        // Usa a nova AlertFactory para mostrar um diálogo de confirmação estilizado.
        AlertFactory.showConfirmation(
            "Confirmar Exclusão",
            "Deletar " + this.paciente.getNomeCompleto() + "?",
            "Esta ação é permanente e não pode ser desfeita.",
            () -> { // Esta é a ação que será executada se o usuário clicar "OK"
                String resultado = authServicePaciente.deletar(this.paciente.getId());

                if (resultado.isEmpty()) { // Sucesso
                    if (deletionListener != null) {
                        deletionListener.onPatientDeleted(this.paciente);
                    }
                } else { 
                    // Usa a AlertFactory para mostrar um diálogo de erro estilizado.
                    AlertFactory.showError(
                        "Erro ao Deletar",
                        "Não foi possível deletar o paciente: " + resultado
                    );
                }
            }
        );
    }

    @FXML
private void handleEdit() {
    try {
        String fxmlPath = "/static/formulario_paciente.fxml";
        // Se você já renomeou os arquivos, use a linha abaixo:
        // String fxmlPath = "/static/formulario_paciente.fxml";
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        FormularioPacienteController controller = loader.getController();
        // Se você já renomeou, o nome da classe será FormularioPacienteController
        // FormularioPacienteController controller = loader.getController();
        
        controller.initData(this.paciente);

        // --- LÓGICA DE NOVA JANELA ---
        Stage stage = new Stage();
        stage.setTitle("SoftFisio - Editar Paciente");
        stage.setScene(new Scene(root, 1280, 720));
        
        // Configura a nova janela para ser um "modal" (bloqueia a janela de trás)
        stage.initOwner(editButton.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        
        // Remove a necessidade do NavigationService aqui, pois a janela apenas fecha
        stage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
        AlertFactory.showError("Erro de Navegação", "Não foi possível abrir a tela de edição.");
    }
}
}