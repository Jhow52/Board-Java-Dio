package org.example.service;

import lombok.AllArgsConstructor;
import org.example.persistence.dao.BoardColumnDAO;
import org.example.persistence.entity.BoardColumnEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnQueryService {

    private final Connection connection;
    // Conexão com o banco, injetada via construtor (não mostrada aqui, mas deve existir)

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        // Cria uma instância do DAO de BoardColumn usando a conexão fornecida
        var dao = new BoardColumnDAO(connection);
        // Usa o DAO para buscar a entidade BoardColumn pelo id e retorna o resultado
        return dao.findById(id);
    }
}

