package com.bot.telegram.app.core.dto.request;

import com.bot.telegram.app.domain.model.enums.TipoMovimento;

public record CategoriaRequest(
        String nome,
        Boolean fixa,
        TipoMovimento tipo,
        Integer diaVencimento

) {
}
