package org.example.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnKindEnum {

    INITIAL, // representa o tipo "inicial" da coluna
    FINAL,   // representa o tipo "final" da coluna
    CANCEL,  // representa o tipo "cancelado" da coluna
    PENDING; // representa o tipo "pendente" da coluna

    // Método estático para buscar um valor da enum a partir do nome (string)
    public static BoardColumnKindEnum findByName(final String name){
        return Stream.of(BoardColumnKindEnum.values()) // cria um stream dos valores da enum
                .filter(b -> b.name().equals(name))   // filtra buscando o que tem o mesmo nome (case-sensitive)
                .findFirst()                          // retorna o primeiro valor que encontrar
                .orElseThrow();                      // lança exceção se não encontrar nenhum
    }
}