package ru.runyk.meteostation_backend.dto;

import java.util.List;

public class WeatherResponse {
    private boolean success;
    private String message;
    private SensorDataDTO data;
    private List<SensorDataDTO> history; // Для будущего расширения

    public WeatherResponse(boolean success, String message, SensorDataDTO data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}