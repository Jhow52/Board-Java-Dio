package org.example.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.example.persistence.entity.BoardColumnKindEnum.CANCEL;
import static org.example.persistence.entity.BoardColumnKindEnum.INITIAL;

@Data
public class BoardEntity {

    private Long id; // Identificador único do quadro (board)
    private String name; // Nome do quadro

    @ToString.Exclude // Exclui do toString para evitar recursão infinita (por causa da lista)
    @EqualsAndHashCode.Exclude // Exclui da comparação e hashCode para evitar problemas de recursão
    private List<BoardColumnEntity> boardColumns = new ArrayList<>(); // Lista das colunas associadas ao quadro

    // Retorna a coluna do tipo INITIAL (inicial)
    public BoardColumnEntity getInitialColumn(){
        // Usa método privado para filtrar a coluna que seja do tipo INITIAL
        return getFilteredColumn(bc -> bc.getKind().equals(INITIAL));
    }

    // Retorna a coluna do tipo CANCEL (cancelada)
    public BoardColumnEntity getCancelColumn(){
        // Usa método privado para filtrar a coluna que seja do tipo CANCEL
        return getFilteredColumn(bc -> bc.getKind().equals(CANCEL));
    }

    // Método privado genérico que recebe um filtro (Predicate) para encontrar a primeira coluna que bate com o filtro
    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter){
        return boardColumns.stream() // cria stream da lista de colunas
                .filter(filter)       // aplica o filtro passado por parâmetro
                .findFirst()          // busca o primeiro elemento que satisfaz o filtro
                .orElseThrow();       // se não encontrar, lança exceção (NoSuchElementException)
    }
}