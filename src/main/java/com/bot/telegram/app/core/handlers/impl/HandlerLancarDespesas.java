package com.bot.telegram.app.core.handlers.impl;

import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.enums.FluxoLancDespesas;
import com.bot.telegram.app.core.fluxo.FluxoLancarNovaDespesa;
import com.bot.telegram.app.core.handlers.Handler;
import com.bot.telegram.app.core.service.HandleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class HandlerLancarDespesas implements Handler {

    private final HandleService service;
    private final FluxoLancarNovaDespesa fluxoDespesa;


    private boolean lancarRapido = false;

    @Override
    public EtapasMenuPrincipal getEtapa() {
        return EtapasMenuPrincipal.HANDLER_LANCAR_DESPESA;
    }

    @Override
    public void handleMessage(String message) {
        service.setMensagemAtual(message);
        FluxoLancDespesas supetapa = null;
        if (service.getLancDespesasMap().containsKey(service.getChatId())){
             supetapa = service.getLancDespesasMap().get(service.getChatId());
        }

        if ((Objects.nonNull(supetapa) && supetapa.equals(FluxoLancDespesas.LANCAMENTO_RAPIDO)) || lancarRapido) {
            lancarRapido = true;
            fluxoDespesa.cadastroRapido(service);
        } else {
            fluxoDespesa.cadastroDespesaService(service);
        }



    }

    @Override
    public void setarChatId(Long chatId) {
        service.setChatId(chatId);
    }
}
