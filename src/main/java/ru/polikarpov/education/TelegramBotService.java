package ru.polikarpov.education;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBotService extends TelegramLongPollingBot {

    // Переменные для хранения токена бота и Вашего chatId
    private final String botToken;
    private final String chatId;

    public TelegramBotService(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    // Отправка сообщения
    public void sendMessage(String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
            System.out.println("Сообщение отправлено в Telegram");
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки в Telegram: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Для добавления новых команд в боте
    }

    @Override
    public String getBotUsername() {
        return "Your Bot's name";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}