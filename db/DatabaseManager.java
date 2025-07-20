package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:fisioterapia.db";
    
    // REMOVEMOS a variável estática da conexão.
    // private static Connection connectionInstance;

    // O construtor privado continua, pois não queremos que esta classe seja instanciada.
    private DatabaseManager() {}

    /**
     * Este método agora retorna uma NOVA conexão com o banco de dados toda vez que é chamado.
     * Isso é ideal para o SQLite e resolve o problema de conexão fechada.
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Falha ao criar conexão com o banco de dados: " + e.getMessage());
            // Lançar uma exceção em tempo de execução pode ser uma boa ideia aqui
            // para parar a aplicação se não for possível conectar.
            throw new RuntimeException(e);
        }
    }

    /**
     * O método de inicialização continua funcionando perfeitamente,
     * pois ele também usa o try-with-resources, abrindo e fechando sua própria conexão.
     */
    public static void initializeDatabase() {
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT UNIQUE NOT NULL," +
                "senha TEXT NOT NULL," +
                "nome_completo TEXT NOT NULL" +
                ");";
        
        // Adicione aqui a criação de outras tabelas se necessário...

        try (Connection conn = getConnection(); // Pega uma nova conexão
            Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlUsuarios);

            if (!stmt.executeQuery("SELECT id FROM usuarios LIMIT 1;").next()) {
                stmt.execute("INSERT INTO usuarios (login, senha, nome_completo) VALUES ('admin', 'admin123', 'Administrador do Sistema');");
                System.out.println("Usuário 'admin' padrão criado.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas: " + e.getMessage());
        }
    }
}