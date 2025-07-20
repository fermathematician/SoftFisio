// db/DatabaseManager.java
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:fisioterapia.db";
    private static Connection connectionInstance;

    // Construtor privado para evitar instanciação direta
    private DatabaseManager() {}

    // Método estático para obter a conexão (Singleton)
    public static Connection getConnection() {
        if (connectionInstance == null) {
            try {
                connectionInstance = DriverManager.getConnection(DB_URL);
            } catch (SQLException e) {
                System.err.println("Falha ao criar conexão com o banco de dados: " + e.getMessage());
                // Em uma aplicação real, seria melhor lançar uma exceção aqui
                return null;
            }
        }
        return connectionInstance;
    }

    public static void initializeDatabase() {
        // ... (o código de criação das tabelas continua o mesmo)
        // Adicionaremos a tabela de usuários
        try (Statement stmt = getConnection().createStatement()) {
            // Tabela de Usuários para Login
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT UNIQUE NOT NULL," +
                    "senha TEXT NOT NULL," + // Em um projeto real, armazene um HASH da senha!
                    "nome_completo TEXT NOT NULL" +
                    ");");

            // Insere um usuário padrão se a tabela estiver vazia
            if (!stmt.executeQuery("SELECT id FROM usuarios LIMIT 1;").next()) {
                 // NUNCA guarde senhas em texto plano. Isto é apenas para exemplo.
                 // Use bibliotecas como jBCrypt para gerar um hash seguro.
                stmt.execute("INSERT INTO usuarios (login, senha, nome_completo) VALUES ('admin', 'admin123', 'Administrador do Sistema');");
            }
            
            // ... (criação das outras tabelas como 'pacientes')

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas: " + e.getMessage());
        }
    }
}