package org.example.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static org.example.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor // Gera automaticamente um construtor com todos os atributos (nesse caso, Connection)
public class BlockDAO {

    private final Connection connection; // A conexão com o banco, injetada via construtor

    // Método para bloquear um cartão, registrando o motivo e a hora do bloqueio
    public void block(final String reason, final Long cardId) throws SQLException {
        // SQL para inserir um novo registro na tabela BLOCKS
        var sql = "INSERT INTO BLOCKS (blocked_at, block_reason, card_id) VALUES (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            // Define o primeiro parâmetro com a data/hora atual convertida para Timestamp
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            // Define o motivo do bloqueio (string)
            statement.setString(i++, reason);
            // Define o id do cartão bloqueado
            statement.setLong(i, cardId);
            // Executa o insert
            statement.executeUpdate();
        }
    }

    // Método para desbloquear um cartão, registrando o motivo e a hora do desbloqueio
    public void unblock(final String reason, final Long cardId) throws SQLException {
        // SQL para atualizar o registro na tabela BLOCKS, apenas desbloqueios que ainda não tem motivo de desbloqueio
        var sql = "UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            // Define o timestamp atual para a coluna unblocked_at
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            // Define o motivo do desbloqueio
            statement.setString(i++, reason);
            // Define o id do cartão para o qual será feito o desbloqueio
            statement.setLong(i, cardId);
            // Executa o update
            statement.executeUpdate();
        }
    }
}