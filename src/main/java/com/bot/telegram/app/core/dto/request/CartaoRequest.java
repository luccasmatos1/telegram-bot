package com.bot.telegram.app.core.dto.request;

public record CartaoRequest(
        String nome,
        Integer diaVencimento,
        Integer diaFechamento
) {
}
