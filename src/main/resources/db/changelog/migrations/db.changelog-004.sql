--liquibase formatted sql
--changeset jhonata:004
--comment: blocks table create

CREATE TABLE BLOCKS( -- Criação da tabela BLOCKS
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Coluna 'id': chave primária com incremento automático

    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Coluna 'blocked_at': armazena a data/hora do bloqueio, valor padrão é o momento atual

    block_reason VARCHAR(255) NOT NULL,
    -- Coluna 'block_reason': motivo do bloqueio, obrigatório, até 255 caracteres

    unblocked_at TIMESTAMP NULL,
    -- Coluna 'unblocked_at': data/hora em que o bloqueio foi removido, pode ser nulo

    unblock_reason VARCHAR(255) NOT NULL,
    -- Coluna 'unblock_reason': motivo da liberação do bloqueio, obrigatório

    card_id BIGINT NOT NULL,
    -- Coluna 'card_id': chave estrangeira que referencia um card, obrigatório

    CONSTRAINT cards__blocks_fk FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
    -- Define a chave estrangeira ligando 'card_id' à tabela CARDS
    -- 'ON DELETE CASCADE' garante que, ao excluir um card, o bloqueio associado também seja removido
) ENGINE=InnoDB; -- Define o mecanismo de armazenamento como InnoDB (suporta transações e integridade referencial)


--rollback DROP TABLE BLOCKS