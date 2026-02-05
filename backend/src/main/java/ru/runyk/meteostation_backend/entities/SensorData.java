package ru.runyk.meteostation_backend.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "humidity")
    private Double humidity;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    public SensorData() {
    }

    public SensorData(Double temperature, Double humidity, LocalDateTime recordedAt, LocalDateTime createAt) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.recordedAt = recordedAt;
        this.createAt = createAt;
    }

    public Long getId() {
        return id;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
