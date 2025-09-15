package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import com.avelina_anton.bzhch.smart_house.demo.repositories.DevicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutomationService {

    private final SensorsService sensorsService;
    private final DevicesRepository devicesRepository;

    @Autowired
    public AutomationService(SensorsService sensorsService, DevicesRepository devicesRepository) {
        this.sensorsService = sensorsService;
        this.devicesRepository = devicesRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void automateSmartHome() {
        Sensor temperatureSensor = sensorsService.getSensorByType(SensorType.TEMPERATURE).orElse(null);
        Sensor humiditySensor = sensorsService.getSensorByType(SensorType.HUMIDITY).orElse(null);
        Sensor co2Sensor = sensorsService.getSensorByType(SensorType.CO2).orElse(null);

        if (temperatureSensor != null) {
            checkAndControlTemperature(temperatureSensor.getValue());
        }
        if (humiditySensor != null) {
            checkAndControlHumidity(humiditySensor.getValue());
        }
        if (co2Sensor != null) {
            checkAndControlCo2(co2Sensor.getValue());
        }
    }

    private void checkAndControlTemperature(double temp) {
        List<Device> heaters = devicesRepository.findByTypeAndStatus(DeviceType.HEATER, null);
        List<Device> conditioners = devicesRepository.findByTypeAndStatus(DeviceType.AIR_CONDITIONER, null);

        if (temp < 20.0) {
            heaters.forEach(this::turnOnIfManualModeNotSet);
            conditioners.forEach(this::turnOffIfManualModeNotSet);
        } else if (temp > 24.0) {
            conditioners.forEach(this::turnOnIfManualModeNotSet);
            heaters.forEach(this::turnOffIfManualModeNotSet);
        } else {
            heaters.forEach(this::turnOffIfManualModeNotSet);
            conditioners.forEach(this::turnOffIfManualModeNotSet);
        }
    }

    private void checkAndControlHumidity(double humidity) {
        List<Device> humidifiers = devicesRepository.findByType(DeviceType.HUMIDIFIER);
        List<Device> dehumidifiers = devicesRepository.findByType(DeviceType.DEHUMIDIFIER);

        if (humidity < 30.0) {
            humidifiers.forEach(this::turnOnIfManualModeNotSet);
            dehumidifiers.forEach(this::turnOffIfManualModeNotSet);
        } else if (humidity > 45.0) {
            dehumidifiers.forEach(this::turnOnIfManualModeNotSet);
            humidifiers.forEach(this::turnOffIfManualModeNotSet);
        } else {
            humidifiers.forEach(this::turnOffIfManualModeNotSet);
            dehumidifiers.forEach(this::turnOffIfManualModeNotSet);
        }
    }

    private void checkAndControlCo2(double co2) {
        List<Device> ventilators = devicesRepository.findByType(DeviceType.VENTILATOR);

        if (co2 > 1000.0) {
            ventilators.forEach(this::turnOnIfManualModeNotSet);
        } else {
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
