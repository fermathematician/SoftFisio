package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; 
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType; 
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional; 

import models.Paciente;
import services.AuthServicePaciente;
import services.NavigationService; 

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
        e.printStackTrace(); // Isto vai imprimir o erro detalhado no console
    } catch (Exception e) {
        System.err.println("### UM ERRO INESPERADO OCORREU ###");
        e.printStackTrace();
    }
}

    @FXML
    private void handleDelete() {
        // Mostra um diálogo de confirmação antes de deletar
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmar Exclusão");
        confirmationAlert.setHeaderText("Deletar " + this.paciente.getNomeCompleto() + "?");
        confirmationAlert.setContentText("Esta ação é permanente e não pode ser desfeita.");
        
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String resultado = authServicePaciente.deletar(this.paciente.getId());

            // Verifica o resultado do serviço
            if (resultado.isEmpty()) { // Sucesso
                if (deletionListener != null) {
                    deletionListener.onPatientDeleted(this.paciente);
                }
            } else { 
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erro");
                errorAlert.setHeaderText("Não foi possível deletar o paciente.");
                errorAlert.setContentText(resultado);
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void handleEdit() {
        try {
            String fxmlPath = "/static/editar_paciente.fxml";

            NavigationService.getInstance().pushHistory(fxmlPath);
            
            URL fxmlUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent editarPacienteView = loader.load();

            // Pega a instância do NOVO controlador (EditarPacienteController)
            EditarPacienteController controller = loader.getController();
            
            // Passa o paciente deste card para o controlador da tela de edição
            controller.initData(this.paciente);

            // Exibe a nova cena na mesma janela
            Stage stage = (Stage) editButton.getScene().getWindow();
            stage.setScene(new Scene(editarPacienteView, 1280, 720));
            stage.setTitle("SoftFisio - Editar Paciente");

        } catch (IOException e) {
            e.printStackTrace();
            // Em uma aplicação real, seria bom mostrar um alerta de erro para o usuário.
        }
    }
}