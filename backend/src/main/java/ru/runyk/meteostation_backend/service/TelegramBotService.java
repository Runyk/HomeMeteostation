package ru.runyk.meteostation_backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.runyk.meteostation_backend.dto.SensorDataDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private String botToken;
    private final String chatId;
    private final String botUsername;

    @Autowired
    private ArduinoService arduinoService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void testSendMessage() {
        sendMessage("✅ Тестовое сообщение из Spring Boot!");
    }

    public TelegramBotService(
            @Value("${telegram.bot.token}") String botToken, @Value("${telegram.chat.id}") String chatId,
            @Value("${telegram.bot.username}") String botUsername) {
        super(botToken);
        this.chatId = chatId;
        this.botUsername = botUsername;
    }

    // Отправка сообщения от Бота в Telegram
    public void sendMessage(String text){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    public void sendNotification() {
        SensorDataDTO data = arduinoService.getLastSensorData();

        if (data != null) {
            String message = String.format(
                    "📊 Отчет о микроклимате\n" +
                    "🌡 Температура: %.1f°C\n" +
                    "💧 Влажность: %.1f%%\n",
                    data.getTemperature(),
                    data.getHumidity()
            );

            sendMessage(message);
        } else {
            sendMessage("Данные с датчика пока не получены, проверьте подключение Arduino.");
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Бот только отправляет сообщения, не обрабатывает входящие
        // Можно оставить пустым или добавить логирование
    }

    public String getBotUsername(){
        return botUsername;
    }

    public String getChatId() {
        return chatId;
    }
}
