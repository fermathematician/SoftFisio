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

├── db/

│   ├── DatabaseManager.java  # Gerencia a conexão e inicialização do BD

│   └── UsuarioDAO.java       # Operações CRUD para a tabela 'usuarios'

│

├── src/

│   ├── controllers/

│   │   └── LoginController.java # Controla a tela de login

│   │

│   ├── models/

│   │   └── Usuario.java        # Representa a entidade 'usuario'

│   │

│   ├── services/

│   │   └── AuthService.java    # Contém a lógica de autenticação

│   │

│   └── MainApp.java            # Ponto de entrada da aplicação JavaFX

│

├── static/

│   └── main_view.fxml          # Estrutura da janela principal/boas-vindas

│

├── fisioterapia.db             # Arquivo do banco de dados (gerado na 1ª execução)

├── pom.xml                     # Arquivo de configuração do Maven com as dependências

├── README.md                   # Esta documentação

└── setup.sh                    # Script de configuração do ambiente

---


### `AvaliacaoViewController`:
 é um controlador JavaFX para uma interface de avaliação de paciente. Ele coleta dados como queixa principal, diagnóstico e plano de tratamento através de campos de texto. Ao final, ele salva essas informações no prontuário de um paciente específico e notifica a interface sobre o sucesso ou falha da operação.

* **AvaliacaoViewController()**: 
Construtor da classe. Ele inicializa o ProntuarioService, que é o serviço responsável pela lógica de negócio para gerenciar prontuários.

* **initData(Paciente paciente, OnHistoryChangedListener listener)**: 
Recebe os dados do paciente que está sendo avaliado e um "ouvinte" (listener) para notificar outras partes do sistema quando uma nova avaliação for salva.

* **handleSalvarAvaliacao()**: 
Ação acionada pelo botão "Salvar". Coleta os dados dos campos da tela, chama o serviço para registrar a avaliação e exibe uma mensagem de sucesso ou erro.

* **initialize()**:
 Método padrão do JavaFX, executado ao carregar a tela. Ele define a data atual como valor padrão no seletor de data (dataAvaliacaoPicker).

* **setMensagem(String mensagem, boolean isError)**: 
Exibe uma mensagem de feedback para o usuário. A cor do texto muda para vermelho se for um erro ou verde se for sucesso.

* **limparCampos()**: 
Limpa todos os campos de texto do formulário e redefine a data para o dia atual, geralmente após um salvamento bem-sucedido.

 ---

### `CadastrarPacienteController`: 
controlador JavaFX para uma tela de cadastro de novos pacientes. Ele gerencia um formulário que coleta dados pessoais como nome, CPF, data de nascimento e contato. O controlador valida e formata as entradas, processa o salvamento do novo paciente associando-o ao usuário logado e permite cancelar a operação retornando à tela anterior.

* **CadastrarPacienteController()**: 
Construtor da classe que inicializa o AuthServicePaciente, serviço responsável pela lógica de autenticação e cadastro de pacientes.

* **initialize()**:
Método executado na inicialização da tela. Configura os componentes da interface, como preencher as opções de gênero, aplicar máscaras de formatação aos campos de CPF e telefone, e configurar o seletor de data.

* **configureDatePicker()**: 
Método auxiliar privado que personaliza o seletor de data (dobPicker). Ele permite que o usuário digite a data manualmente no formato dd/MM/yyyy e define as regras para converter o texto em um objeto de data (LocalDate).

* **handleSave()**: 
Ação do botão "Salvar". Coleta todos os dados inseridos nos campos do formulário, chama o serviço de autenticação para registrar o paciente e exibe uma mensagem de sucesso ou erro na tela.

* **handleCancel()**: 
Ação do botão "Cancelar". Utiliza um serviço de navegação (NavigationService) para carregar e exibir a tela anterior, fechando o formulário de cadastro.

---

### `EditarAvaliacaoController`: 
controlador JavaFX para uma interface de edição de uma avaliação de paciente já existente. Ele carrega os dados de uma avaliação específica em um formulário, permitindo que o usuário altere as informações e a data da avaliação. Após a edição, ele salva as alterações no banco de dados e fornece uma opção para retornar à tela principal do prontuário do paciente.

* **EditarAvaliacaoController()**: 
Construtor da classe que inicializa o ProntuarioService, o qual contém a lógica para interagir com os dados do prontuário.

