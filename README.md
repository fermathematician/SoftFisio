# SoftFisio - Sistema de Gestão para Fisioterapia

*Última atualização: 21 de julho de 2025*

## 📖 Sobre o Projeto

O **SoftFisio** é um software de desktop, de código aberto, projetado para simplificar a rotina de fisioterapeutas. A aplicação, em seu estágio atual, oferece um sistema seguro de autenticação de usuários (fisioterapeutas) e o gerenciamento completo do cadastro de seus pacientes (CRUD - Criar, Ler, Atualizar e Deletar).

O sistema é construído sobre uma base de tecnologias robustas e amplamente utilizadas no mercado, garantindo estabilidade e uma fundação sólida para a implementação de futuras funcionalidades, como agendamento de sessões e prontuários de avaliação.

## 🛠️ Tecnologias e Versões

O projeto utiliza um conjunto de ferramentas modernas e estáveis do ecossistema Java.

| Tecnologia | Versão | Propósito |
| :--- | :--- | :--- |
| **Java (JDK)** | 11+ | Linguagem principal da aplicação. |
| **JavaFX** | 17.0.2+ | Framework para construção da interface gráfica. |
| **SQLite JDBC**| 3.45.1.0 | Driver de conexão para o banco de dados local. |
| **Apache Maven** | 3.6+ | Ferramenta de automação para build e gerenciamento de dependências. |

## 🚀 Como Executar o Projeto

Para clonar e executar este projeto em sua máquina local, siga os passos abaixo.

