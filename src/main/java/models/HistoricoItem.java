// Em models/HistoricoItem.java
package models;

import java.time.LocalDate;

public interface HistoricoItem {
    LocalDate getData(); // Alterado de getDataHora() para getData() e de LocalDateTime para LocalDate
    String getTipo();
}