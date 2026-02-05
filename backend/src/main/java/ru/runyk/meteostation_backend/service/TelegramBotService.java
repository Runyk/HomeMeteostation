package ru.runyk.meteostation_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.runyk.meteostation_backend.dto.SensorDataDTO;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private String botToken;
    private final String chatId;
    private final String botUsername;

    @Autowired
    private ArduinoService arduinoService;

    public TelegramBotService(
            @Value("${telegram.bot.token}") String botToken, @Value("${telegram.chat.id}") String chatId,
            @Value("${telegram.bot.username}") String botUsername) {
        super(botToken);
        this.chatId = chatId;
        this.botUsername = botUsername;
    }

    // Отправление отчета от Бота.
    // fixedDelay = n, где n - интервал времени между отправлениями отчетов (в миллисекундах).
    // initialDelay - задержка между запуском программы и первым отправлением отчета.
    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    public void sendNotification() {
        SensorDataDTO data = arduinoService.getLastSensorData();

        // Если данные содержат информацию, отправляется отчет
        if (data != null) {
            String message = String.format(
                    "📊 Отчет о микроклимате\n" +
                    "🌡 Температура: %.1f°C\n" +
                    "💧 Влажность: %.1f%%\n",
                    data.getTemperature(),
                    data.getHumidity()
            );
            sendMessage(message);

            // В ином случае отправление сообщения об отсутствии данных
        } else {
            sendMessage("Данные с датчика пока не получены, проверьте подключение Arduino.");
        }
    }

    // Тестовое отправление сообщение от Бота. Нужно для проверки соединения и работоспособности API Бота.
    public void testSendMessage() {
        sendMessage("✅ Тестовое сообщение из Spring Boot!");
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

    @Override
    public void onUpdateReceived(Update update) {
        // Бот только отправляет сообщения, не обрабатывает входящие.
        // Для будущего расширения.
    }

    public String getBotUsername(){
        return botUsername;
    }

    public String getChatId() {
        return chatId;
    }
}
