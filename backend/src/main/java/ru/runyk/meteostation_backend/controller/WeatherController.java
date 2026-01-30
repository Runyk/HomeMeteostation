package ru.runyk.meteostation_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.runyk.meteostation_backend.dto.SensorDataDTO;
import ru.runyk.meteostation_backend.dto.WeatherResponse;
import ru.runyk.meteostation_backend.service.ArduinoService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private ArduinoService arduinoService;

    // Конструктор
    @Autowired
    public WeatherController(ArduinoService arduinoService) {
        this.arduinoService = arduinoService;
    }

    @PostMapping
    public ResponseEntity<WeatherResponse> saveWeatherData(
            @RequestBody SensorDataDTO sensorData) {

        WeatherResponse response = new WeatherResponse(
                true,
                "Данные успешно приняты",
                sensorData
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<WeatherResponse> getLatestWeatherData() {
        SensorDataDTO lastData = arduinoService.getLastSensorData();

        if (lastData == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new WeatherResponse(false,
                            "Данные с датчика еще не получены", null));
        }

        WeatherResponse response = new WeatherResponse(
                true,
                "Последние данные с датчика",
                lastData
        );

        return ResponseEntity.ok(response);
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