package services;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class MaskService {

    /**
     * Aplica uma máscara de CPF (xxx.xxx.xxx-xx) a um TextField.
     * @param textField O campo de texto ao qual a máscara será aplicada.
     */
    public static void applyCpfMask(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            // Se não houver mudança, não faz nada
            if (change.isAdded()) {
                String newText = change.getControlNewText();
                String digitsOnly = newText.replaceAll("[^0-9]", "");

                if (digitsOnly.length() > 11) {
                    return null; // Rejeita se exceder 11 dígitos
                }

                String formattedText = formatCpf(digitsOnly);

                // MODIFICAÇÃO: Lógica final e correta
                // Substitui o texto inteiro pelo texto formatado
                change.setText(formattedText);
                // Substitui todo o conteúdo antigo
                change.setRange(0, change.getControlText().length());
                // Define o cursor e a âncora no final, para evitar seleção
                change.setAnchor(formattedText.length());
                change.setCaretPosition(formattedText.length());
            }
            return change;
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    /**
     * Aplica uma máscara de Telefone ((xx) xxxxx-xxxx) a um TextField.
     * @param textField O campo de texto ao qual a máscara será aplicada.
     */
    public static void applyPhoneMask(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.isAdded()) {
                String newText = change.getControlNewText();
                String digitsOnly = newText.replaceAll("[^0-9]", "");

                if (digitsOnly.length() > 11) {
                    return null;
                }

                String formattedText = formatPhone(digitsOnly);

                // MODIFICAÇÃO: Lógica final e correta
                change.setText(formattedText);
                change.setRange(0, change.getControlText().length());
                change.setAnchor(formattedText.length());
                change.setCaretPosition(formattedText.length());
            }
            return change;
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    private static String formatCpf(String digitsOnly) {
        int len = digitsOnly.length();
        if (len == 0) return "";
        if (len <= 3) return digitsOnly;
        if (len <= 6) return digitsOnly.substring(0, 3) + "." + digitsOnly.substring(3);
        if (len <= 9) return digitsOnly.substring(0, 3) + "." + digitsOnly.substring(3, 6) + "." + digitsOnly.substring(6);
        return digitsOnly.substring(0, 3) + "." + digitsOnly.substring(3, 6) + "." + digitsOnly.substring(6, 9) + "-" + digitsOnly.substring(9);
    }
    
    private static String formatPhone(String digitsOnly) {
        int len = digitsOnly.length();
        if (len == 0) return "";
        if (len <= 2) return "(" + digitsOnly;
        if (len <= 7) return "(" + digitsOnly.substring(0, 2) + ") " + digitsOnly.substring(2);
        return "(" + digitsOnly.substring(0, 2) + ") " + digitsOnly.substring(2, 7) + "-" + digitsOnly.substring(7);
    }
}