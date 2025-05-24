# 🗂️ Gerenciador de Boards

Este projeto é um gerenciador de boards desenvolvido em **Java** para execução via terminal. Permite criar boards personalizados com múltiplas colunas, além de gerenciar cards por meio de operações como criação, movimentação entre colunas, bloqueio, desbloqueio e cancelamento.

O sistema utiliza banco de dados relacional para persistência das informações, oferecendo uma aplicação simples, prática e eficiente para organização e acompanhamento de tarefas. Ideal para quem deseja aprender conceitos de estrutura de dados, **JDBC** e design de sistemas modulares em **Java**.

## 🧠 Sobre o Projeto

Este projeto é um sistema simples e funcional de gerenciamento de tarefas baseado no modelo Kanban. É possível criar boards personalizados, adicionar colunas intermediárias, selecionar boards existentes e excluí-los do sistema.

A estrutura básica de um board inclui:
- Coluna inicial
- Colunas pendentes personalizadas
- Coluna final
- Coluna de cancelamento

Tudo é salvo e gerenciado com acesso a banco de dados, usando JDBC.

---

## ✅ Funcionalidades

- ✅ Criar boards com colunas iniciais, intermediárias, finais e de cancelamento
- ✅ Listar boards existentes
- ✅ Navegar entre boards e gerenciar seus cards
- ✅ Criar, mover, bloquear, desbloquear e cancelar cards
- ✅ Visualizar estado geral do board ou colunas específicas
- ✅ Visualizar um card por ID
- ✅ Persistência com banco de dados relacional
- ✅ Navegação por menus no terminal
- ✅ Integração com banco de dados via JDBC

---

## 📦 Tecnologias e Ferramentas

| Tecnologia | Uso |
|------------|-----|
| Java 17+   | Lógica da aplicação |
| JDBC       | Conexão com banco de dados |
| MySQL      | Banco de dados |
| Gradle     | Gerenciamento do projeto |
| Flyway     | Versionamento do banco de dados |
| Scanner    | Entrada de dados pelo terminal |