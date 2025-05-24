package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.BoardDetailsDTO;
import org.example.persistence.dao.BoardColumnDAO;
import org.example.persistence.dao.BoardDAO;
import org.example.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        // Cria uma instância do DAO de Board (tabela principal)

        var boardColumnDAO = new BoardColumnDAO(connection);
        // Cria uma instância do DAO de BoardColumn (colunas associadas ao board)

        var optional = dao.findById(id);
        // Busca a entidade Board pelo ID (pode ou não existir)

        if (optional.isPresent()){
            var entity = optional.get();
            // Se existir, recupera o objeto BoardEntity

            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            // Preenche o BoardEntity com suas colunas (relacionadas ao board)

            return Optional.of(entity);
            // Retorna o BoardEntity com os dados completos
        }

        return Optional.empty();
        // Se não encontrar o board, retorna vazio
    }


    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        // DAO para acessar os dados do Board

        var boardColumnDAO = new BoardColumnDAO(connection);
        // DAO para acessar colunas com detalhes adicionais

        var optional = dao.findById(id);
        // Busca a entidade Board pelo ID

        if (optional.isPresent()){
            var entity = optional.get();
            // Se encontrar, pega o objeto

            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            // Busca colunas com quantidade de cards associadas (usando DTO)

            var dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
            // Monta um DTO de detalhes para o board

            return Optional.of(dto);
            // Retorna o DTO preenchido
        }

        return Optional.empty();
        // Se não encontrar, retorna vazio
    }


}