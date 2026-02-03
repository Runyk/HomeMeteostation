package ru.runyk.meteostation_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.runyk.meteostation_backend.dto.SensorDataDTO;
import ru.runyk.meteostation_backend.dto.WeatherResponse;
import ru.runyk.meteostation_backend.service.ArduinoService;

// Контроллер для возвращения данных
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private ArduinoService arduinoService;

    // Конструктор
    @Autowired
    public WeatherController(ArduinoService arduinoService) {
        this.arduinoService = arduinoService;
    }

    // Обработка POST запросов на /weather
    @PostMapping
    public ResponseEntity<WeatherResponse> saveWeatherData(
            @RequestBody SensorDataDTO sensorData) {

        // Возвращение HTTP 200 OK с данными
        return ResponseEntity.ok(WeatherResponse.success(sensorData));
    }

    // Обработка GET запросов для получения актуальных данных с датчика
    @GetMapping("/latest")
    public ResponseEntity<WeatherResponse> getLatestWeatherData() {
        SensorDataDTO lastData = arduinoService.getLastSensorData();

        if (lastData == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(WeatherResponse.error("Данные с датчика еще не получены"));
        }

        // Возвращение HTTP 200 OK с данными
        return ResponseEntity.ok(WeatherResponse.success(lastData));
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