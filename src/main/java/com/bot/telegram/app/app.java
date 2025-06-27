package com.bot.telegram.app;

import com.bot.telegram.app.core.TelegramBot;
import com.bot.telegram.app.domain.model.Despesas;
import com.bot.telegram.app.domain.repository.DespesasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableMongoRepositories
@Configuration
public class app {


    public static void main(String[] args) {
        SpringApplication.run(app.class, args);
    }


    @Bean
    public DefaultBotOptions botOptions() {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setGetUpdatesTimeout(60);
        return options;
    }

    @Bean
    public CommandLineRunner commandLineRunner(TelegramBot telegramBot) {
        return args -> {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        };
    }


}
