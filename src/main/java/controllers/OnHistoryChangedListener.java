package controllers;

/**
 * Interface para comunicação entre controllers quando o histórico de prontuário é alterado.
 */
public interface OnHistoryChangedListener {
    void onHistoryChanged();
}