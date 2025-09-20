
package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import com.avelina_anton.bzhch.smart_house.demo.repositories.DevicesRepository;
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

    public AutomationService(SensorsService sensorsService, DevicesService devicesService) {
        this.sensorsService = sensorsService;
        this.devicesService = devicesService;
    }

    @Scheduled(fixedRate = 10000)
    public void automateSmartHome() {
        logger.info("Запуск автоматизации умного дома...");

        List<Device> allDevices = devicesService.findAll();
        Map<DeviceType, List<Device>> devicesByType = allDevices.stream()
                .collect(Collectors.groupingBy(Device::getType));

        processTemperature(devicesByType);
        processHumidity(devicesByType);
        processCO2(devicesByType);
    }

    private void processTemperature(Map<DeviceType, List<Device>> devicesByType) {
        List<Sensor> temperatureSensors = sensorsService.getSensorsByType(SensorType.TEMPERATURE);
        if (temperatureSensors.isEmpty()) return;

        double avgTemp = temperatureSensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
        logger.info("Средняя температура: {}°C", avgTemp);

        List<Device> heaters = devicesByType.getOrDefault(DeviceType.HEATER, List.of());
        List<Device> conditioners = devicesByType.getOrDefault(DeviceType.AIR_CONDITIONER, List.of());

        if (avgTemp < 20.0) {
            logger.info("Температура низкая, включаем обогреватели");
            setDevicesStatus(heaters, DeviceStatus.ON);
            setDevicesStatus(conditioners, DeviceStatus.OFF);
        } else if (avgTemp > 24.0) {
            logger.info("Температура высокая, включаем кондиционеры");
            setDevicesStatus(conditioners, DeviceStatus.ON);
            setDevicesStatus(heaters, DeviceStatus.OFF);
        } else {
            logger.info("Температура нормальная, выключаем климатику");
            setDevicesStatus(heaters, DeviceStatus.OFF);
            setDevicesStatus(conditioners, DeviceStatus.OFF);
        }
    }

    private void processHumidity(Map<DeviceType, List<Device>> devicesByType) {
        List<Sensor> humiditySensors = sensorsService.getSensorsByType(SensorType.HUMIDITY);
        if (humiditySensors.isEmpty()) return;

        double avgHumidity = humiditySensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
        logger.info("Средняя влажность: {}%", avgHumidity);

        List<Device> humidifiers = devicesByType.getOrDefault(DeviceType.HUMIDIFIER, List.of());
        List<Device> dehumidifiers = devicesByType.getOrDefault(DeviceType.DEHUMIDIFIER, List.of());

        if (avgHumidity < 30.0) {
            logger.info("Влажность низкая, включаем увлажнители");
            setDevicesStatus(humidifiers, DeviceStatus.ON);
            setDevicesStatus(dehumidifiers, DeviceStatus.OFF);
        } else if (avgHumidity > 45.0) {
            logger.info("Влажность высокая, включаем осушители");
            setDevicesStatus(dehumidifiers, DeviceStatus.ON);
            setDevicesStatus(humidifiers, DeviceStatus.OFF);
        } else {
            logger.info("Влажность нормальная, выключаем оборудование");
            setDevicesStatus(humidifiers, DeviceStatus.OFF);
            setDevicesStatus(dehumidifiers, DeviceStatus.OFF);
        }
    }

    private void processCO2(Map<DeviceType, List<Device>> devicesByType) {
        List<Sensor> co2Sensors = sensorsService.getSensorsByType(SensorType.CO2);
        if (co2Sensors.isEmpty()) return;

        double avgCo2 = co2Sensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
        logger.info("Средний уровень CO2: {} ppm", avgCo2);

        List<Device> ventilators = devicesByType.getOrDefault(DeviceType.VENTILATOR, List.of());

        if (avgCo2 > 1000.0) {
            logger.info("CO2 высокий, включаем вентиляторы");
            setDevicesStatus(ventilators, DeviceStatus.ON);
        } else if (avgCo2 > 800.0) {
            logger.info("CO2 повышенный, регулируем вентиляцию");
            ventilators.forEach(device -> {
                if (device.getMode() == DeviceMode.AUTO) {
                    device.setPowerLevel(50);
                    device.setStatus(DeviceStatus.ON);
                    devicesService.save(device);
                }
            });
        } else {
            logger.info("CO2 нормальный, выключаем вентиляторы");
            setDevicesStatus(ventilators, DeviceStatus.OFF);
        }
    }

    private void setDevicesStatus(List<Device> devices, DeviceStatus status) {
        devices.forEach(device -> {
            if (device.getMode() == DeviceMode.AUTO) {
                device.setStatus(status);
                devicesService.save(device);
            }
        });
    }
}