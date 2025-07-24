package models;

import java.time.LocalDateTime;

public interface HistoricoItem {
    LocalDateTime getDataHora();
    String getTipo(); // Método para sabermos se é "Sessão" ou "Avaliação"
}