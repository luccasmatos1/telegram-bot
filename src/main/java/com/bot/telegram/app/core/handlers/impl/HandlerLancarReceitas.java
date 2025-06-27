package com.bot.telegram.app.core.handlers.impl;

import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.enums.FluxoCadCartao;
import com.bot.telegram.app.core.fluxo.FluxoCadastroCartao;
import com.bot.telegram.app.core.fluxo.FluxoLancarReceita;
import com.bot.telegram.app.core.handlers.Handler;
import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.domain.model.Cartoes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlerLancarReceitas implements Handler {

    private final HandleService service;
    private final FluxoLancarReceita receitas;

    @Override
    public EtapasMenuPrincipal getEtapa() {
        return EtapasMenuPrincipal.HANDLER_LANCAR_RECEITA;
    }

    @Override
    public void handleMessage(String message) {
        service.setMensagemAtual(message);

        receitas.lancarReceita(service);

    }

    @Override
    public void setarChatId(Long chatId) {
        service.setChatId(chatId);
    }


}
