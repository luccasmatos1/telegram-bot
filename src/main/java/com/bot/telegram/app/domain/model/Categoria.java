package com.bot.telegram.app.domain.model;

import com.bot.telegram.app.domain.model.enums.TipoMovimento;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "categoria")
@Data
public class Categoria {

    private Long id;
    private String descricao;
    private TipoMovimento tipo;
    private Integer diaPagamento;
    private Boolean fixa;
}
