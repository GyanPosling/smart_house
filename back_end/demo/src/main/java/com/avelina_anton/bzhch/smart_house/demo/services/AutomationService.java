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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutomationService {

    private static final Logger logger = LoggerFactory.getLogger(AutomationService.class);
    private final SensorsService sensorsService;
    private final DevicesRepository devicesRepository;

    @Autowired
    public AutomationService(SensorsService sensorsService, DevicesRepository devicesRepository) {
        this.sensorsService = sensorsService;
        this.devicesRepository = devicesRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void automateSmartHome() {
        List<Sensor> temperatureSensors = sensorsService.getSensorsByType(SensorType.TEMPERATURE);
        List<Sensor> humiditySensors = sensorsService.getSensorsByType(SensorType.HUMIDITY);
        List<Sensor> co2Sensors = sensorsService.getSensorsByType(SensorType.CO2);

        if (!temperatureSensors.isEmpty()) {
            double avgTemp = temperatureSensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
            logger.info("Средняя температура: {}", avgTemp);
            checkAndControlTemperature(avgTemp);
        }
        if (!humiditySensors.isEmpty()) {
            double avgHumidity = humiditySensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
            logger.info("Средняя влажность: {}", avgHumidity);
            checkAndControlHumidity(avgHumidity);
        }
        if (!co2Sensors.isEmpty()) {
            double avgCo2 = co2Sensors.stream().mapToDouble(Sensor::getValue).average().orElse(0.0);
            logger.info("Средний уровень CO2: {}", avgCo2);
            checkAndControlCo2(avgCo2);
        }
    }

    private void checkAndControlTemperature(double temp) {
        List<Device> heaters = devicesRepository.findByType(DeviceType.HEATER);
        List<Device> conditioners = devicesRepository.findByType(DeviceType.AIR_CONDITIONER);

        if (temp < 20.0) {
            logger.info("Температура ниже 20°C, включаем обогреватели");
            heaters.forEach(this::turnOnIfManualModeNotSet);
            conditioners.forEach(this::turnOffIfManualModeNotSet);
        } else if (temp > 24.0) {
            logger.info("Температура выше 24°C, включаем кондиционеры");
            conditioners.forEach(this::turnOnIfManualModeNotSet);
            heaters.forEach(this::turnOffIfManualModeNotSet);
        } else {
            logger.info("Температура в норме, выключаем обогреватели и кондиционеры");
            heaters.forEach(this::turnOffIfManualModeNotSet);
            conditioners.forEach(this::turnOffIfManualModeNotSet);
        }
    }

    private void checkAndControlHumidity(double humidity) {
        List<Device> humidifiers = devicesRepository.findByType(DeviceType.HUMIDIFIER);
        List<Device> dehumidifiers = devicesRepository.findByType(DeviceType.DEHUMIDIFIER);

        if (humidity < 30.0) {
            logger.info("Влажность ниже 30%, включаем увлажнители");
            humidifiers.forEach(this::turnOnIfManualModeNotSet);
            dehumidifiers.forEach(this::turnOffIfManualModeNotSet);
        } else if (humidity > 45.0) {
            logger.info("Влажность выше 45%, включаем осушители");
            dehumidifiers.forEach(this::turnOnIfManualModeNotSet);
            humidifiers.forEach(this::turnOffIfManualModeNotSet);
        } else {
            logger.info("Влажность в норме, выключаем увлажнители и осушители");
            humidifiers.forEach(this::turnOffIfManualModeNotSet);
            dehumidifiers.forEach(this::turnOffIfManualModeNotSet);
        }
    }

    private void checkAndControlCo2(double co2) {
        List<Device> ventilators = devicesRepository.findByType(DeviceType.VENTILATOR);

        if (co2 > 1000.0) {
            logger.info("CO2 выше 1000 ppm, включаем вентиляторы");
            ventilators.forEach(this::turnOnIfManualModeNotSet);
        } else if (co2 > 800.0) {
            logger.info("CO2 между 800-1000 ppm, частичная вентиляция");
            ventilators.forEach(device -> {
                if (device.getMode() == null || device.getMode() == DeviceMode.AUTO) {
                    device.setStatus(DeviceStatus.ON);
                    devicesRepository.save(device);
                }
            });
        } else {
            logger.info("CO2 в норме, выключаем вентиляторы");
            ventilators.forEach(this::turnOffIfManualModeNotSet);
        }
    }

    private void turnOnIfManualModeNotSet(Device device) {
        if (device.getMode() == null || device.getMode() == DeviceMode.AUTO) {
            device.setStatus(DeviceStatus.ON);
            devicesRepository.save(device);
        }
    }

    private void turnOffIfManualModeNotSet(Device device) {
        if (device.getMode() == null || device.getMode() == DeviceMode.AUTO) {
            device.setStatus(DeviceStatus.OFF);
            devicesRepository.save(device);
        }
    }
}