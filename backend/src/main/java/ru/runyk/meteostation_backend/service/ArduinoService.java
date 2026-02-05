package ru.runyk.meteostation_backend.service;

import com.fazecast.jSerialComm.SerialPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.runyk.meteostation_backend.dto.SensorDataDTO;
import ru.runyk.meteostation_backend.entities.SensorData;
import ru.runyk.meteostation_backend.repositories.SensorDataRepository;

import java.time.LocalDateTime;
import java.util.Scanner;

@Service
public class ArduinoService {

    private SerialPort arduinoPort;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SensorDataDTO lastSensorData;

    private final SensorDataRepository sensorDataRepository;

    public ArduinoService(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    // Запуск получения данных о подключении порта с Arduino и запуск чтения данных с него
    @PostConstruct
    public void init() {
        System.out.println("=== Запуск ArduinoService ===");
        connectToArduino();
        startReadingData();
    }

    // Получение подключенного порта
    private void connectToArduino() {
        System.out.println("Поиск Arduino...");

        for (SerialPort port : SerialPort.getCommPorts()) {
            System.out.println("Найден порт: " + port.getSystemPortName() +
                    " - " + port.getDescriptivePortName());

            // Ищем порт, в описании которого есть "arduino" или "ch340"
            if (port.getDescriptivePortName().toLowerCase().contains("arduino") ||
                    port.getDescriptivePortName().toLowerCase().contains("ch340")) {
                arduinoPort = port;
                break;
            }
        }

        // Если порт не определился, попытка подключиться к другому порту
        if (arduinoPort == null && SerialPort.getCommPorts().length > 0) {
            arduinoPort = SerialPort.getCommPorts()[0];
            System.out.println("ВНИМАНИЕ: Arduino не найден, используем первый порт: " +
                    arduinoPort.getSystemPortName());
        }

        // Если порт найден, начинаем чтение на скорости 115200 бод
        if (arduinoPort != null) {
            arduinoPort.setBaudRate(115200);
            arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

            // Если порт открылся, выводим сообщение об успешном подключении
            if (arduinoPort.openPort()) {
                System.out.println("УСПЕХ: Подключено к " + arduinoPort.getSystemPortName());
            } else {
                System.out.println("ОШИБКА: Не удалось открыть порт!");
            }
        } else {
            System.out.println("ОШИБКА: COM-порты не найдены!");
        }
    }

    // Запуск потока для непрерывного чтения данных с датчика
    private void startReadingData() {
        if (arduinoPort == null || !arduinoPort.isOpen()) {
            System.out.println("ПРЕДУПРЕЖДЕНИЕ: Порт не открыт, данные не читаются");
            return;
        }

        new Thread(() -> {
            try (Scanner scanner = new Scanner(arduinoPort.getInputStream())) {
                // Чтение строк пока порт открыт
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

    // Чтение входящего JSON и обработка данных с него
    private void processSensorData(String jsonLine) {
        try {
            if (jsonLine.startsWith("{")) {
                SensorData rawData = objectMapper.readValue(jsonLine, SensorData.class);

                lastSensorData = new SensorDataDTO(
                        rawData.getTemperature(),
                        rawData.getHumidity(),
                        LocalDateTime.now()
                );

                SensorData entity = new SensorData(
                        rawData.getTemperature(),
                        rawData.getHumidity(),
                        LocalDateTime.now()
                );

                sensorDataRepository.save(entity);

                System.out.println("ДАННЫЕ: " + lastSensorData.getTemperature() +
                        "°C, " + lastSensorData.getHumidity() + "%");
            }
        } catch (Exception e) {
            System.out.println("ОШИБКА парсинга: " +
                    jsonLine.substring(0, Math.min(50, jsonLine.length())));
        }
    }

    // Проверка подключения порта
    public boolean isConnected() {
        return arduinoPort != null && arduinoPort.isOpen();
    }

    // Освобождение порта при остановке программы
    @PreDestroy
    public void cleanup() throws InterruptedException {
        if (isConnected()) {
            arduinoPort.closePort();
            Thread.sleep(1000);
            System.out.println("Порт Arduino закрыт");
        }
    }

    // Класс для чтения JSON
    private static class RawSensorData {
        private Double temperature;
        private Double humidity;

        public Double getTemperature() {
            return temperature;
        }

        public Double getHumidity() {
            return humidity;
        }
    }

    public SensorDataDTO getLastSensorData() {
        return lastSensorData;
    }
}