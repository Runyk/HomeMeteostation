package ru.polikarpov.education;

import com.fazecast.jSerialComm.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.*;
import java.io.FileWriter;
import java.util.*;

public class Main {
    // "Планировщик" для выполнения кода по времени
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    // Переменная для хранения последних полученных данных с датчика
    private static SensorData lastSensorData = null;

    public static void main(String[] args) throws Exception {
        // Поиск Arduino
        System.out.println("Поиск Arduino...");
        // Переменная для хранения номера порта
        SerialPort port = null;

        for (SerialPort p : SerialPort.getCommPorts()) {
            System.out.println("Найден порт: " + p.getSystemPortName());
            if (p.getDescriptivePortName().contains("Arduino") ||
                    p.getDescriptivePortName().contains("CH340")) {
                port = p;
                break;
            }
        }

        // Если порт не найден, выводим сообщение об этом
        if (port == null) {
            System.out.println("Arduino не найден! Используйте другой порт.");
            port = SerialPort.getCommPorts()[0];
        }

        // Подключение Arduino
        port.setBaudRate(115200); // Количество бод должно быть такое же, что и в Arduino!
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        port.openPort();
        System.out.println("Подключено к: " + port.getSystemPortName());

        startTelegramNotifications();

        // Чтение данных
        ObjectMapper mapper = new ObjectMapper();
        Scanner scanner = new Scanner(port.getInputStream());

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            // Пропускаем пустые строки
            if (line.isEmpty()) {
                continue;
            }

            // Цикл обработки данных от Arduino
            if (line.startsWith("{")) {
                try {
                    SensorData data = mapper.readValue(line, SensorData.class);

                    lastSensorData = data;

                    // Вывод в консоль данных
                    String time = new Date().toString().substring(11, 19);
                    System.out.println("[" + time + "] " + data);

                    // Сохранение в файл
                    saveToFile(data);

                } catch (Exception e) {
                    System.err.println("Ошибка в данных: " + line.substring(0, Math.min(50, line.length())));
                }
            }
        }

        scanner.close();
        port.closePort();
    }

    // Метод отправки сообщения в Боте
    private static void startTelegramNotifications() {
        String botToken = "ТОКЕН_БОТА"; // Токен бота (узнать можно через @BotFather)
        String chatId = "ВАШ_CHAT_ID"; // Здесь должен быть ВАШ chat_id (можно узнать в @userinfobot)

        // Объект
        TelegramBotService bot = new TelegramBotService(botToken, chatId);

        // Отправление сообщения в чат с ботом
        scheduler.scheduleAtFixedRate(() -> {
            // Получаем последние данные
            SensorData lastData = getLastSensorData();

            // Если полученные данные содержат переданную информацию от Arduino, то создается сообщение с этими данными
            if (lastData != null) {
                String message = String.format(
                        "📊 Данные от Вашей метеостанции:\n" +
                                "🌡 Температура: %.1f°C\n" +
                                "💧 Влажность: %.1f%%\n" +
                                "⏰ Время: %s",
                        lastData.temperature,
                        lastData.humidity,
                        new java.util.Date()
                );
                bot.sendMessage(message);
            }
        }, 0, 1, TimeUnit.MINUTES); // Настройка интервала времени для отправления нового уведомления
    }

    // Метод получения данных
    private static SensorData getLastSensorData() {
        return lastSensorData;
    }

    // Сохранение полученных данных в файл
    static void saveToFile(SensorData data) {
        try (FileWriter fw = new FileWriter("data.csv", true)) {
            fw.write(new Date() + "," + data.temperature + "," + data.humidity + "\n");
        } catch (Exception e) {
        }
    }
}