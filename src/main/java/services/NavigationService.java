package services;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Gerencia o estado da pilha de navegação da aplicação.
 * Esta classe atua puramente como um gerenciador de histórico de strings (caminhos FXML).
 * Ela não é responsável por carregar ou exibir cenas.
 */
public class NavigationService {

    // Singleton Pattern
    private static final NavigationService instance = new NavigationService();

    private final Deque<String> history = new ArrayDeque<>();

    private NavigationService() {}

    public static NavigationService getInstance() {
        return instance;
    }

    /**
     * Limpa o histórico e define a primeira página.
     * Deve ser chamado ao iniciar a aplicação.
     * @param initialFxmlPath O caminho para o FXML da página inicial.
     */
    public void startHistoryWith(String initialFxmlPath) {
        history.clear();
        history.push(initialFxmlPath);
    }

    /**
     * Adiciona um novo FXML ao topo da pilha de histórico.
     * Isso deve ser chamado pelo controller *antes* de ele carregar a nova cena.
     * @param fxmlPath O caminho para o FXML da nova página.
     */
    public void pushHistory(String fxmlPath) {
        history.push(fxmlPath);
    }

    /**
     * Remove a página atual do histórico e retorna a página anterior.
     * Retorna null se não houver página anterior.
     * @return O caminho FXML da página anterior ou null.
     */
    public String getPreviousPage() {
        if (history.size() > 1) {
            history.pop(); // Remove a página atual
            return history.peek(); // Retorna a nova página do topo (a anterior)
        }
        return null; // Não há histórico para onde voltar
    }

    /**
     * Apenas visualiza qual é a página atual no topo da pilha, sem modificar o histórico.
     * @return O caminho FXML da página atual.
     */
    public String getCurrentPage() {
        return history.peek();
    }
    
    /**
     * Limpa todo o histórico de navegação. Útil para um logout ou reset.
     */
    public void clearHistory() {
        history.clear();
    }
}