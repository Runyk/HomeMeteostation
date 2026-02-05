package ru.runyk.meteostation_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.runyk.meteostation_backend.entities.SensorData;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
}
