package org.example.ui;

import lombok.AllArgsConstructor;
import org.example.dto.BoardColumnInfoDTO;
import org.example.persistence.entity.BoardColumnEntity;
import org.example.persistence.entity.BoardEntity;
import org.example.persistence.entity.CardEntity;
import org.example.service.BoardColumnQueryService;
import org.example.service.BoardQueryService;
import org.example.service.CardQueryService;
import org.example.service.CardService;

import java.sql.SQLException;
import java.util.Scanner;

import static org.example.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    // Scanner para ler entradas do usuário, separando por nova linha
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    // Representa o board atual que o usuário está interagindo
    private final BoardEntity entity;

    // Método principal que exibe o menu e trata as opções
    public void execute() {
        try {
            // Exibe mensagem de boas-vindas com o ID do board
            System.out.printf("Bem vindo ao board %s, selecione a operação desejada\n", entity.getId());
            var option = -1;
            // Loop principal do menu até a opção 9 (voltar)
            while (option != 9) {
                // Exibe opções do menu
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Ver board");
                System.out.println("7 - Ver coluna com cards");
                System.out.println("8 - Ver card");
                System.out.println("9 - Voltar para o menu anterior um card");
                System.out.println("10 - Sair");
                option = scanner.nextInt(); // Lê a opção do usuário
                switch (option) {
                    case 1 -> createCard(); // Cria um novo card
                    case 2 -> moveCardToNextColumn(); // Move card para próxima coluna
                    case 3 -> blockCard(); // Bloqueia um card
                    case 4 -> unblockCard(); // Desbloqueia um card
                    case 5 -> cancelCard(); // Cancela um card
                    case 6 -> showBoard(); // Exibe detalhes do board
                    case 7 -> showColumn(); // Exibe uma coluna com seus cards
                    case 8 -> showCard(); // Exibe os dados de um card específico
                    case 9 -> System.out.println("Voltando para o menu anterior"); // Sai do menu atual
                    case 10 -> System.exit(0); // Encerra a aplicação
                    default -> System.out.println("Opção inválida, informe uma opção do menu"); // Opção inválida
                }
            }
        } catch (SQLException ex) {
            // Em caso de erro no banco, imprime a exceção e encerra
            ex.printStackTrace();
            System.exit(0);
        }
    }

    // Cria um novo card na coluna inicial do board
    private void createCard() throws SQLException {
        var card = new CardEntity(); // Cria entidade de card
        System.out.println("Informe o título do card");
        card.setTitle(scanner.next()); // Lê o título do card
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.next()); // Lê a descrição
        card.setBoardColumn(entity.getInitialColumn()); // Associa à coluna inicial do board
        try (var connection = getConnection()) {
            new CardService(connection).create(card); // Salva o card no banco
        }
    }

    // Move um card para a próxima coluna
    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna");
        var cardId = scanner.nextLong(); // Lê o ID do card

        // Cria uma lista com informações básicas das colunas do board
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();

        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo); // Move to card
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage()); // Mostra erro, se houver
        }
    }

    // Bloqueia um card, exigindo motivo
    private void blockCard() throws SQLException {
        System.out.println("Informe o id do card que será bloqueado");
        var cardId = scanner.nextLong(); // Lê ID do card
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next(); // Lê o motivo

        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();

        try (var connection = getConnection()) {
            new CardService(connection).block(cardId, reason, boardColumnsInfo); // Bloqueia o card
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage()); // Mostra erro
        }
    }

    // Desbloqueia um card
    private void unblockCard() throws SQLException {
        System.out.println("Informe o id do card que será desbloqueado");
        var cardId = scanner.nextLong(); // Lê ID do card
        System.out.println("Informe o motivo do desbloqueio do card");
        var reason = scanner.next(); // Lê o motivo
        try (var connection = getConnection()) {
            new CardService(connection).unblock(cardId, reason); // Desbloqueia o card
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage()); // Mostra erro
        }
    }

    // Cancela um card, movendo para a coluna de cancelamento
    private void cancelCard() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong(); // Lê ID do card
        var cancelColumn = entity.getCancelColumn(); // Obtém a coluna de cancelamento

        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();

        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo); // Cancela o card
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage()); // Mostra erro
        }
    }

    // Exibe resumo do board com colunas e quantidade de cards
    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    // Exibe uma coluna do board com seus cards
    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        // Enquanto não escolher um ID válido
        while (!columnsIds.contains(selectedColumnId)) {
            System.out.printf("Escolha uma coluna do board %s pelo id\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong(); // Lê ID da coluna
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    // Exibe os detalhes de um card específico
    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong(); // Lê o ID do card
        try (var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.blocked() ?
                                        "Está bloqueado. Motivo: " + c.blockReason() :
                                        "Não está bloqueado");
                                System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
        }
    }
}
