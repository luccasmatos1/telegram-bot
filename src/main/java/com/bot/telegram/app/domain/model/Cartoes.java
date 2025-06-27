package com.bot.telegram.app.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cartoes")
@Data
public class Cartoes {

    @Id
    private Long id;
    private String nome;
    private Integer diaVencimento;
    private Integer diaFechamento;

}
