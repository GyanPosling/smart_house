// SensorDataSimulator.java
package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SensorDataSimulator {
    private final SensorsRepository sensorsRepository;
    private final Random random = new Random();

    public SensorDataSimulator(SensorsRepository sensorsRepository) {
        this.sensorsRepository = sensorsRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void simulateSensorData() {
        simulateSensor(SensorType.TEMPERATURE, 18.0, 28.0, "Гостиная");
        simulateSensor(SensorType.HUMIDITY, 20.0, 60.0, "Гостиная");
        simulateSensor(SensorType.CO2, 400.0, 1500.0, "Гостиная");
        simulateSensor(SensorType.NOISE, 30.0, 80.0, "Гостиная");
    }

    private void simulateSensor(SensorType type, double min, double max, String location) {
        double value = min + (max - min) * random.nextDouble();

        List<Sensor> existingSensors = sensorsRepository.findByType(type);
        if (existingSensors.isEmpty()) {
            Sensor newSensor = new Sensor();
            newSensor.setType(type);
            newSensor.setValue(value);
            newSensor.setLocation(location);
            sensorsRepository.save(newSensor);
        } else {
            Sensor sensor = existingSensors.get(0);
            sensor.setValue(value);
            sensorsRepository.save(sensor);
        }
    }
}