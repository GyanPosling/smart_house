package com.avelina_anton.bzhch.smart_house.demo.utllis;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DeviceValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Device.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Device device = (Device) target;

        if (device.getName() == null || device.getName().trim().isEmpty()) {
            errors.rejectValue("name", "Имя устройства обязательно");
        } else if (device.getName().length() < 2 || device.getName().length() > 100) {
            errors.rejectValue("name", "Имя устройства должно быть от 2 до 100 символов");
        }

        if (device.getType() == null) {
            errors.rejectValue("type", "Тип устройства обязателен");
        }

        if (device.getPowerLevel() < 0 || device.getPowerLevel() > 100) {
            errors.rejectValue("powerLevel", "Уровень мощности должен быть от 0 до 100");
        }

        // Валидация температурных параметров для соответствующих устройств
        if (device.supportsTemperatureControl()) {
            if (device.getTargetTemperature() != null) {
                if (device.getTargetTemperature() < 10 || device.getTargetTemperature() > 35) {
                    errors.rejectValue("targetTemperature", "Целевая температура должна быть в диапазоне 10-35°C");
                }
            }
        }

        // Валидация параметров влажности для соответствующих устройств
        if (device.supportsHumidityControl()) {
            if (device.getTargetHumidity() != null) {
                if (device.getTargetHumidity() < 20 || device.getTargetHumidity() > 80) {
                    errors.rejectValue("targetHumidity", "Целевая влажность должна быть в диапазоне 20-80%");
                }
            }
        }

        // Проверка на корректность текущих показаний
        if (device.getCurrentTemperature() != null && (device.getCurrentTemperature() < -50 || device.getCurrentTemperature() > 60)) {
            errors.rejectValue("currentTemperature", "Текущая температура вне допустимого диапазона");
        }

        if (device.getCurrentHumidity() != null && (device.getCurrentHumidity() < 0 || device.getCurrentHumidity() > 100)) {
            errors.rejectValue("currentHumidity", "Текущая влажность должна быть в диапазоне 0-100%");
        }
    }
}