* **initData(Avaliacao avaliacao, Paciente paciente)**: 
Inicializa a tela com os dados da avaliação e do paciente que serão editados. Ele preenche o cabeçalho com o nome do paciente e os campos do formulário com os detalhes da avaliação existente.

* **populateForm()**: 
Método auxiliar privado que preenche os campos de texto (TextArea) do formulário com as informações da avaliação carregada.

* **handleSalvarAlteracoes()**: 
Ação do botão "Salvar". Coleta os dados (potencialmente alterados) dos campos do formulário, invoca o serviço para atualizar a avaliação no sistema e exibe uma mensagem de sucesso ou erro.

* **handleBackButton()**: 
Ação do botão "Voltar". Chama o método voltarParaProntuario() para navegar para a tela anterior.

* **voltarParaProntuario()**: 
Carrega e exibe a tela de prontuário do paciente (prontuario_view.fxml), passando os dados do paciente atual para o controlador daquela tela.

* **setMensagem(String mensagem, boolean isError)**: 
Exibe uma mensagem de feedback para o usuário, alterando a cor do texto para verde (sucesso) ou vermelho (erro).

---

### `EditarPacienteController`: 
Controlador JavaFX para uma tela de edição de dados de um paciente existente. Ele carrega as informações atuais de um paciente em um formulário, permitindo que o usuário modifique campos como nome, CPF, e contato. O controlador então salva essas alterações e permite que o usuário cancele a edição, retornando à tela anterior.


* **EditarPacienteController()**: 
Construtor da classe que inicializa o AuthServicePaciente, serviço responsável pela lógica de negócio de atualização de dados de pacientes.

* **initialize()**: 
Método padrão do JavaFX que é executado ao carregar a tela. Ele configura os componentes, como as opções do ComboBox de gênero e a aplicação de máscaras de formatação para os campos de CPF e telefone.

* **initData(Paciente paciente)**: 
Recebe o objeto do paciente a ser editado e utiliza seus dados para preencher os campos do formulário (nome, CPF, data de nascimento, etc.).

* **handleSave()**: 
Ação disparada pelo botão "Salvar". Coleta os dados dos campos do formulário, chama o serviço para atualizar as informações do paciente no sistema e exibe uma mensagem de sucesso ou erro.

* **handleCancel()**: 
Ação disparada pelo botão "Cancelar". Utiliza um serviço de navegação para carregar e exibir a tela anterior, descartando quaisquer alterações feitas no formulário

---

### `EditarSessaoController`: 
Controlador JavaFX para uma tela de edição de uma sessão de tratamento de um paciente. Ele carrega os dados de uma sessão específica, como a data e a anotação da evolução, permitindo que o usuário os modifique. Ao final, o controlador salva as alterações e permite ao usuário retornar para a tela de prontuário do paciente.


* **EditarSessaoController()**: 
Construtor da classe. Ele inicializa o ProntuarioService, que é o serviço responsável pela lógica de negócio para manipular os dados de prontuários, incluindo as sessões.

* **initData(Sessao sessao, Paciente paciente)**: 
Recebe os dados da sessão a ser editada e do paciente correspondente. Usa essas informações para preencher os componentes da tela, como o nome do paciente, a data e o texto da evolução da sessão.

* **handleBackButton()**: 
Ação do botão "Voltar". Carrega a tela principal do prontuário (prontuario_view.fxml), passando os dados do paciente de volta para garantir que o contexto seja mantido.

* **handleUpdateSessao()**: 
Ação do botão para salvar as alterações. Coleta a nova data e o texto da evolução, chama o ProntuarioService para atualizar os dados da sessão no sistema e exibe uma mensagem de sucesso ou erro.


---

### `LoginController`: 
Controlador JavaFX para uma tela de login de usuário. Ele gerencia a entrada de credenciais (login e senha), valida esses dados através de um serviço de autenticação e inclui uma funcionalidade para exibir/ocultar a senha. Em caso de sucesso, ele estabelece uma sessão de usuário e navega para a tela principal; caso contrário, exibe uma mensagem de erro ou permite ir para a tela de cadastro.

* **LoginController()**: 
Construtor da classe. Ele inicializa o AuthServiceUsuario, serviço que contém a lógica para autenticar as credenciais do usuário.

* **initialize()**: 
Método padrão do JavaFX, executado ao carregar a tela. Ele configura o estado inicial da interface, como sincronizar o campo de senha visível e o oculto, e definir o ícone "mostrar senha".

