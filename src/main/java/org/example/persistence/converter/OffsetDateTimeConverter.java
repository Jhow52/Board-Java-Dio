package org.example.persistence.converter;

import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE) // Gera construtor privado sem argumentos para evitar instanciação da classe
public final class OffsetDateTimeConverter { // Classe final, não pode ser estendida

    // Converte um objeto java.sql.Timestamp para java.time.OffsetDateTime (com fuso horário UTC)
    public static OffsetDateTime toOffsetDateTime(final Timestamp value){
        // Se value não for nulo, converte o Timestamp para OffsetDateTime em UTC, senão retorna null
        return nonNull(value) ? OffsetDateTime.ofInstant(value.toInstant(), UTC) : null;
    }

    // Converte um OffsetDateTime para Timestamp (removendo fuso horário)
    public static Timestamp toTimestamp(final OffsetDateTime value){
        // Se value não for nulo, converte para Timestamp no fuso UTC, senão retorna null
        return nonNull(value) ? Timestamp.valueOf(value.atZoneSameInstant(UTC).toLocalDateTime()) : null;
    }
}
