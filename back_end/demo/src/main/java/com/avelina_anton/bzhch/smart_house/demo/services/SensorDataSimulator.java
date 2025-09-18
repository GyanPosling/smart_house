package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SensorDataSimulator {

    private final SensorsRepository sensorsRepository;
    private final Random random = new Random();

    public SensorDataSimulator(SensorsRepository sensorsRepository) {
        this.sensorsRepository = sensorsRepository;
    }

    private void saveOrUpdateSensor(SensorType type, double value) {
        sensorsRepository.findFirstByType(type).ifPresentOrElse(
                sensor -> {
                    sensor.setValue(value);
                    sensorsRepository.save(sensor);
                },

                () -> {
                    Sensor newSensor = new Sensor();
                    newSensor.setType(type);
                    newSensor.setValue(value);
                    sensorsRepository.save(newSensor);
                }
        );
    }

    @Scheduled(fixedRate = 5000)
    public void simulateSensorData() {
        double temperatureValue = 15.0 + (30.0 - 15.0) * random.nextDouble();
        saveOrUpdateSensor(SensorType.TEMPERATURE, temperatureValue);

        double humidityValue = 20.0 + (60.0 - 20.0) * random.nextDouble();
        saveOrUpdateSensor(SensorType.HUMIDITY, humidityValue);


        double co2Value = 400.0 + (1500.0 - 400.0) * random.nextDouble();
        saveOrUpdateSensor(SensorType.CO2, co2Value);

        double noiseValue = 30.0 + (90.0 - 30.0) * random.nextDouble();
        saveOrUpdateSensor(SensorType.NOISE, noiseValue);

    }
}
