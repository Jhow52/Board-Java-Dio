package org.example.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data // Lombok: gera getters, setters, toString, equals, hashCode automaticamente
public class BlockEntity {

    private Long id;                   // identificador único do bloqueio (PK)
    private OffsetDateTime blockedAt;  // data e hora em que o bloqueio foi aplicado
    private String blockReason;        // motivo pelo qual o bloqueio foi realizado
    private OffsetDateTime unblockedAt; // data e hora em que o bloqueio foi removido (pode ser null)
    private String unblockReason;      // motivo pelo qual o bloqueio foi removido (pode ser null)
}