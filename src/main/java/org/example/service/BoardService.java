package org.example.service;

import lombok.AllArgsConstructor;
import org.example.persistence.dao.BoardColumnDAO;
import org.example.persistence.dao.BoardDAO;
import org.example.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var dao = new BoardDAO(connection);
        // Cria instância do DAO de Board para manipular dados da tabela BOARDS

        var boardColumnDAO = new BoardColumnDAO(connection);
        // Cria instância do DAO de BoardColumn para manipular as colunas do board

        try {
            dao.insert(entity);
            // Insere o board no banco (gera ID)

            var columns = entity.getBoardColumns().stream().map(c -> {
                c.setBoard(entity);
                return c;
            }).toList();
            // Para cada coluna do board, define o board pai (associação bidirecional)

            for (var column : columns){
                boardColumnDAO.insert(column);
            }
            // Insere cada coluna associada ao board no banco

            connection.commit();
            // Se tudo deu certo, confirma (commita) a transação

        } catch (SQLException e) {
            connection.rollback();
            // Em caso de erro, desfaz (rollback) as alterações

            throw e;
            // Lança novamente a exceção para ser tratada externamente
        }

        return entity;
        // Retorna o objeto board com colunas já salvas e ID preenchido
    }


    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        // Cria instância do DAO de Board

        try {
            if (!dao.exists(id)) {
                return false;
            }
            // Se o board não existe, retorna false

            dao.delete(id);
            // Deleta o board do banco

            connection.commit();
            // Confirma a transação

            return true;
            // Deleção feita com sucesso

        } catch (SQLException e) {
            connection.rollback();
            // Em caso de erro, desfaz a transação

            throw e;
            // Lança novamente a exceção
        }
    }

}
