package com.bot.telegram.app.core;

import com.bot.telegram.app.core.enums.EtapasMenuPrincipal;
import com.bot.telegram.app.core.handlers.Handler;
import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.domain.model.Cartoes;
import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Despesas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {


    private final HandleService service;
    private final List<Handler> handler;

    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String username;

    public TelegramBot(DefaultBotOptions options, HandleService service, List<Handler> handler) {
        super(options);
        this.service = service;
        this.service.setBot(this);
        this.handler = handler;
    }


    @Override
    public String getBotUsername() {
        return username;
    }

    public String getBotToken() {
        return token;
    }


    public void sendMessage(String message, Long chatId) {
        SendMessage newMessage = new SendMessage();
        newMessage.setChatId(chatId);
        newMessage.setText(message);

        try {
            execute(newMessage);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendCallBack(String message) {
        SendMessage newMessage = new SendMessage();
        newMessage.setChatId(service.getChatId());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton callback = new InlineKeyboardButton();
        callback.setCallbackData(message);

        markup.setKeyboard(Collections.singletonList(Collections.singletonList(callback)));
        newMessage.setReplyMarkup(markup);
        System.out.println("TO NO CALLBACK");

        try {
            execute(newMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageComOpcoes(Long chatId, String mensagem, List<InlineKeyboardButton> opcoes) {


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        opcoes.forEach(
                option -> {
                    rows.add(Collections.singletonList(option));
                }
        );

        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(mensagem);
        message.setReplyMarkup(markup);


        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private String getMessage(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        return update.getMessage().getText();
    }

    private Long getChatId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return update.getMessage().getChatId();
    }


    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery() || update.hasMessage()) {


            String resposta = getMessage(update);
            Long chatId = getChatId(update);

//            Despesas despesa = service.getDespesas().getOrDefault(chatId, new Despesas());
//            Categoria categoria = service.getCategoriaMap().getOrDefault(chatId, new Categoria());
//            Cartoes cartao = service.getCartoesMap().getOrDefault(chatId, new Cartoes());

            if (resposta.equals("/sair")) {
                service.limparFluxos(chatId);
                return;
            }

            if (resposta.equals("/removeall")) {
                service.getEtapas().put(chatId, EtapasMenuPrincipal.HANDLER_REMOVE_ALL);
            }


            handler.stream()
                   .filter(h -> h.getEtapa().equals(service.getEtapas().getOrDefault(chatId, EtapasMenuPrincipal.INICIAR_BOT)))
                   .findFirst()
                   .ifPresent(e -> {
                       e.setarChatId(chatId);
                       e.handleMessage(resposta);

                   });
        }


    }


}