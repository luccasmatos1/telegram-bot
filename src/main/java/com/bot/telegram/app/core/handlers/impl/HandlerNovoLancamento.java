package com.bot.telegram.app.core.handlers.impl;

import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.handlers.Handler;
import com.bot.telegram.app.core.service.HandleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlerNovoLancamento implements Handler {

    private final HandleService service;

    @Override
    public EtapasMenuPrincipal getEtapa() {
        return EtapasMenuPrincipal.HANDLER_NOVO_LANCAMENTO;
    }

    @Override
    public void handleMessage(String message) {
        service.setMensagemAtual(message);
        service.novoLancamento();
    }

    @Override
    public void setarChatId(Long chatId) {
        service.setChatId(chatId);
    }
}
