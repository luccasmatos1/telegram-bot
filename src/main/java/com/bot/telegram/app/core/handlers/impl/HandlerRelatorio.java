package com.bot.telegram.app.core.handlers.impl;

import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.handlers.Handler;
import com.bot.telegram.app.core.reports.RelatorioService;
import com.bot.telegram.app.core.service.HandleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlerRelatorio implements Handler {

    private final RelatorioService relatorioService;
    private final HandleService handleService;

    @Override
    public EtapasMenuPrincipal getEtapa() {
        return EtapasMenuPrincipal.RELATORIO;
    }

    @Override
    public void handleMessage(String message) {
        System.out.println("Relatorio referencia "  + message);

        handleService.proximaMensagem(relatorioService.relatorioGeral(message));
        handleService.limparFluxos(handleService.getChatId());
    }

    @Override
    public void setarChatId(Long chatId) {

    }
}
