package db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class DatabaseManager {

    // Nome da pasta onde o banco de dados será armazenado.
    // O ponto no início a torna oculta em sistemas Linux/macOS.
    private static final String APP_DATA_DIRECTORY = ".fisioterapia-app";
    // Nome do arquivo do banco de dados.
    private static final String DB_FILE_NAME = "fisioterapia.db";

    private DatabaseManager() {}

    /**
     * Constrói o caminho completo para o arquivo do banco de dados,
     * garantindo que o diretório de dados da aplicação exista.
     *
     * @return O caminho absoluto para o arquivo do banco de dados.
     */
    private static String getDatabasePath() {
        // Pega o diretório 'home' do usuário (funciona em Windows, macOS, Linux).
        String userHome = System.getProperty("user.home");

        // Cria um objeto Path para o nosso diretório de dados.
        Path dataDir = Paths.get(userHome, APP_DATA_DIRECTORY);

        // Se o diretório não existir, cria-o.
        if (Files.notExists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
                System.out.println("Diretório de dados da aplicação criado em: " + dataDir);
            } catch (IOException e) {
                System.err.println("Falha ao criar o diretório de dados: " + e.getMessage());
                // Lança uma exceção em tempo de execução para parar a aplicação se não for possível criar o diretório.
                throw new RuntimeException("Não foi possível criar o diretório de dados.", e);
            }
        }

        // Retorna o caminho completo para o arquivo .db.
        return dataDir.resolve(DB_FILE_NAME).toString();
    }

    /**
     * Obtém uma nova conexão com o banco de dados SQLite.
     * A URL de conexão é gerada dinamicamente para o local correto.
     *
     * @return Uma objeto Connection para o banco de dados.
     */
    public static Connection getConnection() {
        // Constrói a String de conexão JDBC usando o caminho dinâmico.
        String dbUrl = "jdbc:sqlite:" + getDatabasePath();
        try {
            return DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            System.err.println("Falha ao criar conexão com o banco de dados: " + e.getMessage());
            throw new RuntimeException("Não foi possível conectar ao banco de dados.", e);
        }
    }

    /**
     * Inicializa o banco de dados, criando as tabelas se elas ainda não existirem.
     * (O conteúdo deste método permanece o mesmo, pois ele já usa o getConnection() modificado).
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
        "doenca_atual TEXT," +
        "historia_pregressa TEXT," +
        "inspecao_palpacao TEXT," +
        "adm TEXT," +
        "forca_muscular TEXT," +
        "avaliacao_funcional TEXT," +
        "testes_especiais TEXT," +
        "escalas_funcionais TEXT," +
        "diagnostico_cinesiologico TEXT," +
        "plano_tratamento TEXT," +
        "FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente)" +
        ");";

        String sqlAnexos = "CREATE TABLE IF NOT EXISTS anexos (" +
        "id_anexo INTEGER PRIMARY KEY AUTOINCREMENT," +
        "id_paciente INTEGER NOT NULL," +
        "caminho_arquivo TEXT NOT NULL UNIQUE," +
        "tipo_midia TEXT NOT NULL," +
        "descricao TEXT," +
        "data_anexo TEXT NOT NULL," +
        "id_sessao_ref INTEGER," +
        "id_avaliacao_ref INTEGER," +
        "FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente) ON DELETE CASCADE," +
        "FOREIGN KEY (id_sessao_ref) REFERENCES sessoes(id_sessao) ON DELETE SET NULL," +
        "FOREIGN KEY (id_avaliacao_ref) REFERENCES avaliacoes(id_avaliacao) ON DELETE SET NULL" +
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
            stmt.execute(sqlAnexos);
            System.out.println("Tabela 'anexos' verificada/criada.");

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