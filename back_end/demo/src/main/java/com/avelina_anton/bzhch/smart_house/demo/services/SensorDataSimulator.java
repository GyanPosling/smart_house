package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

@Service
public class SensorDataSimulator {
    private static final Logger logger = LoggerFactory.getLogger(SensorDataSimulator.class);
    private final SensorsRepository sensorsRepository;
    private final SimulationService simulationService;
    private final DevicesService devicesService;
    private final Random random = new Random();

    // Допустимые пределы для дрифта (шире зоны комфорта)
    private static final double TEMP_DRIFT_MIN = 18.0;
    private static final double TEMP_DRIFT_MAX = 26.0;
    private static final double HUMIDITY_DRIFT_MIN = 25.0;
    private static final double HUMIDITY_DRIFT_MAX = 50.0;
    private static final double CO2_DRIFT_MAX = 1200.0;

    public SensorDataSimulator(SensorsRepository sensorsRepository, SimulationService simulationService,
                               DevicesService devicesService) {
        this.sensorsRepository = sensorsRepository;
        this.simulationService = simulationService;
        this.devicesService = devicesService;
    }

    @Scheduled(fixedRate = 5000)
    public void simulateSensorData() {
        boolean hasManualDevices = devicesService.findAll().stream()
                .anyMatch(device -> device.getStatus() == com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus.ON &&
                        device.getMode() == com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode.MANUAL);

        // Если есть ручные устройства - дрифт минимальный (пользователь сам управляет)
        // Если все устройства в авто или выключены - нормальный дрифт
        double driftIntensity = hasManualDevices ? 0.1 : 1.0;

        driftTemperature(driftIntensity);
        driftHumidity(driftIntensity);
        driftCO2(driftIntensity);
        simulateRandomSensor(SensorType.NOISE, 30.0, 80.0);
    }

    private void driftTemperature(double intensity) {
        double currentValue = simulationService.getSensorValue(SensorType.TEMPERATURE);
        double driftAmount = (random.nextDouble() - 0.5) * 0.2 * intensity;

        double newValue = currentValue + driftAmount;
        newValue = Math.max(TEMP_DRIFT_MIN, Math.min(TEMP_DRIFT_MAX, newValue));

        simulationService.environmentState.put(SensorType.TEMPERATURE, newValue);
        updateSensorInDb(SensorType.TEMPERATURE, newValue);
    }

    private void driftHumidity(double intensity) {
        double currentValue = simulationService.getSensorValue(SensorType.HUMIDITY);
        double driftAmount = (random.nextDouble() - 0.5) * 0.5 * intensity;

        double newValue = currentValue + driftAmount;
        newValue = Math.max(HUMIDITY_DRIFT_MIN, Math.min(HUMIDITY_DRIFT_MAX, newValue));

        simulationService.environmentState.put(SensorType.HUMIDITY, newValue);
        updateSensorInDb(SensorType.HUMIDITY, newValue);
    }

    private void driftCO2(double intensity) {
        double currentValue = simulationService.getSensorValue(SensorType.CO2);
        double growthRate = 10.0 * intensity;

        double newValue = currentValue + growthRate;
        newValue = Math.min(CO2_DRIFT_MAX, newValue);

        simulationService.environmentState.put(SensorType.CO2, newValue);
        updateSensorInDb(SensorType.CO2, newValue);
    }

    private void simulateRandomSensor(SensorType type, double min, double max) {
        double value = min + (max - min) * random.nextDouble();
        simulationService.environmentState.put(type, value);
        updateSensorInDb(type, value);
    }

    private void updateSensorInDb(SensorType type, double value) {
        List<Sensor> existingSensors = sensorsRepository.findByType(type);
        if (existingSensors.isEmpty()) {
            Sensor newSensor = new Sensor();
            newSensor.setType(type);
            newSensor.setValue(value);
            newSensor.setLocation("Гостиная");
            sensorsRepository.save(newSensor);
        } else {
            Sensor sensor = existingSensors.get(0);
            sensor.setValue(value);
            sensorsRepository.save(sensor);
        }
    }
}