package ru.runyk.meteostation_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.runyk.meteostation_backend.service.TelegramBotService;

// Класс для конфигурации Telegram Бота
@Configuration
public class TelegramBotConfig {

    // Поле для Токена Бота
    @Value("${telegram.bot.token}")
    private String botToken;

    // Поле для Id чата пользователя
    @Value("${telegram.chat.id}")
    private String chatId;

    // Username Бота
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Bean
    public TelegramBotService telegramBotService() {
        return new TelegramBotService(botToken, chatId, botUsername);
    }

    // Активация Telegram Бота
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBotService botService) throws TelegramApiException {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(botService);
            return botsApi;
        } catch (TelegramApiException e) {
            return null;
        }
    }
}
