package ru.runyk.meteostation_backend.service;

import com.fazecast.jSerialComm.SerialPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.runyk.meteostation_backend.dto.SensorDataDTO;
import java.util.Scanner;

@Service
public class ArduinoService {

    private SerialPort arduinoPort;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private SensorDataDTO lastSensorData;

    @PostConstruct
    public void init() {
        System.out.println("=== Запуск ArduinoService ===");
        connectToArduino();
        startReadingData();
    }

    private void connectToArduino() {
        System.out.println("Поиск Arduino...");

        for (SerialPort port : SerialPort.getCommPorts()) {
            System.out.println("Найден порт: " + port.getSystemPortName() +
                    " - " + port.getDescriptivePortName());

            if (port.getDescriptivePortName().toLowerCase().contains("arduino") ||
                    port.getDescriptivePortName().toLowerCase().contains("ch340")) {
                arduinoPort = port;
                break;
            }
        }

        if (arduinoPort == null && SerialPort.getCommPorts().length > 0) {
            arduinoPort = SerialPort.getCommPorts()[0];
            System.out.println("ВНИМАНИЕ: Arduino не найден, используем первый порт: " +
                    arduinoPort.getSystemPortName());
        }

        if (arduinoPort != null) {
            arduinoPort.setBaudRate(115200);
            arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

            if (arduinoPort.openPort()) {
                System.out.println("УСПЕХ: Подключено к " + arduinoPort.getSystemPortName());
            } else {
                System.out.println("ОШИБКА: Не удалось открыть порт!");
            }
        } else {
            System.out.println("ОШИБКА: COM-порты не найдены!");
        }
    }

    private void startReadingData() {
        if (arduinoPort == null || !arduinoPort.isOpen()) {
            System.out.println("ПРЕДУПРЕЖДЕНИЕ: Порт не открыт, данные не читаются");
            return;
        }

        new Thread(() -> {
            try (Scanner scanner = new Scanner(arduinoPort.getInputStream())) {
                while (arduinoPort.isOpen() && scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    processSensorData(line);
                }
            } catch (Exception e) {
                System.out.println("ОШИБКА чтения данных: " + e.getMessage());
            }
        }).start();

        System.out.println("Запущено чтение данных с Arduino");
    }

    private void processSensorData(String jsonLine) {
        try {
            if (jsonLine.startsWith("{")) {
                SensorData sensorData = objectMapper.readValue(jsonLine, SensorData.class);

                lastSensorData = new SensorDataDTO(
                        sensorData.getTemperature(),
                        sensorData.getHumidity()
                );

                System.out.println("ДАННЫЕ: " + lastSensorData.getTemperature() +
                        "°C, " + lastSensorData.getHumidity() + "%");
            }
        } catch (Exception e) {
            System.out.println("ОШИБКА парсинга: " +
                    jsonLine.substring(0, Math.min(50, jsonLine.length())));
        }
    }

    public SensorDataDTO getLastSensorData() {
        return lastSensorData;
    }

    public boolean isConnected() {
        return arduinoPort != null && arduinoPort.isOpen();
    }

    @PreDestroy
    public void cleanup() {
        if (arduinoPort != null && arduinoPort.isOpen()) {
            arduinoPort.closePort();
            System.out.println("Порт Arduino закрыт");
        }
    }

    private static class SensorData {
        private Double temperature;
        private Double humidity;

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Double getHumidity() {
            return humidity;
        }

        public void setHumidity(Double humidity) {
            this.humidity = humidity;
        }
    }
}