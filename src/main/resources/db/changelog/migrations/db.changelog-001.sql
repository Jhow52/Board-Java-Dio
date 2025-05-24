--liquibase formatted sql
--changeset jhonata:001
--comment: boards table create

CREATE TABLE BOARDS( -- Criação da tabela BOARDS
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Coluna 'id': tipo BIGINT, valor gerado automaticamente e chave primária
    name VARCHAR(255) NOT NULL -- Coluna 'name': texto de até 255 caracteres, obrigatório (NOT NULL)
) ENGINE=InnoDB; -- Define o mecanismo de armazenamento como InnoDB (suporta integridade referencial, transações etc.)


--rollback DROP TABLE BOARDS