package org.example.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardColumnEntity {

    private Long id;              // identificador único da coluna do quadro (PK)
    private String name;          // nome da coluna (ex: "To Do", "Doing", "Done")
    private int order;            // ordem da coluna dentro do quadro (posição)
    private BoardColumnKindEnum kind; // tipo da coluna, usando enum (ex: INITIAL, FINAL, etc)
    private BoardEntity board = new BoardEntity(); // referência ao quadro (board) ao qual essa coluna pertence

    @ToString.Exclude // exclui essa lista do método toString (evita recursão ou poluição da saída)
    @EqualsAndHashCode.Exclude // exclui do equals e hashCode para evitar loops ou comparações pesadas
    private List<CardEntity> cards = new ArrayList<>(); // lista de cards que pertencem a essa coluna
}