package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AutomationService {
    private static final Logger logger = LoggerFactory.getLogger(AutomationService.class);
    private final SensorsService sensorsService;
    private final DevicesService devicesService;
    private final SmartHomeService smartHomeService;
    private final SimulationService simulationService;

    // Зоны комфорта
    private static final double TEMP_COMFORT_MIN = 20.0;
    private static final double TEMP_COMFORT_MAX = 24.0;
    private static final double HUMIDITY_COMFORT_MIN = 30.0;
    private static final double HUMIDITY_COMFORT_MAX = 45.0;
    private static final double CO2_COMFORT_MAX = 1000.0;

    public AutomationService(SensorsService sensorsService, DevicesService devicesService,
                             SmartHomeService smartHomeService, SimulationService simulationService) {
        this.sensorsService = sensorsService;
        this.devicesService = devicesService;
        this.smartHomeService = smartHomeService;
        this.simulationService = simulationService;
    }

    @Scheduled(fixedRate = 10000)
    public void runAutomationCycle() {
        List<SmartHome> smartHomes = smartHomeService.findAll();
        smartHomes.forEach(this::processSmartHome);
    }

    private void processSmartHome(SmartHome smartHome) {
        List<Device> allDevices = devicesService.findBySmartHome(smartHome);

        List<Device> autoDevices = allDevices.stream()
                .filter(device -> device.getMode() == DeviceMode.AUTO)
                .collect(Collectors.toList());

        if (autoDevices.isEmpty()) return;

        Map<DeviceType, List<Device>> devicesByType = autoDevices.stream()
                .collect(Collectors.groupingBy(Device::getType));

        List<Sensor> allSensors = sensorsService.getSensorsBySmartHome(smartHome);
        Map<SensorType, List<Sensor>> sensorsByType = allSensors.stream()
                .collect(Collectors.groupingBy(Sensor::getType));

        processTemperature(smartHome, sensorsByType.getOrDefault(SensorType.TEMPERATURE, List.of()), devicesByType);
        processHumidity(smartHome, sensorsByType.getOrDefault(SensorType.HUMIDITY, List.of()), devicesByType);
        processCO2(smartHome, sensorsByType.getOrDefault(SensorType.CO2, List.of()), devicesByType);
    }

    private void processTemperature(SmartHome smartHome, List<Sensor> sensors, Map<DeviceType, List<Device>> devicesByType) {
        if (sensors.isEmpty()) return;

        double avgTemp = sensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
        logger.info("Автоматизация {}: Температура {}°C", smartHome.getName(), String.format("%.2f", avgTemp));

        List<Device> heaters = devicesByType.getOrDefault(DeviceType.HEATER, List.of());
        List<Device> conditioners = devicesByType.getOrDefault(DeviceType.AIR_CONDITIONER, List.of());

        if (avgTemp >= TEMP_COMFORT_MIN && avgTemp <= TEMP_COMFORT_MAX) {
            setDevicesStatus(heaters, DeviceStatus.OFF, 0);
            setDevicesStatus(conditioners, DeviceStatus.OFF, 0);
            return;
        }

        if (avgTemp < TEMP_COMFORT_MIN) {
            setDevicesStatus(heaters, DeviceStatus.ON, calculatePowerLevel(avgTemp, TEMP_COMFORT_MIN, 100));
            setDevicesStatus(conditioners, DeviceStatus.OFF, 0);
        }
        else if (avgTemp > TEMP_COMFORT_MAX) {
            setDevicesStatus(heaters, DeviceStatus.OFF, 0);
            setDevicesStatus(conditioners, DeviceStatus.ON, calculatePowerLevel(avgTemp, TEMP_COMFORT_MAX, 100));
        }
    }

    private void processHumidity(SmartHome smartHome, List<Sensor> sensors, Map<DeviceType, List<Device>> devicesByType) {
        if (sensors.isEmpty()) return;

        double avgHumidity = sensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
        logger.info("Автоматизация {}: Влажность {}%", smartHome.getName(), String.format("%.2f", avgHumidity));

        List<Device> humidifiers = devicesByType.getOrDefault(DeviceType.HUMIDIFIER, List.of());
        List<Device> dehumidifiers = devicesByType.getOrDefault(DeviceType.DEHUMIDIFIER, List.of());

        if (avgHumidity >= HUMIDITY_COMFORT_MIN && avgHumidity <= HUMIDITY_COMFORT_MAX) {
            setDevicesStatus(humidifiers, DeviceStatus.OFF, 0);
            setDevicesStatus(dehumidifiers, DeviceStatus.OFF, 0);
            return;
        }

        if (avgHumidity < HUMIDITY_COMFORT_MIN) {
            setDevicesStatus(humidifiers, DeviceStatus.ON, calculatePowerLevel(avgHumidity, HUMIDITY_COMFORT_MIN, 100));
            setDevicesStatus(dehumidifiers, DeviceStatus.OFF, 0);
        }

        else if (avgHumidity > HUMIDITY_COMFORT_MAX) {
            setDevicesStatus(humidifiers, DeviceStatus.OFF, 0);
            setDevicesStatus(dehumidifiers, DeviceStatus.ON, calculatePowerLevel(avgHumidity, HUMIDITY_COMFORT_MAX, 100));
        }
    }

    private void processCO2(SmartHome smartHome, List<Sensor> sensors, Map<DeviceType, List<Device>> devicesByType) {
        if (sensors.isEmpty()) return;

        double avgCo2 = sensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
        logger.info("Автоматизация {}: CO2 {} ppm", smartHome.getName(), String.format("%.2f", avgCo2));

        List<Device> ventilators = devicesByType.getOrDefault(DeviceType.VENTILATOR, List.of());

        if (avgCo2 <= CO2_COMFORT_MAX) {
            setDevicesStatus(ventilators, DeviceStatus.OFF, 0);
            return;
        }

        if (avgCo2 > CO2_COMFORT_MAX) {
            setDevicesStatus(ventilators, DeviceStatus.ON, calculatePowerLevel(avgCo2, CO2_COMFORT_MAX, 100));
        }
    }

    private int calculatePowerLevel(double currentValue, double targetValue, int maxPower) {
        double difference = Math.abs(currentValue - targetValue);
        double powerRatio = Math.min(difference / 5.0, 1.0); // Нормализуем разницу
        return (int) (maxPower * powerRatio);
    }

    private void setDevicesStatus(List<Device> devices, DeviceStatus status, int powerLevel) {
        devices.forEach(device -> {
            try {
                device.setStatus(status);
                device.setPowerLevel(powerLevel);
                devicesService.save(device);
                logger.info("Авторежим: {} {} ({}%)",
                        device.getName(),
                        status == DeviceStatus.ON ? "включен" : "выключен",
                        powerLevel);
            } catch (Exception e) {
                logger.error("Ошибка автоуправления {}: {}", device.getName(), e.getMessage());
            }
        });
    }
}