### Pré-requisitos
* **Git:** Para clonar o repositório.
* **JDK 11 ou superior:** Essencial para executar a aplicação.
* **Apache Maven:** Necessário para gerenciar as dependências e executar a aplicação.
* **Conexão com a Internet:** Para o download automático das dependências na primeira execução.

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/fermathematician/SoftFisio.git](https://github.com/fermathematician/SoftFisio.git)
    ```

2.  **Acesse a pasta do projeto:**
    ```bash
    cd SoftFisio
    ```

3.  **Execute a Aplicação:**
    Use o Maven para compilar o projeto e iniciar o software com o comando:
    ```bash
    mvn javafx:run
    ```
    Na primeira vez que o programa iniciar, ele criará um arquivo de banco de dados `fisioterapia.db` na raiz do projeto. Este banco de dados virá populado com um usuário administrador padrão para testes.
    * **Login:** `admin`
    * **Senha:** `admin123`

## 🏛️ Arquitetura do Sistema

O SoftFisio foi projetado utilizando uma **arquitetura em três camadas** para garantir a separação de responsabilidades, facilitando a manutenção e a escalabilidade do código.

`View (FXML)` → `Controller` **↔** `Service (Lógica de Negócio)` **↔** `DAO (Acesso a Dados)`

1.  **Camada de Apresentação (View/Controller):**
    * **Responsabilidade:** Exibir a interface gráfica, capturar as interações do usuário e delegar as operações para a camada de Serviço. É a única camada que "conhece" o JavaFX.
    * **Componentes:**
        * `resources/static/*.fxml`: Arquivos XML que definem a estrutura visual das janelas.
        * `controllers/*.java`: Classes que controlam a lógica da interface (ex: `LoginController`, `PatientCardController`), respondendo a eventos e formatando dados para exibição.

2.  **Camada de Serviço (Lógica de Negócio):**
    * **Responsabilidade:** Orquestrar as regras de negócio da aplicação, como validações e coordenação de operações. Serve como uma ponte entre os Controllers e os DAOs.
    * **Componentes:**
        * `services/*.java`: Classes como `AuthServiceUsuario` e `AuthServicePaciente` que contêm a lógica principal (ex: "para cadastrar um usuário, a senha deve ter no mínimo 6 caracteres").
        * `models/*.java`: Classes "puras" (POJOs) como `Usuario` e `Paciente`, que apenas representam as entidades do sistema.

3.  **Camada de Acesso a Dados (DAO):**
    * **Responsabilidade:** Ser a única parte do sistema que interage diretamente com o banco de dados SQLite, executando os comandos SQL.
    * **Componentes:**
        * `db/DatabaseManager.java`: Gerencia a conexão com o banco de dados e a criação inicial das tabelas.
        * `db/*DAO.java`: (Data Access Objects) Classes como `UsuarioDAO` e `PacienteDAO` que contêm os comandos SQL específicos (INSERT, SELECT, UPDATE, DELETE) para cada tabela.

## 🗃️ Estrutura do Banco de Dados

O SoftFisio utiliza um banco de dados SQLite local (`fisioterapia.db`) para armazenar todas as informações. As tabelas atuais são:

### `usuarios`
* **Propósito:** Armazena os dados de cadastro dos fisioterapeutas que acessam o sistema.
* **Colunas:**
    * `id_usuario`: `INTEGER PRIMARY KEY AUTOINCREMENT` - Identificador único do usuário.
    * `login`: `TEXT UNIQUE NOT NULL` - Nome de usuário para acesso ao sistema.
    * `senha`: `TEXT NOT NULL` - Senha do usuário (atualmente em texto plano).
    * `nome_completo`: `TEXT NOT NULL` - Nome completo do fisioterapeuta.

### `pacientes`
* **Propósito:** Contém as informações de identificação e contato de cada paciente.
* **Colunas:**
    * `id_paciente`: `INTEGER PRIMARY KEY AUTOINCREMENT` - Identificador único do paciente.
    * `id_usuario`: `INTEGER NOT NULL` - Chave estrangeira que vincula o paciente ao fisioterapeuta responsável (`FOREIGN KEY REFERENCES usuarios(id_usuario)`).
    * `nome`: `TEXT NOT NULL` - Nome completo do paciente.
    * `cpf`: `TEXT UNIQUE` - CPF do paciente (opcional, mas recomendado para unicidade).
    * `genero`: `TEXT` - Gênero do paciente.
    * `telefone`: `TEXT` - Telefone de contato.
    * `email`: `TEXT` - Email do paciente.
    * `data_nascimento`: `TEXT` - Data de nascimento (formato 'YYYY-MM-DD').
    * `data_cadastro`: `TEXT` - Data e hora do cadastro do paciente.

*(Nota: As tabelas para `avaliacoes` e `sessoes` estão planejadas para futuras versões do sistema.)*

## 🌳 Estrutura de Diretórios (Corrigida)

A estrutura segue o padrão de projetos Maven.

SoftFisio/
├── src/
│   └── main/
│       ├── java/
│       │   ├── MainApp.java                    # Ponto de entrada da aplicação
│       │   ├── controllers/
│       │   │   ├── LoginController.java
│       │   │   ├── RegisterController.java
│       │   │   ├── MainViewController.java
│       │   │   ├── PatientCardController.java
│       │   │   ├── CadastrarPacienteController.java
│       │   │   └── EditarPacienteController.java
│       │   ├── db/
│       │   │   ├── DatabaseManager.java
│       │   │   ├── UsuarioDAO.java
│       │   │   └── PacienteDAO.java
│       │   ├── models/
│       │   │   ├── Usuario.java
│       │   │   └── Paciente.java
│       │   └── services/
│       │       ├── AuthServiceUsuario.java
│       │       ├── AuthServicePaciente.java
│       │       └── SessaoUsuario.java
│       └── resources/
│           └── static/
│               ├── css/
│               │   ├── login-register.css
│               │   ├── main_view.css
│               │   └── cadastrar_paciente.css
│               ├── login.fxml
│               ├── register.fxml
│               ├── main_view.fxml
│               ├── patient_card.fxml
│               ├── cadastrar_paciente.fxml
│               └── editar_paciente.fxml
│
├── fisioterapia.db                         # Arquivo do banco de dados (gerado na 1ª execução)
├── pom.xml                                 # Arquivo de configuração do Maven
└── README.md                               # Esta documentação
