# FisioApp - Software de Gestão para Fisioterapia

## 📖 Sobre

O **FisioApp** é um sistema de desktop para auxiliar fisioterapeutas no gerenciamento de pacientes e sessões. Construído com JavaFX para a interface e SQLite para o armazenamento de dados local.

## 🛠️ Tecnologias

* **Linguagem:** Java 11+
* **Interface:** JavaFX 17+
* **Banco de Dados:** SQLite
* **Build Tool:** Maven

## 🗄️ Estrutura do Projeto

* **/src**: Código-fonte Java.
* **/db**: Scripts e gerenciador do banco de dados.
* **/static**: Arquivos FXML, CSS e outras mídias.
* **pom.xml**: Arquivo de configuração e dependências do Maven.
* **fisioterapia.db**: Arquivo do banco de dados (gerado na primeira execução).

## 🚀 Como Configurar e Executar

### Pré-requisitos

* JDK (Java Development Kit) 11 ou superior.
* Apache Maven.

### 1. Dependências

O Maven gerencia todas as dependências listadas no `pom.xml`. As principais são:
* `org.openjfx:javafx-controls`
* `org.openjfx:javafx-fxml`
* `org.xerial:sqlite-jdbc`

### 2. Compilar o Projeto

Abra o terminal na raiz do projeto e execute:
```bash
# Compila o código e baixa as dependências
mvn clean install
```
O Maven está configurado para reconhecer a estrutura de pastas `src`, `db` e `static`.

### 3. Executar a Aplicação

```bash
# Executa a aplicação
mvn javafx:run
```
Na primeira execução, o arquivo `fisioterapia.db` será criado na raiz do projeto, contendo as tabelas e dados de exemplo.

---