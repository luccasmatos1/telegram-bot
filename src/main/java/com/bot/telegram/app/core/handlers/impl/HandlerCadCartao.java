package com.bot.telegram.app.core.handlers.impl;

import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.enums.FluxoCadCartao;
import com.bot.telegram.app.core.fluxo.FluxoCadastroCartao;
import com.bot.telegram.app.core.handlers.Handler;
import com.bot.telegram.app.domain.model.Cartoes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlerCadCartao implements Handler {

    private final HandleService service;
    private final FluxoCadastroCartao fluxoCartao;

    @Override
    public EtapasMenuPrincipal getEtapa() {
        return EtapasMenuPrincipal.NOVO_CADASTRO_CARTOES;
    }

    @Override
    public void handleMessage(String message) {
        service.setMensagemAtual(message);
        FluxoCadCartao fluxoCadCartao = service.getSubEtapaMap().getOrDefault(service.getChatId(), FluxoCadCartao.CAD_CARTAO_INFORMAR_NOME);
        Cartoes cartoes = service.getCartoesMap().get(service.getChatId());
        fluxoCartao.cadastroCartaoService(service);
    }

    @Override
    public void setarChatId(Long chatId) {
        service.setChatId(chatId);
    }


}