* **handleToggleSenhaAction()**: 
Ação disparada ao clicar no ícone de "olho". Alterna a visibilidade da senha, trocando entre um campo de senha (PasswordField) que oculta os caracteres e um campo de texto normal (TextField) que os exibe.

* **handleLoginButtonAction()**: 
Ação do botão de login. Coleta o login e a senha, chama o serviço de autenticação e, se as credenciais forem válidas, inicia a sessão do usuário e carrega a tela principal da aplicação. Se não, exibe uma mensagem de erro.

* **handleGoToRegisterButtonAction()**: 
Ação de um botão ou link para "Cadastrar-se". Navega da tela de login para a tela de registro de novos usuários (register.fxml).

---

### `MainViewController`: 
Controlador da tela principal da aplicação, que exibe uma lista de pacientes associados ao usuário logado. Ele carrega dinamicamente um "card" para cada paciente e permite buscar por nome em tempo real. A tela também oferece botões para adicionar um novo paciente, navegar para uma lista de pacientes especiais ("de corrida") e fazer logout.

* **initialize()**: 
Executado ao carregar a tela. Configura o nome do usuário logado, inicializa um "ouvinte" no campo de busca para filtrar a lista e agenda o carregamento inicial dos pacientes.

* **loadPatients()**: 
Busca no banco de dados os pacientes do usuário logado, cria um card visual (patient_card.fxml) para cada um e os exibe na tela.

* **filterPatients(String query)**: 
Filtra os cards de pacientes na tela em tempo real, exibindo apenas aqueles cujos nomes correspondem ao texto digitado no campo de busca.

* **updateViewVisibility()**: 
Gerencia a exibição da lista de pacientes. Se a lista estiver vazia ou se nenhum paciente corresponder à busca, ele esconde a lista e exibe uma mensagem informativa no lugar.

* **handlePacienteCorrida()**: 
Ação do botão "Pacientes de corrida". Navega o usuário para uma tela separada que lista apenas pacientes dessa categoria específica.

* **handleNewPatient()**: 
Ação do botão "Novo Paciente". Leva o usuário para a tela de cadastro de um novo paciente.

* **handleLogout()**: 
Ação do botão "Sair". Encerra a sessão do usuário atual e retorna para a tela de login.

* **onPatientDeleted(Paciente paciente)**: 
Método chamado quando um paciente é excluído a partir de seu card. Ele simplesmente recarrega a lista de pacientes para refletir a remoção.

---

### `PacientesCorridaController`: 
Controlador JavaFX para uma tela que exibe uma lista de pacientes de uma categoria especial: "pacientes de corrida". Ele é muito similar à tela principal, mas filtra e carrega apenas os pacientes marcados com essa característica. A tela permite buscar, adicionar novos pacientes, fazer logout e retornar à lista de pacientes comuns.

* **initialize()**: 
Método executado ao carregar a tela. Ele prepara a interface, exibindo o nome do usuário logado, configurando o campo de busca e agendando o carregamento dos pacientes "de corrida".

* **loadPatients()**: 
Busca no banco de dados e carrega dinamicamente na tela apenas os pacientes do usuário logado que estão marcados como "pacientes de corrida".

* **filterPatients(String query)**: 
Filtra em tempo real a lista de pacientes exibida, mostrando apenas os cards cujos nomes correspondem ao texto da busca.

* **updateViewVisibility()**: 
Gerencia a interface para exibir a lista de pacientes ou uma mensagem de "lista vazia" caso nenhum paciente "de corrida" esteja cadastrado ou seja encontrado na busca.

* **handleVerComuns()**: 
Ação do botão "Ver Comuns". Navega o usuário de volta para a tela principal, que exibe a lista de todos os outros pacientes.

* **handleNewPatient()**: 
Ação do botão "Novo Paciente". Leva o usuário para a tela de cadastro, onde um novo paciente (de qualquer tipo) pode ser criado.

* **handleLogout()**: 
Ação do botão "Sair". Finaliza a sessão do usuário e retorna à tela de login da aplicação.

* **onPatientDeleted(Paciente paciente)**: 
Método que é chamado quando um paciente é excluído. Ele atualiza a tela recarregando a lista de pacientes para remover o card do paciente que foi deletado.

---

### `PatientCardController`: 
Controlador para um componente de interface reutilizável: um "card de paciente". Cada card exibe as informações resumidas de um único paciente e oferece botões de ação. Ele permite visualizar o prontuário completo, editar os dados do paciente ou excluí-lo, notificando a tela principal após a exclusão para que a lista seja atualizada.


