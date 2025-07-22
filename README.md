# SoftFisio - Sistema de Gestão para Fisioterapia

*Última atualização: 19 de julho de 2025*

## 📖 Sobre o Projeto

O **SoftFisio** é um software de desktop, de código aberto, projetado para simplificar a rotina de fisioterapeutas e clínicas. A aplicação permite o gerenciamento completo de pacientes, o agendamento de sessões e o acompanhamento detalhado da evolução de cada tratamento, tudo em uma interface local, rápida e segura.

O sistema é construído sobre uma base de tecnologias robustas e amplamente utilizadas no mercado, garantindo estabilidade e a possibilidade de futuras expansões.

## 🛠️ Tecnologias e Versões

O projeto utiliza um conjunto de ferramentas modernas e estáveis do ecossistema Java.

| Tecnologia | Versão | Propósito |
| :--- | :--- | :--- |
| **Java (JDK)** | 11+ | Linguagem principal da aplicação. |
| **JavaFX** | 17.0.2 | Framework para construção da interface gráfica. |
| **SQLite JDBC**| 3.45.1.0 | Driver de conexão para o banco de dados local. |
| **Apache Maven** | 3.6+ | Ferramenta de automação para build e gerenciamento de dependências. |

## 🚀 Como Executar o Projeto

Para clonar e executar este projeto em sua máquina local, siga os passos abaixo.

### Pré-requisitos
* **Git:** Para clonar o repositório.
* **JDK 11 ou superior:** Essencial para executar a aplicação.
* **Conexão com a Internet:** Para o download automático das dependências na primeira execução.

### Passo a Passo

1.  **Clone o repositório:**
    Abra seu terminal e execute o seguinte comando:
    ```bash
    git clone [https://github.com/fermathematician/SoftFisio.git](https://github.com/fermathematician/SoftFisio.git)
    ```

2.  **Acesse a pasta do projeto:**
    ```bash
    cd SoftFisio
    ```

3.  **Execute o script de configuração:**
    Este script inteligente irá verificar as dependências (como o Maven) e instalá-las se necessário (em sistemas Ubuntu/Debian). Ele também irá compilar o projeto.
    ```bash
    # Dê permissão de execução ao script (apenas na primeira vez)
    chmod +x setup.sh

    # Rode o script
    ./setup.sh
    ```
    *Na primeira execução, o script pode pedir sua senha de administrador para instalar o Maven.*

4.  **Execute a Aplicação:**
    Após a configuração ser concluída com sucesso, inicie o software com o comando:
    ```bash
    mvn javafx:run
    ```
    Na primeira vez que o programa iniciar, ele criará um arquivo de banco de dados `fisioterapia.db` na raiz do projeto com tabelas e um usuário administrador padrão (`login: admin`, `senha: admin123`).

## 🏛️ Arquitetura do Sistema

O SoftFisio foi projetado utilizando uma **arquitetura em três camadas** para garantir a separação de responsabilidades, facilitando a manutenção e a escalabilidade do código.

`Frontend (Controllers)` **↔** `Backend (Services)` **↔** `Database (DAO)`

1.  **Módulo Frontend (Camada de Apresentação):**
    * **Responsabilidade:** Exibir a interface gráfica e capturar as interações do usuário. É a única camada que "conhece" o JavaFX.
    * **Componentes:**
        * `static/*.fxml`: Arquivos XML que definem a estrutura visual das janelas.
        * `src/controllers/*.java`: Classes Java que controlam a lógica da interface (ex: `LoginController`), respondendo a cliques de botão e preenchimento de campos. Elas delegam as operações para a camada de Serviço.

2.  **Módulo Backend (Camada de Serviço/Lógica de Negócio):**
    * **Responsabilidade:** Orquestrar as regras de negócio da aplicação. Serve como uma ponte entre o Frontend e os dados, sem ter conhecimento de nenhum deles.
    * **Componentes:**
        * `src/services/*.java`: Classes como `AuthService` que contêm a lógica principal (ex: como validar um login).
        * `src/models/*.java`: Classes "puras" (POJOs) como `Usuario` e `Paciente`, que apenas representam as estruturas de dados do sistema.

3.  **Módulo de Banco de Dados (Camada de Acesso a Dados - DAO):**
    * **Responsabilidade:** Ser a única parte do sistema que interage diretamente com o banco de dados SQLite. Abstrai toda a complexidade do SQL.
    * **Componentes:**
        * `db/DatabaseManager.java`: Gerencia a conexão com o banco de dados e a criação inicial das tabelas.
        * `db/*DAO.java`: (Data Access Objects) Classes como `UsuarioDAO` que contêm os comandos SQL específicos para cada entidade (INSERT, SELECT, UPDATE, DELETE).

## 🗃️ Estrutura do Banco de Dados

O SoftFisio utiliza um banco de dados SQLite local (`fisioterapia.db`) para armazenar todas as informações do sistema. A estrutura foi projetada para ser eficiente e escalável, focando na gestão de usuários (fisioterapeutas), pacientes, suas avaliações e sessões de tratamento.

Abaixo estão as tabelas principais e seus respectivos campos:

