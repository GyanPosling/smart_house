package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
import com.avelina_anton.bzhch.smart_house.demo.utllis.SensorNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorsService {

    private final SensorsRepository sensorRepository;

    public SensorsService(SensorsRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public List<Sensor> getSensorsByUser(User user) {
        return sensorRepository.findByUser(user);
    }

    public Sensor getSensorById(Long id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new SensorNotFoundException("Датчик с id " + id + " не найден"));
    }

    public List<Sensor> getSensorsByUserAndType(User user, SensorType type) {
        return sensorRepository.findByUserAndType(user, type);
    }

    public Sensor saveSensor(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public void deleteSensor(Long id) {
        Sensor sensor = getSensorById(id);
        sensorRepository.delete(sensor);
    }

    public void createDefaultSensorsForUser(User user) {
        List<SensorType> defaultTypes = List.of(SensorType.TEMPERATURE, SensorType.HUMIDITY, SensorType.CO2, SensorType.NOISE);
        for (SensorType type : defaultTypes) {
            // Проверяем, есть ли уже датчик этого типа для пользователя
            if (sensorRepository.findByUserAndType(user, type).isEmpty()) {
                Sensor sensor = new Sensor();
                sensor.setType(type);
                sensor.setValue(getInitialValue(type)); // Устанавливаем начальное значение вне зоны комфорта
                sensor.setLocation("Гостиная");
                sensor.setUser(user);
                sensor.setCreatedAt(LocalDateTime.now());
                sensor.setUpdatedAt(LocalDateTime.now());
                sensorRepository.save(sensor);
            }
        }
    }

    private double getInitialValue(SensorType type) {
        switch (type) {
            case TEMPERATURE:
                return 18.0; // Ниже зоны комфорта (20-24°C)
            case HUMIDITY:
                return 25.0; // Ниже зоны комфорта (30-45%)
            case CO2:
                return 1100.0; // Выше зоны комфорта (<1000 ppm)
            case NOISE:
                return 45.0; // Нейтральное значение
            default:
                return 0.0;
        }
    }
}