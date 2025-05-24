--liquibase formatted sql
--changeset jhonata:005
--comment: set unblock_reason nullable

ALTER TABLE BLOCKS MODIFY COLUMN unblock_reason VARCHAR(255) NULL;
-- Altera a tabela BLOCKS para permitir que a coluna 'unblock_reason' aceite valores nulos
-- Antes, essa coluna era NOT NULL (obrigatória)
-- Agora, ela se torna opcional, permitindo registros sem motivo de desbloqueio (útil quando o cartão ainda está bloqueado)

--rollback ALTER TABLE BLOCKS MODIFY COLUMN unblock_reason VARCHAR(255) NOT NULL;