package com.bot.telegram.app.core.dto.response;


public record DespesaResponse(
        String cartao,
        String dataFechamento,
        String dataPrimeiroVencimento,
        Integer parcelas
) {
}
