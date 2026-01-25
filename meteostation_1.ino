#include <DHT.h>
#define DHTPIN 2
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(115200);  // Количество бод должно быть такое же, что и в Java!
  dht.begin();
}

void loop() {
  delay(2000);  // Пауза
  
  // Переменные для хранения данных о влажности и температуры
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  
  if (!isnan(h) && !isnan(t)) {
    // Отправляем JSON в Serial порт
    Serial.print("{\"temperature\":");
    Serial.print(t);
    Serial.print(",\"humidity\":");
    Serial.print(h);
    Serial.println("}");
  }
}