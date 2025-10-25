package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SensorDataSimulator {
    private static final Logger logger = LoggerFactory.getLogger(SensorDataSimulator.class);
    private final SensorsRepository sensorsRepository;
    private final SimulationService simulationService;
    private final DevicesService devicesService;
    private final Random random = new Random();

    private static final double TEMP_DRIFT_MIN = 15.0;
    private static final double TEMP_DRIFT_MAX = 30.0;
    private static final double HUMIDITY_DRIFT_MIN = 20.0;
    private static final double HUMIDITY_DRIFT_MAX = 60.0;
    private static final double CO2_DRIFT_MAX = 1500.0;

    public SensorDataSimulator(SensorsRepository sensorsRepository, SimulationService simulationService,
                               DevicesService devicesService) {
        this.sensorsRepository = sensorsRepository;
        this.simulationService = simulationService;
        this.devicesService = devicesService;

        simulationService.environmentState.put(SensorType.TEMPERATURE, 18.0); // Ниже 20°C
        simulationService.environmentState.put(SensorType.HUMIDITY, 25.0);    // Ниже 30%
        simulationService.environmentState.put(SensorType.CO2, 1100.0);       // Выше 1000 ppm
        simulationService.environmentState.put(SensorType.NOISE, 45.0);
    }

    @Scheduled(fixedRate = 5000)
    public void simulateSensorData() {
        boolean hasActiveDevices = devicesService.findAll().stream()
                .anyMatch(device -> device.getStatus() == com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus.ON);

        double driftIntensity = hasActiveDevices ? 0.5 : 2.0;

        driftTemperature(driftIntensity);
        driftHumidity(driftIntensity);
        driftCO2(driftIntensity);
        simulateRandomSensor(SensorType.NOISE, 30.0, 80.0);
    }

    private void driftTemperature(double intensity) {
        double currentValue = simulationService.getSensorValue(SensorType.TEMPERATURE);
        double driftAmount = (random.nextDouble() - 0.5) * 1.0 * intensity;

        double newValue = currentValue + driftAmount;
        // Ограничиваем, чтобы оставаться вне зоны комфорта (20-24°C) при выключенных устройствах
        if (!hasActiveDevices()) {
            newValue = clampOutsideComfort(newValue, 20.0, 24.0);
        }
        newValue = Math.max(TEMP_DRIFT_MIN, Math.min(TEMP_DRIFT_MAX, newValue));

        simulationService.environmentState.put(SensorType.TEMPERATURE, newValue);
        updateSensorInDb(SensorType.TEMPERATURE, newValue);
    }

    private void driftHumidity(double intensity) {
        double currentValue = simulationService.getSensorValue(SensorType.HUMIDITY);
        double driftAmount = (random.nextDouble() - 0.5) * 2.0 * intensity;

        double newValue = currentValue + driftAmount;
        if (!hasActiveDevices()) {
            newValue = clampOutsideComfort(newValue, 30.0, 45.0);
        }
        newValue = Math.max(HUMIDITY_DRIFT_MIN, Math.min(HUMIDITY_DRIFT_MAX, newValue));

        simulationService.environmentState.put(SensorType.HUMIDITY, newValue);
        updateSensorInDb(SensorType.HUMIDITY, newValue);
    }

    private void driftCO2(double intensity) {
        double currentValue = simulationService.getSensorValue(SensorType.CO2);
        double growthRate = (random.nextDouble() * 20.0 + 5.0) * intensity;

        double newValue = currentValue + growthRate;
        if (!hasActiveDevices()) {
            newValue = Math.max(1000.0, newValue); // Держим выше 1000 ppm
        }
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

    private boolean hasActiveDevices() {
        return devicesService.findAll().stream()
                .anyMatch(device -> device.getStatus() == com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus.ON);
    }

    private double clampOutsideComfort(double value, double minComfort, double maxComfort) {
        if (value >= minComfort && value <= maxComfort) {
            if (Math.abs(value - minComfort) < Math.abs(value - maxComfort)) {
                return minComfort - 1.0; // Смещаем ниже
            } else {
                return maxComfort + 1.0; // Смещаем выше
            }
        }
        return value;
    }
}