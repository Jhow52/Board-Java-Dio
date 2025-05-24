package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.BoardColumnInfoDTO;
import org.example.exception.CardBlockedException;
import org.example.exception.CardFinishedException;
import org.example.exception.EntityNotFoundException;
import org.example.persistence.dao.BlockDAO;
import org.example.persistence.dao.CardDAO;
import org.example.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.example.persistence.entity.BoardColumnKindEnum.CANCEL;
import static org.example.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    // Método para criar um novo Card no banco
    public CardEntity create(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection); // DAO para interagir com a tabela CARDS
            dao.insert(entity); // Insere o card no banco
            connection.commit(); // Confirma a transação
            return entity; // Retorna a entidade com ID preenchido
        } catch (SQLException ex) {
            connection.rollback(); // Reverte a transação em caso de erro
            throw ex; // Relança a exceção
        }
    }

    // Move o card para a próxima coluna no fluxo do quadro
    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId); // Busca detalhes do card pelo ID
            var dto = optional.orElseThrow(() ->
                    new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );

            if (dto.blocked()) { // Verifica se o card está bloqueado
                var message = "O card %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId);
                throw new CardBlockedException(message);
            }

            // Obtém a coluna atual do card
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));

            // Verifica se o card já está na coluna FINAL
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado");
            }

            // Obtém a próxima coluna no fluxo
            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card está cancelado"));

            dao.moveToColumn(nextColumn.id(), cardId); // Move o card
            connection.commit(); // Confirma a transação
        } catch (SQLException ex) {
            connection.rollback(); // Reverte em caso de erro
            throw ex;
        }
    }

    // Cancela o card, movendo-o para a coluna de cancelamento
    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() ->
                    new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );

            if (dto.blocked()) {
                var message = "O card %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId);
                throw new CardBlockedException(message);
            }

            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));

            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado");
            }

            // Garante que não seja a próxima coluna (verificação de fluxo válida)
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card está cancelado"));

            dao.moveToColumn(cancelColumnId, cardId); // Move o card para a coluna de cancelamento
            connection.commit(); // Confirma a transação
        } catch (SQLException ex) {
            connection.rollback(); // Reverte em caso de erro
            throw ex;
        }
    }

    // Bloqueia um card por um motivo
    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(() ->
                    new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
            );

            if (dto.blocked()) {
                var message = "O card %s já está bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }

            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow();

            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
                var message = "O card está em uma coluna do tipo %s e não pode ser bloqueado"
                        .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }

            var blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, id); // Realiza o bloqueio
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    // Desbloqueia um card, fornecendo um motivo
    public void unblock(final Long id, final String reason) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(() ->
                    new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
            );

            if (!dto.blocked()) {
                var message = "O card %s não está bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }

            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, id); // Realiza o desbloqueio
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

}

