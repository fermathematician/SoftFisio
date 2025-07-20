#!/bin/bash

echo "🚀 Iniciando configuração inteligente do ambiente FisioApp..."

# --- 1. Verificação do Java ---
echo "🔎 Verificando o Java (JDK)..."
if ! command -v java &> /dev/null; then
    echo "❌ Java (JDK) não encontrado. Por favor, instale o OpenJDK 11 ou superior."
    exit 1
fi
echo "✅ Java encontrado."
java -version

echo "" # Linha em branco para separar as seções

# --- 2. Verificação e Instalação Automática do Maven ---
echo "🔎 Verificando o Apache Maven..."
if ! command -v mvn &> /dev/null; then
    echo "⚠️  Maven não encontrado. Tentando instalar automaticamente..."

    # Verifica se o sistema operacional possui o gerenciador de pacotes APT (padrão do Ubuntu/Debian)
    if command -v apt &> /dev/null; then
        echo "   -> Sistema compatível com APT detectado."
        
        # O 'sudo' pedirá sua senha de administrador para instalar pacotes
        sudo apt update
        sudo apt install maven -y # O '-y' confirma a instalação automaticamente

        # Confirmação final após a tentativa de instalação
        if ! command -v mvn &> /dev/null; then
            echo "❌ FALHA: A instalação automática do Maven não teve sucesso."
            echo "   Por favor, tente instalar manualmente com 'sudo apt install maven' e rode o script de novo."
            exit 1
        fi
        echo "✅ Maven foi instalado com sucesso!"
    else
        echo "❌ FALHA: Este script só pode instalar o Maven automaticamente em sistemas com APT (Ubuntu, Debian, etc)."
        echo "   Por favor, instale o Maven manualmente para sua distribuição e execute o script novamente."
        exit 1
    fi
else
    echo "✅ Maven já está instalado."
fi
mvn -version

echo ""

# --- 3. Compilação do Projeto ---
echo "📦 Baixando dependências e compilando o projeto com Maven..."
mvn clean install

if [ $? -eq 0 ]; then
    echo "✅ Build do projeto concluído com sucesso!"
    echo "👉 Para executar a aplicação, use o comando: mvn javafx:run"
else
    echo "❌ Falha no build do Maven. Verifique os erros no console acima."
    exit 1
fi

echo ""
echo "🎉 Ambiente configurado e pronto para uso!"