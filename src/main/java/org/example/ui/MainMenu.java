package org.example.ui;

import org.example.persistence.entity.BoardColumnEntity;
import org.example.persistence.entity.BoardColumnKindEnum;
import org.example.persistence.entity.BoardEntity;
import org.example.service.BoardQueryService;
import org.example.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.persistence.config.ConnectionConfig.getConnection;
import static org.example.persistence.entity.BoardColumnKindEnum.*;

public class MainMenu { // Classe principal que representa o menu inicial do sistema

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n"); // Scanner para entrada de dados do usuário, utilizando quebra de linha como delimitador

    public void execute() throws SQLException { // Método que inicia o menu principal e lida com exceções SQL
        System.out.println("Bem-vindo ao gerenciador de boards, escolha a opção desejada"); // Mensagem de boas-vindas
        int option; // Variável para armazenar a opção do menu
        while (true) { // Loop infinito para manter o menu em execução até o usuário escolher sair
            // Exibe o menu principal
            System.out.println("1 - Criar um novo board"); // Opção para criar um board
            System.out.println("2 - Selecionar um board existente"); // Opção para selecionar um board já existente
            System.out.println("3 - Excluir um board"); // Opção para excluir um board
            System.out.println("4 - Sair"); // Opção para sair da aplicação

            option = scanner.nextInt(); // Lê a opção digitada pelo usuário
            switch (option) { // Estrutura condicional para tratar cada opção do menu
                case 1 -> createBoard();    // Criação de board com colunas
                case 2 -> selectBoard();    // Seleciona board pelo ID e abre o submenu
                case 3 -> deleteBoard();    // Exclui um board
                case 4 -> System.exit(0);   // Encerra a aplicação
                default -> System.out.println("Opção inválida, informe uma opção do menu"); // Mensagem de erro para opção inválida
            }
        }
    }

    private void createBoard() throws SQLException { // Método para criar um novo board
        var entity = new BoardEntity(); // Cria uma nova instância de BoardEntity

        System.out.println("Informe o nome do seu board"); // Solicita o nome do board
        entity.setName(scanner.next()); // Lê o nome e define no board

        System.out.println("Seu board terá colunas além das 3 padrões? Se sim informe quantas, senão digite '0'"); // Pergunta se terá colunas extras
        int additionalColumns = scanner.nextInt(); // Lê a quantidade de colunas adicionais

        List<BoardColumnEntity> columns = new ArrayList<>(); // Lista para armazenar as colunas do board

        // Cria a coluna inicial
        System.out.println("Informe o nome da coluna inicial do board"); // Solicita o nome da coluna inicial
        String initialColumnName = scanner.next(); // Lê o nome da coluna inicial
        var initialColumn = createColumn(initialColumnName, INITIAL, 0); // Cria a coluna inicial com ordem 0 e tipo INITIAL
        columns.add(initialColumn); // Adiciona a coluna à lista

        // Cria colunas intermediárias (tipo PENDING)
        for (int i = 0; i < additionalColumns; i++) { // Laço para criar colunas PENDING
            System.out.println("Informe o nome da coluna de tarefa pendente do board"); // Solicita o nome da coluna pendente
            String pendingColumnName = scanner.next(); // Lê o nome da coluna pendente
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1); // Cria a coluna pendente com ordem sequencial
            columns.add(pendingColumn); // Adiciona à lista
        }

        // Cria a coluna final
        System.out.println("Informe o nome da coluna final"); // Solicita o nome da coluna final
        String finalColumnName = scanner.next(); // Lê o nome da coluna final
        var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1); // Cria a coluna final com ordem apropriada
        columns.add(finalColumn); // Adiciona à lista

        // Cria a coluna de cancelamento
        System.out.println("Informe o nome da coluna de cancelamento do board"); // Solicita o nome da coluna de cancelamento
        String cancelColumnName = scanner.next(); // Lê o nome da coluna de cancelamento
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2); // Cria a coluna de cancelamento com ordem apropriada
        columns.add(cancelColumn); // Adiciona à lista

        entity.setBoardColumns(columns); // Define as colunas criadas no board

        // Insere o board no banco com suas colunas
        try (var connection = getConnection()) { // Abre conexão com o banco
            var service = new BoardService(connection); // Cria o serviço para inserir o board
            service.insert(entity); // Insere o board no banco
            System.out.println("Board criado com sucesso!"); // Mensagem de sucesso
        }
    }

    private void selectBoard() throws SQLException { // Método para selecionar um board existente
        System.out.println("Informe o id do board que deseja selecionar"); // Solicita o ID do board
        var id = scanner.nextLong(); // Lê o ID digitado
        try(var connection = getConnection()){ // Abre conexão com o banco
            var queryService = new BoardQueryService(connection); // Cria serviço para buscar o board
            var optional = queryService.findById(id); // Busca o board pelo ID
            optional.ifPresentOrElse( // Se encontrar o board
                    b -> new BoardMenu(b).execute(),// Abre o menu específico do board
                    () -> System.out.printf("Não foi encontrado um board com id %s\n", id) // Caso não encontre, exibe mensagem
            );
        }
    }

    private void deleteBoard() throws SQLException { // Método para excluir um board
        System.out.println("Informe o id do board que será excluido"); // Solicita o ID do board
        var id = scanner.nextLong(); // Lê o ID informado
        try(var connection = getConnection()){ // Abre conexão com o banco
            var service = new BoardService(connection); // Cria serviço para exclusão
            if (service.delete(id)){ // Tenta deletar o board
                System.out.printf("O board %s foi excluido\n", id); // Mensagem de sucesso
            } else {
                System.out.printf("Não foi encontrado um board com id %s\n", id); // Mensagem se não encontrou o board
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){ // Método para criar uma nova coluna
        var boardColumn = new BoardColumnEntity(); // Cria nova instância da coluna
        boardColumn.setName(name); // Define o nome da coluna
        boardColumn.setKind(kind); // Define o tipo da coluna
        boardColumn.setOrder(order); // Define a ordem da coluna
        return boardColumn; // Retorna a coluna criada
    }
}