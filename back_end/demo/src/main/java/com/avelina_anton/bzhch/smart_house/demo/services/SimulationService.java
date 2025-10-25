package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SimulationService {
    private static final Logger logger = LoggerFactory.getLogger(SimulationService.class);

    public final ConcurrentMap<SensorType, Double> environmentState = new ConcurrentHashMap<>();
    private final SensorsRepository sensorsRepository;
    private final DevicesService devicesService;

    public SimulationService(SensorsRepository sensorsRepository, DevicesService devicesService) {
        this.sensorsRepository = sensorsRepository;
        this.devicesService = devicesService;
       
        environmentState.put(SensorType.TEMPERATURE, 22.0);
        environmentState.put(SensorType.HUMIDITY, 40.0);
        environmentState.put(SensorType.CO2, 600.0);
        environmentState.put(SensorType.NOISE, 45.0);
    }

    @Scheduled(fixedRate = 5000)
    public void applyAllDevicesInfluence() {
        List<Device> allDevices = devicesService.findAll();

        for (Device device : allDevices) {
            if (device.getStatus() == DeviceStatus.ON) {
                applyDeviceInfluence(device);
            }
        }
    }

    private void applyDeviceInfluence(Device device) {
        if (device.getStatus() == DeviceStatus.OFF) return;

        SensorType targetSensorType = getTargetSensorType(device.getType());
        if (targetSensorType == null) return;

        double currentValue = environmentState.getOrDefault(targetSensorType, 0.0);
        double influence = calculateInfluence(device.getType(), device.getPowerLevel(), currentValue);
        double newValue = currentValue + influence;

        if (device.getMode() == DeviceMode.AUTO) {
            newValue = applyComfortLimits(targetSensorType, newValue);
        }

        environmentState.put(targetSensorType, newValue);
        updateSensorInDb(targetSensorType, newValue);

        logger.debug("{} {} ({}%) → {}: {} → {}",
                device.getName(),
                device.getMode() == DeviceMode.AUTO ? "АВТО" : "РУЧНОЙ",
                device.getPowerLevel(),
                targetSensorType,
                String.format("%.2f", currentValue),
                String.format("%.2f", newValue));
    }

    private SensorType getTargetSensorType(com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType deviceType) {
        switch (deviceType) {
            case HEATER:
            case AIR_CONDITIONER:
                return SensorType.TEMPERATURE;
            case HUMIDIFIER:
            case DEHUMIDIFIER:
                return SensorType.HUMIDITY;
            case VENTILATOR:
                return SensorType.CO2;
            default:
                return null;
        }
    }

    private double calculateInfluence(com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType deviceType, int powerLevel, double currentValue) {
        double influenceFactor = powerLevel / 100.0;

        switch (deviceType) {
            case HEATER:
                return (currentValue < 24.0) ? 0.5 * influenceFactor : 0.0; // Только повышаем, если ниже максимума
            case AIR_CONDITIONER:
                return (currentValue > 20.0) ? -0.7 * influenceFactor : 0.0; // Только понижаем, если выше минимума
            case HUMIDIFIER:
                return (currentValue < 45.0) ? 1.5 * influenceFactor : 0.0; // Только повышаем, если ниже максимума
            case DEHUMIDIFIER:
                return (currentValue > 30.0) ? -1.5 * influenceFactor : 0.0; // Только понижаем, если выше минимума
            case VENTILATOR:
                return (currentValue > 400.0) ? -50.0 * influenceFactor : 0.0; // Только понижаем, если выше минимума
            default:
                return 0.0;
        }
    }

    private double applyComfortLimits(SensorType type, double value) {
        switch (type) {
            case TEMPERATURE:
                return Math.max(20.0, Math.min(24.0, value));
            case HUMIDITY:
                return Math.max(30.0, Math.min(45.0, value));
            case CO2:
                return Math.min(1000.0, Math.max(400.0, value));
            default:
                return value;
        }
    }

    private void updateSensorInDb(SensorType type, double value) {
        sensorsRepository.findByType(type).stream().findFirst().ifPresent(sensor -> {
            sensor.setValue(value);
            sensorsRepository.save(sensor);
        });
    }

    public double getSensorValue(SensorType type) {
        return environmentState.getOrDefault(type, 0.0);
    }
}