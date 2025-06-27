package com.bot.telegram.app.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document( collection = "receitas")
@Data
public class Receitas {

    @Id
    private Long id;
    private Categoria categoria;
    private String descricao;
    private LocalDate dataLancamento;
    private Double valor;
}