* **OnPatientDeletedListener (Interface)**: 
Define um contrato (método onPatientDeleted) que permite que a tela principal (que exibe os cards) seja notificada quando um paciente é excluído.

* **PatientCardController()**: 
Construtor que inicializa o AuthServicePaciente, o serviço que contém a lógica para deletar um paciente.

* **setOnPatientDeletedListener(OnPatientDeletedListener listener)**: 
Permite que a tela que cria o card (ex: MainViewController) se registre para "ouvir" o evento de exclusão.

* **setData(Paciente paciente)**: 
Preenche todos os campos de texto (Label) do card com os dados de um objeto Paciente específico.

* **handleViewRecord()**: 
Ação do botão "Ver Prontuário". Leva o usuário para a tela de prontuário detalhado do paciente, passando as informações do paciente para essa nova tela.

* **handleDelete()**: 
Ação do ícone de lixeira. Exibe um pop-up de confirmação e, se o usuário confirmar, chama o serviço para deletar o paciente e notifica o "ouvinte" sobre a exclusão.

* **handleEdit()**: 
Ação do botão "Editar". Navega para a tela de edição de paciente, já preenchendo o formulário com os dados do paciente deste card.

--- 

### `ProntuarioViewController`: 
Controlador complexo para a tela de prontuário eletrônico de um paciente. Ele funciona como um painel central que organiza todas as informações do paciente em abas: uma para adicionar novas avaliações, uma para gerenciar sessões de tratamento, uma para ver o histórico cronológico completo e uma para gerenciar anexos de mídia. O controlador também permite gerar um relatório completo em PDF com todo o histórico do paciente.

* **onHistoryChanged()**: 
Acionado quando uma avaliação ou sessão é criada/deletada, garantindo que o histórico completo e a lista de sessões sejam atualizados.

* **initData(Paciente paciente)**: 
Inicializa toda a tela com os dados de um paciente específico, configurando o cabeçalho e os controladores das abas internas.

* **setupHeader()**: 
Preenche o cabeçalho da tela com o nome, CPF e idade do paciente.

* **handleBackButton()**: 
Ação do botão "Voltar", que navega o usuário para a tela de listagem de pacientes anterior.

* **carregarHistoricoCompleto()**: Busca todas as sessões e avaliações, ordena-as por data e cria os cards visuais para exibir na aba de histórico.

* **createHistoryCard(...)**: 
Cria o componente visual (um card) para um item do histórico (avaliação ou sessão), incluindo botões de editar e excluir.

* **updateHistoryVisibility()**: 
Mostra a lista de histórico ou uma mensagem de "histórico vazio", dependendo se há itens para exibir.

* **carregarAnexos()**: 
Carrega todos os anexos de mídia (fotos, vídeos) do paciente e exibe-os como cards na aba de anexos.

* **updateAnexosVisibility()**: 
Mostra a galeria de anexos ou uma mensagem de "nenhum anexo", conforme o caso.

* **handleDelete(Sessao sessao)**: 
Exibe um alerta de confirmação e, se confirmado, exclui uma sessão específica e atualiza a tela.

* **handleEdit(Sessao sessao)**: 
Abre a tela de edição para a sessão selecionada.

* **createCampo(String titulo, String texto)**: 
Método auxiliar que cria um par de Label (título e conteúdo) para exibir detalhes dentro de um card de avaliação.

* **handleDeleteA(Avaliacao avaliacao)**:
Exibe um alerta de confirmação e, se confirmado, exclui uma avaliação específica e atualiza a tela.

* **handleEditA(Avaliacao avaliacao)**: 
Abre a tela de edição para a avaliação selecionada.

* **abrirVisualizador(Anexo anexo)**: 
Abre uma nova janela para visualizar um anexo (imagem ou vídeo) em tamanho maior.

* **handleAdicionarAnexo()**: 
Abre um seletor de arquivos para que o usuário possa adicionar uma nova imagem ou vídeo como anexo ao prontuário.

* **criarCampoPdf(...)**: 
Método auxiliar que cria um parágrafo formatado (título em negrito e texto) para o relatório em PDF.

* **handleGerarPdf()**: 
Reúne todo o histórico do paciente e utiliza a biblioteca iTextPDF para gerar um relatório fisioterapêutico completo em formato PDF.

---


