package ru.runyk.meteostation_backend.dto;

import org.springframework.http.ResponseEntity;
import ru.runyk.meteostation_backend.service.ArduinoService;

import java.util.List;

public class WeatherResponse {
    private boolean success;
    private String message;
    private SensorDataDTO data;
    private List<SensorDataDTO> history; // Для будущего расширения
    private ArduinoService arduinoService;

    public WeatherResponse(boolean success, String message, SensorDataDTO data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static WeatherResponse success(SensorDataDTO data){
        return new WeatherResponse(true, "Данные успешно приняты", data);
    }

    public static WeatherResponse error(String message){
        return new WeatherResponse(false, message, null);
    }
}