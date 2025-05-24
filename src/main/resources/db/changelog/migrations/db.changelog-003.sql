--liquibase formatted sql
--changeset jhonata:003
--comment: cards table create

CREATE TABLE CARDS( -- Criação da tabela CARDS
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Coluna 'id': chave primária gerada automaticamente
    title VARCHAR(255) NOT NULL, -- Coluna 'title': título do card, texto de até 255 caracteres, obrigatório
    description VARCHAR(255) NOT NULL, -- Coluna 'description': descrição do card, até 255 caracteres, obrigatório
    board_column_id BIGINT NOT NULL, -- Coluna 'board_column_id': chave estrangeira para BOARDS_COLUMNS, obrigatório

    -- Define a constraint de chave estrangeira ligando 'board_column_id' à tabela BOARDS_COLUMNS
    -- 'ON DELETE CASCADE' garante que, ao excluir uma coluna de quadro, os cards associados também sejam excluídos
    CONSTRAINT boards_columns__cards_fk FOREIGN KEY (board_column_id) REFERENCES BOARDS_COLUMNS(id) ON DELETE CASCADE
) ENGINE=InnoDB; -- Define o mecanismo de armazenamento como InnoDB


--rollback DROP TABLE CARDS