package com.bot.telegram.app.core.handlers.impl;

import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.handlers.Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlerCadastrar implements Handler {

    private final HandleService service;

    @Override
    public EtapasMenuPrincipal getEtapa() {
        return EtapasMenuPrincipal.CADASTRO;
    }

    @Override
    public void handleMessage(String message) {
        service.setMensagemAtual(message);
        service.novoCadastro(message);
    }

    @Override
    public void setarChatId(Long chatId) {

    }
}
