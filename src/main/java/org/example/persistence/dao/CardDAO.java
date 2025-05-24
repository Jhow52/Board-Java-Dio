package org.example.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import org.example.dto.CardDetailsDTO;
import org.example.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.example.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;

@AllArgsConstructor
public class CardDAO {

    private Connection connection; // conexão com o banco de dados

    // Método para inserir um novo card na tabela CARDS
    public CardEntity insert(final CardEntity entity) throws SQLException {
        // SQL para inserir título, descrição e coluna do board associada
        var sql = "INSERT INTO CARDS (title, description, board_column_id) values (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1; // índice para os parâmetros do PreparedStatement
            statement.setString(i ++, entity.getTitle());                  // seta o título
            statement.setString(i ++, entity.getDescription());            // seta a descrição
            statement.setLong(i, entity.getBoardColumn().getId());         // seta o id da coluna do board
            statement.executeUpdate();                                     // executa o insert

            // Se o statement for da implementação StatementImpl, recupera o ID gerado
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());                      // seta o id gerado na entidade
            }
        }
        return entity; // retorna a entidade com o id atualizado
    }

    // Método para mover um card para outra coluna (atualiza o board_column_id)
    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException{
        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i ++, columnId); // define o novo id da coluna
            statement.setLong(i, cardId);      // define o id do card a ser movido
            statement.executeUpdate();         // executa o update
        }
    }

    // Método para buscar um card pelo id, retornando um DTO com detalhes
    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql =
                """
                SELECT c.id,
                       c.title,
                       c.description,
                       b.blocked_at,
                       b.block_reason,
                       c.board_column_id,
                       bc.name,
                       (SELECT COUNT(sub_b.id)
                               FROM BLOCKS sub_b
                              WHERE sub_b.card_id = c.id) blocks_amount
                  FROM CARDS c
                  LEFT JOIN BLOCKS b
                    ON c.id = b.card_id
                   AND b.unblocked_at IS NULL -- só bloqueios ainda ativos
                 INNER JOIN BOARDS_COLUMNS bc
                    ON bc.id = c.board_column_id
                  WHERE c.id = ?;
                """;

        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);    // seta o parâmetro do id do card
            statement.executeQuery();    // executa a consulta
            var resultSet = statement.getResultSet();

            // Se o card existir
            if (resultSet.next()){
                // cria um DTO com os dados do card e seu status de bloqueio
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),                         // id do card
                        resultSet.getString("c.title"),                     // título
                        resultSet.getString("c.description"),               // descrição
                        nonNull(resultSet.getString("b.block_reason")),    // verifica se existe razão de bloqueio
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")), // data do bloqueio
                        resultSet.getString("b.block_reason"),              // razão do bloqueio
                        resultSet.getInt("blocks_amount"),                   // quantidade de bloqueios no card
                        resultSet.getLong("c.board_column_id"),              // id da coluna do board
                        resultSet.getString("bc.name")                        // nome da coluna do board
                );
                return Optional.of(dto); // retorna o DTO dentro de Optional
            }
        }
        return Optional.empty(); // retorna vazio se card não for encontrado
    }
}