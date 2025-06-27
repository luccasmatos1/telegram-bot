package com.bot.telegram.app.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "despesas")
@Data
@NoArgsConstructor
public class Despesas {

    @Id
    private Long id;
    private String descricao;
    private LocalDate dataCompra;
    private LocalDate dataPagamento;
    private Categoria  categoria;
    private Cartoes cartoes;
    private Double valor;
    private Integer parcela;



    @Override
    public String toString() {
        return "Despesas{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", data=" + dataPagamento +
                ", tipoDespesa=" + cartoes +
                ", valor=" + valor +
                '}';
    }
}
