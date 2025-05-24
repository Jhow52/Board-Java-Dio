--liquibase formatted sql
--changeset jhonata:002
--comment: boards_columns table create

CREATE TABLE BOARDS_COLUMNS( -- Criação da tabela BOARDS_COLUMNS
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Coluna 'id': chave primária gerada automaticamente
    name VARCHAR(255) NOT NULL, -- Coluna 'name': texto de até 255 caracteres, obrigatório
    `order` int NOT NULL, -- Coluna 'order': valor inteiro representando a ordem da coluna, obrigatório
    kind VARCHAR(7) NOT NULL, -- Coluna 'kind': tipo da coluna (ex: INITIAL, FINAL...), até 7 caracteres, obrigatório
    board_id BIGINT NOT NULL, -- Coluna 'board_id': chave estrangeira para a tabela BOARDS, obrigatório

    -- Define a constraint de chave estrangeira ligando 'board_id' à coluna 'id' da tabela BOARDS
    -- 'ON DELETE CASCADE' indica que, se um board for deletado, suas colunas também serão automaticamente deletadas
    CONSTRAINT boards__boards_columns_fk FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,

    -- Define uma restrição de unicidade: não pode haver duas colunas com o mesmo 'order' dentro do mesmo 'board'
    CONSTRAINT id_order_uk UNIQUE KEY unique_board_id_order (board_id, `order`)
) ENGINE=InnoDB; -- Define o mecanismo de armazenamento como InnoDB


--rollback DROP TABLE BOARDS_COLUMNS