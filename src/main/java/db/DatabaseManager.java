package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:fisioterapia.db";

    private DatabaseManager() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Falha ao criar conexão com o banco de dados: " + e.getMessage());
            throw new RuntimeException("Não foi possível conectar ao banco de dados.", e);
        }
    }

    /**
     * Inicializa o banco de dados, criando as tabelas se elas ainda não existirem.
     */
    public static void initializeDatabase() {
        // SQL para criar a tabela de usuários
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT UNIQUE NOT NULL," +
                "senha TEXT NOT NULL," +
                "nome_completo TEXT NOT NULL" +
                ");";

        // SQL para criar a tabela de pacientes
        String sqlPacientes = "CREATE TABLE IF NOT EXISTS pacientes (" +
            "id_paciente INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_usuario INTEGER NOT NULL," +
            "nome TEXT NOT NULL," +
            "cpf TEXT UNIQUE," +
            "genero TEXT," +
            "telefone TEXT," +
            "email TEXT," +
            "data_nascimento TEXT," +
            "data_cadastro TEXT DEFAULT CURRENT_TIMESTAMP," +
            "paciente_corrida BOOLEAN DEFAULT FALSE," + 
            "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)" +
            ");";

        // SQL para a nova tabela 'sessoes'
        String sqlSessoes = "CREATE TABLE IF NOT EXISTS sessoes (" +
                "id_sessao INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_paciente INTEGER NOT NULL," +
                "data_sessao TEXT NOT NULL," +
                "evolucao_texto TEXT NOT NULL," +
                "observacoes_sessao TEXT," +
                "FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente)" +
                ");";

        String sqlAvaliacoes = "CREATE TABLE IF NOT EXISTS avaliacoes (" +
        "id_avaliacao INTEGER PRIMARY KEY AUTOINCREMENT," +
        "id_paciente INTEGER NOT NULL," +
        "data_avaliacao TEXT NOT NULL," +
        "queixa_principal TEXT," +
        "historico_doenca_atual TEXT," +
        "exames_fisicos TEXT," +
        "diagnostico_fisioterapeutico TEXT," +
        "plano_tratamento TEXT," +
        "FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente)" +
        ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Criar as tabelas
            stmt.execute(sqlUsuarios);
            System.out.println("Tabela 'usuarios' verificada/criada.");
            stmt.execute(sqlPacientes);
            System.out.println("Tabela 'pacientes' verificada/criada.");
            stmt.execute(sqlSessoes);
            System.out.println("Tabela 'sessoes' verificada/criada.");
            stmt.execute(sqlAvaliacoes);
            System.out.println("Tabela 'avaliacoes' verificada/criada.");

            // Inserir usuário 'admin' se não existir
            int adminId = -1;
            try (ResultSet rs = stmt.executeQuery("SELECT id_usuario FROM usuarios WHERE login = 'admin';")) {
                if (!rs.next()) {
                    stmt.execute("INSERT INTO usuarios (login, senha, nome_completo) VALUES ('admin', 'admin123', 'Administrador do Sistema');");
                    System.out.println("Usuário 'admin' padrão criado.");
                    try (ResultSet rsAdmin = stmt.executeQuery("SELECT id_usuario FROM usuarios WHERE login = 'admin';")) {
                        if (rsAdmin.next()) {
                            adminId = rsAdmin.getInt("id_usuario");
                        }
                    }
                } else {
                    adminId = rs.getInt("id_usuario");
                }
            }

            // Inserir pacientes de exemplo se a tabela estiver vazia
            if (adminId != -1) {
                try (ResultSet rs = stmt.executeQuery("SELECT id_paciente FROM pacientes LIMIT 1;")) {
                    if (!rs.next()) {
                        System.out.println("Inserindo pacientes de exemplo para o admin (ID: " + adminId + ").");

                        String sqlInsert = "INSERT INTO pacientes(id_usuario, nome, cpf, genero, telefone, email, data_nascimento, paciente_corrida) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                        
                        // Paciente 1 (Comum)
                        System.out.println("-> Inserindo paciente comum de exemplo: Maria Silva");
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                            pstmt.setInt(1, adminId);
                            pstmt.setString(2, "Maria Silva");
                            pstmt.setString(3, "111.222.333-44");
                            pstmt.setString(4, "Feminino");
                            pstmt.setString(5, "(11) 98765-4321");
                            pstmt.setString(6, "maria.silva@email.com");
                            pstmt.setString(7, LocalDate.of(1990, 5, 15).toString());
                            pstmt.setBoolean(8, false);
                            pstmt.executeUpdate();
                        }

                        // Paciente 2 (Corrida)
                        System.out.println("-> Inserindo paciente de corrida de exemplo: Pedro Almeida");
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                            pstmt.setInt(1, adminId);
                            pstmt.setString(2, "Pedro Almeida");
                            pstmt.setString(3, "222.333.444-55");
                            pstmt.setString(4, "Masculino");
                            pstmt.setString(5, "(41) 96666-4444");
                            pstmt.setString(6, "pedro.almeida@email.com");
                            pstmt.setString(7, LocalDate.of(1975, 7, 3).toString());
                            pstmt.setBoolean(8, true);
                            pstmt.executeUpdate();
                        }
                        System.out.println("Pacientes de exemplo inseridos.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas ou inserir dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

}