// Local: src/controllers/PatientCardController.java
package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.Paciente;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class PatientCardController {

    @FXML private Label patientNameLabel;
    @FXML private Label patientCpfLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label dobLabel;

    @FXML private Button editButton;
    private Paciente paciente;

    /**
     * Preenche os componentes do card com os dados de um paciente específico.
     * Este método será chamado pelo MainViewController após carregar o card.
     * @param paciente O objeto Paciente contendo os dados a serem exibidos.
     */
    public void setData(Paciente paciente) {
        if (paciente != null) {
            this.paciente = paciente;
            patientNameLabel.setText(paciente.getNomeCompleto());
            patientCpfLabel.setText(paciente.getCpf());
            phoneLabel.setText(paciente.getTelefone());
            emailLabel.setText(paciente.getEmail());

            // Formata a data para um formato mais amigável
            if (paciente.getDataNascimento() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
                dobLabel.setText(paciente.getDataNascimento().format(formatter));
            } else {
                dobLabel.setText("Não informada");
            }
        }
    }

    @FXML
    private void handleEdit() {
        System.out.println(">>> MÉTODO handleEdit FOI CHAMADO! <<<"); 
        try {
            
            // Carrega o NOVO arquivo FXML de edição
            URL fxmlUrl = getClass().getResource("/static/editar_paciente.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Pega a instância do NOVO controlador (EditarPacienteController)
            EditarPacienteController controller = loader.getController();
            
            // Passa o paciente deste card para o controlador da tela de edição
            controller.initData(this.paciente);

            // Exibe a nova cena na mesma janela
            Stage stage = (Stage) editButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("SoftFisio - Editar Paciente");

        } catch (IOException e) {
            e.printStackTrace();
            // Em uma aplicação real, seria bom mostrar um alerta de erro para o usuário.
        }
    }
}