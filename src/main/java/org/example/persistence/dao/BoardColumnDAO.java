package org.example.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import org.example.dto.BoardColumnDTO;
import org.example.persistence.entity.BoardColumnEntity;
import org.example.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static org.example.persistence.entity.BoardColumnKindEnum.findByName;

@AllArgsConstructor
public class BoardColumnDAO {

    private final Connection connection; // conexão com o banco de dados passada no construtor

    // Método para inserir uma nova coluna no quadro (board column)
    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        // SQL para inserir na tabela BOARDS_COLUMNS
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            // Setar os parâmetros do PreparedStatement a partir da entidade
            statement.setString(i++, entity.getName());             // nome da coluna
            statement.setInt(i++, entity.getOrder());               // ordem da coluna
            statement.setString(i++, entity.getKind().name());      // tipo da coluna (enum convertido para String)
            statement.setLong(i, entity.getBoard().getId());        // id do board pai
            statement.executeUpdate();                              // executar o insert

            // Pegar o id gerado pelo banco (auto_increment) e setar na entidade
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
            return entity; // retorna a entidade com o id atualizado
        }
    }

    // Buscar todas as colunas de um board pelo seu id
    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        // SQL para selecionar as colunas do board ordenadas pela ordem
        var sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);   // seta o parâmetro do board_id
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));                         // id da coluna
                entity.setName(resultSet.getString("name"));                   // nome
                entity.setOrder(resultSet.getInt("order"));                    // ordem
                entity.setKind(findByName(resultSet.getString("kind")));       // converter string para enum
                entities.add(entity);                                           // adiciona à lista
            }
            return entities; // retorna lista de colunas
        }
    }

    // Buscar colunas do board com detalhes (quantidade de cards)
    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        // SQL que seleciona colunas junto com a quantidade de cards em cada coluna
        var sql =
                """
                SELECT bc.id,
                       bc.name,
                       bc.kind,
                       (SELECT COUNT(c.id)
                              FROM CARDS c
                             WHERE c.board_column_id = bc.id) cards_amount
                  FROM BOARDS_COLUMNS bc
                 WHERE board_id = ?
                 ORDER BY `order`;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),                     // id da coluna
                        resultSet.getString("bc.name"),                  // nome
                        findByName(resultSet.getString("bc.kind")),      // tipo convertido para enum
                        resultSet.getInt("cards_amount")                  // número de cards na coluna
                );
                dtos.add(dto);  // adiciona DTO na lista
            }
            return dtos; // retorna lista com detalhes
        }
    }

    // Buscar uma coluna por seu id com os cards relacionados
    public Optional<BoardColumnEntity> findById(final Long boardId) throws SQLException {
        var sql =
                """
                SELECT bc.name,
                       bc.kind,
                       c.id,
                       c.title,
                       c.description
                  FROM BOARDS_COLUMNS bc
                  LEFT JOIN CARDS c
                    ON c.board_column_id = bc.id
                 WHERE bc.id = ?;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();

            if (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("bc.name"));                  // nome da coluna
                entity.setKind(findByName(resultSet.getString("bc.kind")));      // tipo convertido para enum

                // Itera os cards que pertencem a essa coluna
                do {
                    var card = new CardEntity();
                    // Se não houver título, significa que não há card nesta linha, então para o loop
                    if (isNull(resultSet.getString("c.title"))) {
                        break;
                    }
                    card.setId(resultSet.getLong("c.id"));                       // id do card
                    card.setTitle(resultSet.getString("c.title"));               // título do card
                    card.setDescription(resultSet.getString("c.description"));   // descrição do card
                    entity.getCards().add(card);                                 // adiciona o card à coluna
                } while (resultSet.next());
                return Optional.of(entity); // retorna a coluna com seus cards
            }
            return Optional.empty(); // se não encontrou a coluna, retorna vazio
        }
    }
}