### `RegisterController`: 
Controlador JavaFX para uma tela de cadastro de novos usuários. Ele gerencia um formulário que coleta nome completo, login e senha, com um campo de confirmação de senha. O controlador possui uma funcionalidade para mostrar/ocultar as senhas e utiliza um serviço para validar e salvar o novo usuário, retornando feedback visual sobre o sucesso ou falha da operação.


* **RegisterController()**: 
Construtor da classe, responsável por inicializar o AuthServiceUsuario, que contém a lógica de negócio para cadastrar usuários.

* **initialize()**: 
Método executado ao carregar a tela. Ele configura os componentes, como sincronizar os campos de senha visíveis e ocultos e definir os ícones iniciais para "mostrar/ocultar senha".

* **handleToggleSenhaAction()**: 
Ação do ícone de "olho" do campo de senha principal. Chama o método auxiliar toggleIcon para alternar a visibilidade da senha.

* **handleToggleConfirmarSenhaAction()**: 
Ação do ícone de "olho" do campo de confirmação de senha. Também utiliza o método toggleIcon para gerenciar a visibilidade.

* **toggleIcon(...)**: 
Método auxiliar privado que implementa a lógica de alternar a visibilidade entre um campo de senha (PasswordField) e um campo de texto (TextField), atualizando o ícone correspondente.

* **handleRegisterButtonAction()**: 
Ação do botão "Cadastrar". Coleta os dados do formulário, envia para o serviço de autenticação para realizar o cadastro e exibe uma mensagem de sucesso ou erro.

* **handleBackToLoginButtonAction()**: 
Ação do botão "Voltar". Navega o usuário de volta para a tela de login.


---


### `TreatmentViewController`: 
Controlador JavaFX que gerencia a aba de "Sessões de Tratamento" dentro do prontuário de um paciente. Ele permite ao usuário registrar novas sessões de tratamento com data e anotações sobre a evolução. Além disso, ele exibe uma lista de todas as sessões anteriores, com opções para editar ou excluir cada uma delas.

* **TreatmentViewController()**: 
Construtor da classe que inicializa o ProntuarioService, responsável pela lógica de manipulação dos dados das sessões.

* **initialize()**: 
Método executado ao carregar a tela. Ele define a data atual como padrão no seletor de data e configura o botão de salvar.

* **initData(Paciente paciente, OnHistoryChangedListener listener)**: 
Recebe os dados do paciente e um "ouvinte" (listener) da tela principal. Dispara o carregamento inicial das sessões.

* **loadSessoes()**: 
Carrega do banco de dados todas as sessões do paciente atual, criando um "card" visual para cada uma e exibindo-as em uma lista.

* **updateSessionsVisibility()**: 
Gerencia a interface, exibindo a lista de sessões ou uma mensagem de "lista vazia" caso o paciente não tenha nenhuma sessão registrada.

* **createSessionCard(Sessao sessao)**: 
Cria o componente visual (um VBox) que representa um único card de sessão na lista, incluindo a data, o texto da evolução e os botões de ação.

* **handleSaveSessao()**: 
Ação do botão "Salvar". Coleta os dados do formulário, cadastra a nova sessão e notifica a tela principal (ProntuarioViewController) sobre a mudança.

* **handleDelete(Sessao sessao)**: 
Ação do ícone de lixeira. Exibe um alerta de confirmação e, se o usuário concordar, remove a sessão do sistema e atualiza a lista.

* **handleEdit(Sessao sessao)**: 
Ação do botão "Editar". Navega para a tela de edição, passando os dados da sessão selecionada para serem modificados.

* **setMensagem(String mensagem, boolean isError)**: 
Exibe uma mensagem de feedback para o usuário (ex: "Sessão salva com sucesso!"), com a cor verde para sucesso e vermelha para erro.

---


### `VisualizadorMidiaController`: 
Controlador JavaFX para uma janela de visualização de mídia. Ele é projetado para abrir um arquivo e determinar se é uma imagem ou um vídeo. O controlador então exibe o conteúdo no componente apropriado, seja um visualizador de imagens ou um reprodutor de vídeo, e oferece um botão para fechar a janela.

* **initData(String caminhoArquivo)**: 
Recebe o caminho de um arquivo, verifica sua extensão para identificar se é uma imagem (png, jpg, etc.) ou um vídeo (mp4, mov, etc.) e o carrega no componente visual correspondente (ImageView para imagens ou MediaView para vídeos).

* **handleClose()**: 
Ação do botão "Fechar". Interrompe a reprodução de qualquer vídeo que esteja em andamento para liberar recursos do sistema e, em seguida, fecha a janela do visualizador.

