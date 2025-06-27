package com.bot.telegram.app.core.handlers;

import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;

public interface Handler {

    EtapasMenuPrincipal getEtapa ();
    void handleMessage(String message);
    void setarChatId(Long chatId);
}
