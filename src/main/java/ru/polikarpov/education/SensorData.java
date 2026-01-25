package ru.polikarpov.education;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Класс для хранения данных датчика
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем неизвестные поля
public class SensorData {
    public float temperature;
    public float humidity;

    // Форматирование данных для вывода
    public String toString() {
        return String.format("Температура: %.1f°C, Влажность: %.1f%%", temperature, humidity);
    }
}