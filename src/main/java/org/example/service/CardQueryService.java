package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.CardDetailsDTO;
import org.example.persistence.dao.CardDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {

    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        // Cria uma instância de CardDAO, injetando a conexão com o banco de dados.
        // Isso permitirá executar operações de consulta relacionadas à entidade Card.
        var dao = new CardDAO(connection);

        // Chama o método findById do DAO, que retorna um Optional com os detalhes do card,
        // incluindo informações como título, descrição, status de bloqueio, e nome da coluna.
        // O retorno pode estar vazio se o card com o ID fornecido não existir no banco.
        return dao.findById(id);
    }


}
