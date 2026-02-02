package ru.runyk.meteostation_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.runyk.meteostation_backend.service.TelegramBotService;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @Autowired
    private TelegramBotService telegramBotService;

    @GetMapping("/telegram")
    public String testTelegram() {
        telegramBotService.testSendMessage();
        return "Тестовое сообщение отправлено в Telegram!";
    }
}