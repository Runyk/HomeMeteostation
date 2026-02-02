package ru.runyk.meteostation_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.runyk.meteostation_backend.dto.SensorDataDTO;
import ru.runyk.meteostation_backend.dto.WeatherResponse;
import ru.runyk.meteostation_backend.service.ArduinoService;
import ru.runyk.meteostation_backend.service.TelegramBotService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private ArduinoService arduinoService;

    @Autowired
    private TelegramBotService telegramBotService;

    // ЭТОТ МЕТОД РАБОТАЕТ ЧЕРЕЗ GET ИЗ БРАУЗЕРА
    @GetMapping("/latest")
    public ResponseEntity<WeatherResponse> getLatestWeatherData() {
        SensorDataDTO lastData = arduinoService.getLastSensorData();

        if (lastData == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new WeatherResponse(false,
                            "Данные с датчика еще не получены", null));
        }

        // Отправка в Telegram
        try {
            telegramBotService.sendMessage(
                    "📡 *Ручной запрос данных*\n" +
                            "🌡 Температура: " + lastData.getTemperature() + "°C\n" +
                            "💧 Влажность: " + lastData.getHumidity() + "%\n" +
                            "⏰ Время: " + lastData.getTimestamp()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        WeatherResponse response = new WeatherResponse(
                true,
                "Последние данные с датчика",
                lastData
        );

        return ResponseEntity.ok(response);
    }

    // ДЛЯ POST-ЗАПРОСОВ (из Arduino)
    @PostMapping("/latest")
    public ResponseEntity<WeatherResponse> getLatestWeatherDataPost() {
        return getLatestWeatherData(); // Вызывает тот же метод
    }

    @GetMapping("/status")
    public ResponseEntity<?> getArduinoStatus() {
        return ResponseEntity.ok().body(
                new Object() {
                    public final boolean connected = arduinoService.isConnected();
                    public final String status = arduinoService.isConnected()
                            ? "Arduino подключен"
                            : "Arduino не подключен";
                }
        );
    }
}