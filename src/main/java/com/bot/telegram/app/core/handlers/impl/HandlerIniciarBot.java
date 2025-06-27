package com.bot.telegram.app.core.handlers.impl;

import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.handlers.Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlerIniciarBot implements Handler {

    private final HandleService service;


    @Override
    public EtapasMenuPrincipal getEtapa() {
        return EtapasMenuPrincipal.INICIAR_BOT;
    }

    @Override
    public void handleMessage(String mensagem)  {
        service.iniciarBot();
    }

    @Override
    public void setarChatId(Long chatId) {
        service.setChatId(chatId);
    }


}
