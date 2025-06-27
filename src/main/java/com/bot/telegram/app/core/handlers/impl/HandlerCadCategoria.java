package com.bot.telegram.app.core.handlers.impl;

import com.bot.telegram.app.core.fluxo.FluxoCadastroCategoria;
import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.handlers.Handler;
import com.bot.telegram.app.domain.model.Categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlerCadCategoria implements Handler {

    private final HandleService service;
    private final FluxoCadastroCategoria fluxoCategoria;


    @Override
    public EtapasMenuPrincipal getEtapa() {
        return EtapasMenuPrincipal.NOVO_CADASTRO_CATEGORIA;
    }

    @Override
    public void handleMessage(String message) {
        Categoria categoria = service.getCategoriaMap().get(service.getChatId());
        service.setMensagemAtual(message);
        fluxoCategoria.cadastroCategoriaService(service);

    }

    @Override
    public void setarChatId(Long chatId) {
        service.setChatId(chatId);
    }
}
