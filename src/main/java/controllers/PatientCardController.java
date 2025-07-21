// Local: src/controllers/PatientCardController.java
package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.Paciente;
import java.time.format.DateTimeFormatter;

public class PatientCardController {

    @FXML private Label patientNameLabel;
    @FXML private Label patientCpfLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label dobLabel;

    /**
     * Preenche os componentes do card com os dados de um paciente específico.
     * Este método será chamado pelo MainViewController após carregar o card.
     * @param paciente O objeto Paciente contendo os dados a serem exibidos.
     */
    public void setData(Paciente paciente) {
        if (paciente != null) {
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

    // Aqui você pode adicionar métodos para os botões "Editar" e "Ver Ficha" do card
    // Ex: @FXML private void handleEdit() { ... }
}