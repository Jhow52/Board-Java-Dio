package org.example.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import org.example.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {

    private Connection connection; // conexão com o banco de dados, passada no construtor ou setada externamente

    // Método para inserir um novo board no banco de dados
    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        // SQL para inserir um registro na tabela BOARDS, apenas com o nome
        var sql = "INSERT INTO BOARDS (name) values (?);";
        try(var statement = connection.prepareStatement(sql)){
            statement.setString(1, entity.getName()); // define o parâmetro do nome no SQL
            statement.executeUpdate();                // executa o insert

            // Se a implementação do PreparedStatement for StatementImpl,
            // pegamos o último ID auto-gerado e setamos na entidade
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity; // retorna a entidade já com o ID setado
    }

    // Método para deletar um board pelo seu ID
    public void delete(final Long id) throws SQLException {
        // SQL para deletar o registro da tabela BOARDS onde o id for igual ao parâmetro
        var sql = "DELETE FROM BOARDS WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);    // seta o parâmetro do ID no SQL
            statement.executeUpdate();   // executa o delete
        }
    }

    // Método para buscar um board pelo ID, retorna Optional porque pode não existir
    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT id, name FROM BOARDS WHERE id = ?;"; // consulta SQL
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);   // seta o parâmetro do ID
            statement.executeQuery();   // executa a consulta
            var resultSet = statement.getResultSet(); // pega os resultados

            // Se encontrou alguma linha, cria e popula a entidade BoardEntity
            if (resultSet.next()){
                var entity = new BoardEntity();
                entity.setId(resultSet.getLong("id"));         // seta o id
                entity.setName(resultSet.getString("name"));   // seta o nome
                return Optional.of(entity);                      // retorna a entidade dentro do Optional
            }
            return Optional.empty(); // retorna vazio se não encontrou nada
        }
    }

    // Método para verificar se um board existe, usando apenas o ID
    public boolean exists(final Long id) throws SQLException {
        var sql = "SELECT 1 FROM BOARDS WHERE id = ?;"; // consulta que retorna 1 se existir
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);       // seta o ID
            statement.executeQuery();       // executa a consulta
            return statement.getResultSet().next(); // retorna true se encontrou algo, false caso contrário
        }
    }
}

