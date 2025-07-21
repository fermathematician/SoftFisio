// db/DatabaseManager.java
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement; // Importado para usar PreparedStatement para buscar o ID do admin
import java.sql.ResultSet;       // Importado para usar ResultSet
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;     // Importado para datas de nascimento
import java.time.LocalDateTime; // Importado para datas de cadastro
import java.time.format.DateTimeFormatter; // Importado para formatar datas

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:fisioterapia.db";

    // O construtor privado continua, pois não queremos que esta classe seja instanciada.
    private DatabaseManager() {}

    /**
     * Este método agora retorna uma NOVA conexão com o banco de dados toda vez que é chamado.
     * Isso é ideal para o SQLite e resolve o problema de conexão fechada.
     * @return Uma nova conexão JDBC.
     * @throws RuntimeException se houver uma falha ao criar a conexão.
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Falha ao criar conexão com o banco de dados: " + e.getMessage());
            throw new RuntimeException("Não foi possível conectar ao banco de dados.", e);
        }
    }

    /**
     * Inicializa o banco de dados, criando as tabelas 'usuarios' e 'pacientes'
     * se elas ainda não existirem. Também insere um usuário 'admin' padrão
     * e 4 pacientes de exemplo se as tabelas estiverem vazias.
     */
    public static void initializeDatabase() {
        // SQL para criar a tabela de usuários
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT," + // Renomeado para id_usuario conforme sua documentação
                "login TEXT UNIQUE NOT NULL," +
                "senha TEXT NOT NULL," +
                "nome_completo TEXT NOT NULL" +
                ");";

        // SQL para criar a tabela de pacientes
        String sqlPacientes = "CREATE TABLE IF NOT EXISTS pacientes (" +
                "id_paciente INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_usuario INTEGER NOT NULL," +
                "nome TEXT NOT NULL," +
                "data_nascimento TEXT," + // Formato YYYY-MM-DD
                "cpf TEXT UNIQUE," +
                "genero TEXT," +
                "telefone TEXT," +
                "email TEXT," +
                "data_cadastro TEXT DEFAULT CURRENT_TIMESTAMP," + // Formato YYYY-MM-DD HH:MM:SS.SSS
                "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Criar a tabela de usuários
            stmt.execute(sqlUsuarios);
            System.out.println("Tabela 'usuarios' verificada/criada.");

            // 2. Inserir usuário 'admin' se não existir
            int adminId = -1;
            try (ResultSet rs = stmt.executeQuery("SELECT id_usuario FROM usuarios WHERE login = 'admin';")) {
                if (!rs.next()) { // Se o admin não existe
                    stmt.execute("INSERT INTO usuarios (login, senha, nome_completo) VALUES ('admin', 'admin123', 'Administrador do Sistema');");
                    System.out.println("Usuário 'admin' padrão criado.");
                    // Obter o ID do admin recém-criado
                    try (ResultSet rsAdmin = stmt.executeQuery("SELECT id_usuario FROM usuarios WHERE login = 'admin';")) {
                        if (rsAdmin.next()) {
                            adminId = rsAdmin.getInt("id_usuario");
                        }
                    }
                } else {
                    adminId = rs.getInt("id_usuario"); // Admin já existe, pega o ID
                    System.out.println("Usuário 'admin' já existe (ID: " + adminId + ").");
                }
            }

            // 3. Criar a tabela de pacientes
            stmt.execute(sqlPacientes);
            System.out.println("Tabela 'pacientes' verificada/criada.");

            // 4. Inserir pacientes de exemplo se a tabela de pacientes estiver vazia
            if (adminId != -1) { // Garante que o ID do admin foi obtido
                try (ResultSet rs = stmt.executeQuery("SELECT id_paciente FROM pacientes LIMIT 1;")) {
                    if (!rs.next()) { // Se a tabela de pacientes está vazia
                        System.out.println("Inserindo pacientes de exemplo para o admin (ID: " + adminId + ").");

                        // Formato para data de nascimento (LocalDate)
                        DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        // Formato para data de cadastro (LocalDateTime)
                        DateTimeFormatter dtcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

                        // Paciente 1
                        String sqlInsert1 = "INSERT INTO pacientes(id_usuario, nome, data_nascimento, cpf, genero, telefone, email, data_cadastro) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert1)) {
                            pstmt.setInt(1, adminId);
                            pstmt.setString(2, "Maria Silva");
                            pstmt.setString(3, LocalDate.of(1990, 5, 15).format(dobFormatter));
                            pstmt.setString(4, "111.222.333-44");
                            pstmt.setString(5, "Feminino");
                            pstmt.setString(6, "(11) 98765-4321");
                            pstmt.setString(7, "maria.silva@email.com");
                            pstmt.setString(8, LocalDateTime.now().format(dtcFormatter));
                            pstmt.executeUpdate();
                        }

                        // Paciente 2
                        String sqlInsert2 = "INSERT INTO pacientes(id_usuario, nome, data_nascimento, cpf, genero, telefone, email, data_cadastro) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert2)) {
                            pstmt.setInt(1, adminId);
                            pstmt.setString(2, "João Santos");
                            pstmt.setString(3, LocalDate.of(1985, 10, 20).format(dobFormatter));
                            pstmt.setString(4, "555.666.777-88");
                            pstmt.setString(5, "Masculino");
                            pstmt.setString(6, "(21) 99887-6655");
                            pstmt.setString(7, "joao.santos@email.com");
                            pstmt.setString(8, LocalDateTime.now().plusMinutes(1).format(dtcFormatter)); // Adiciona 1 minuto
                            pstmt.executeUpdate();
                        }

                        // Paciente 3
                        String sqlInsert3 = "INSERT INTO pacientes(id_usuario, nome, data_nascimento, cpf, genero, telefone, email, data_cadastro) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert3)) {
                            pstmt.setInt(1, adminId);
                            pstmt.setString(2, "Ana Costa");
                            pstmt.setString(3, LocalDate.of(2000, 1, 1).format(dobFormatter));
                            pstmt.setString(4, "999.888.777-66");
                            pstmt.setString(5, "Feminino");
                            pstmt.setString(6, "(31) 97777-5555");
                            pstmt.setString(7, "ana.costa@email.com");
                            pstmt.setString(8, LocalDateTime.now().plusMinutes(2).format(dtcFormatter)); // Adiciona 2 minutos
                            pstmt.executeUpdate();
                        }

                        // Paciente 4
                        String sqlInsert4 = "INSERT INTO pacientes(id_usuario, nome, data_nascimento, cpf, genero, telefone, email, data_cadastro) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert4)) {
                            pstmt.setInt(1, adminId);
                            pstmt.setString(2, "Pedro Almeida");
                            pstmt.setString(3, LocalDate.of(1975, 7, 3).format(dobFormatter));
                            pstmt.setString(4, "222.333.444-55");
                            pstmt.setString(5, "Masculino");
                            pstmt.setString(6, "(41) 96666-4444");
                            pstmt.setString(7, "pedro.almeida@email.com");
                            pstmt.setString(8, LocalDateTime.now().plusMinutes(3).format(dtcFormatter)); // Adiciona 3 minutos
                            pstmt.executeUpdate();
                        }
                        System.out.println("4 pacientes de exemplo inseridos.");
                    } else {
                        System.out.println("Tabela 'pacientes' já contém dados. Não inserindo pacientes de exemplo.");
                    }
                }
            } else {
                System.err.println("Não foi possível obter o ID do usuário 'admin'. Pacientes de exemplo não foram inseridos.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas ou inserir dados: " + e.getMessage());
            e.printStackTrace(); // Para ver o stack trace completo
        }
    }
}