### `usuarios`
* **Propósito:** Armazena os dados de cadastro dos fisioterapeutas que acessam o sistema.
* **Colunas:**
    * `id_usuario`: `INTEGER PRIMARY KEY AUTOINCREMENT` - Identificador único do usuário.
    * `nome`: `TEXT NOT NULL` - Nome completo do fisioterapeuta.
    * `email`: `TEXT UNIQUE NOT NULL` - Email do fisioterapeuta (utilizado para login, deve ser único).
    * `senha_hash`: `TEXT NOT NULL` - Hash da senha do usuário (armazenado de forma segura).

### `pacientes`
* **Propósito:** Contém as informações de identificação e contato de cada paciente.
* **Colunas:**
    * `id_paciente`: `INTEGER PRIMARY KEY AUTOINCREMENT` - Identificador único do paciente.
    * `id_usuario`: `INTEGER NOT NULL` - Chave estrangeira, vincula o paciente ao fisioterapeuta responsável (`FOREIGN KEY REFERENCES usuarios(id_usuario)`).
    * `nome`: `TEXT NOT NULL` - Nome completo do paciente.
    * `data_nascimento`: `TEXT` - Data de nascimento do paciente (formato 'YYYY-MM-DD').
    * `cpf`: `TEXT UNIQUE` - CPF do paciente (opcional, mas recomendado para unicidade).
    * `genero`: `TEXT` - Gênero do paciente (ex: 'Masculino', 'Feminino', 'Outro').
    * `telefone`: `TEXT` - Telefone de contato.
    * `email`: `TEXT` - Email do paciente.
    * `data_cadastro`: `TEXT DEFAULT CURRENT_TIMESTAMP` - Data e hora do cadastro do paciente no sistema.

### `avaliacoes`
* **Propósito:** Armazena os dados da avaliação inicial e/ou reavaliações do paciente. Um paciente pode ter múltiplas avaliações ao longo do tempo.
* **Colunas:**
    * `id_avaliacao`: `INTEGER PRIMARY KEY AUTOINCREMENT` - Identificador único da avaliação.
    * `id_paciente`: `INTEGER NOT NULL` - Chave estrangeira, vincula a avaliação a um paciente específico (`FOREIGN KEY REFERENCES pacientes(id_paciente)`).
    * `data_avaliacao`: `TEXT DEFAULT CURRENT_TIMESTAMP` - Data e hora da realização da avaliação.
    * `queixa_principal`: `TEXT` - Descrição da queixa principal do paciente.
    * `historia_doenca_atual`: `TEXT` - Histórico da doença ou condição atual.
    * `historico_medico_passado`: `TEXT` - Histórico médico prévio, cirurgias, medicamentos.
    * `exame_fisico`: `TEXT` - Detalhes do exame físico realizado.
    * `testes_especificos`: `TEXT` - Resultados de testes específicos (ortopédicos, neurológicos, etc.).
    * `diagnostico_fisioterapeutico`: `TEXT` - O diagnóstico estabelecido pelo fisioterapeuta.
    * `plano_tratamento`: `TEXT` - Detalhamento do plano de tratamento proposto.
    * `objetivos_tratamento`: `TEXT` - Metas e objetivos terapêuticos.
    * `observacoes_adicionais`: `TEXT` - Quaisquer outras observações relevantes sobre a avaliação.
    * *Nota: Todos os campos de texto longo (`TEXT`) suportam parágrafos e quebras de linha.*

### `sessoes`
* **Propósito:** Registra o progresso e as intervenções de cada sessão de tratamento realizada.
* **Colunas:**
    * `id_sessao`: `INTEGER PRIMARY KEY AUTOINCREMENT` - Identificador único da sessão.
    * `id_paciente`: `INTEGER NOT NULL` - Chave estrangeira, vincula a sessão a um paciente específico (`FOREIGN KEY REFERENCES pacientes(id_paciente)`).
    * `data_sessao`: `TEXT NOT NULL` - Data e hora da realização da sessão.
    * `evolucao_texto`: `TEXT NOT NULL` - Texto detalhado sobre a evolução do paciente na sessão e as intervenções realizadas.
    * `observacoes_sessao`: `TEXT` - Observações adicionais específicas da sessão.

## 🌳 Estrutura de Diretórios (Tree)

```
SoftFisio/
├── db/
│   ├── DatabaseManager.java  # Gerencia a conexão e inicialização do BD
│   └── UsuarioDAO.java       # Operações CRUD para a tabela 'usuarios'
│
├── src/
│   ├── controllers/
│   │   └── LoginController.java # Controla a tela de login
│   │
│   ├── models/
│   │   └── Usuario.java        # Representa a entidade 'usuario'
│   │
│   ├── services/
│   │   └── AuthService.java    # Contém a lógica de autenticação
│   │
│   └── MainApp.java            # Ponto de entrada da aplicação JavaFX
│
├── static/
│   └── main_view.fxml          # Estrutura da janela principal/boas-vindas
│
├── fisioterapia.db             # Arquivo do banco de dados (gerado na 1ª execução)
├── pom.xml                     # Arquivo de configuração do Maven com as dependências
├── README.md                   # Esta documentação
└── setup.sh                    # Script de configuração do ambiente
```
---
