package com.avelina_anton.bzhch.smart_house.demo.utllis;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SensorValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Sensor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Sensor sensor = (Sensor) target;

        if (sensor.getType() == null) {
            errors.rejectValue("type", "Тип датчика обязателен");
        }

        if (sensor.getLocation() == null || sensor.getLocation().trim().isEmpty()) {
            errors.rejectValue("location", "Локация датчика обязательна");
        } else if (sensor.getLocation().length() < 2 || sensor.getLocation().length() > 100) {
            errors.rejectValue("location", "Локация должна быть от 2 до 100 символов");
        }

        // Валидация значений в зависимости от типа датчика
        if (sensor.getType() != null) {
            switch (sensor.getType()) {
                case TEMPERATURE:
                    if (sensor.getValue() < -50 || sensor.getValue() > 60) {
                        errors.rejectValue("value", "Температура должна быть в диапазоне -50°C до 60°C");
                    }
                    break;
                case HUMIDITY:
                    if (sensor.getValue() < 0 || sensor.getValue() > 100) {
                        errors.rejectValue("value", "Влажность должна быть в диапазоне 0-100%");
                    }
                    break;
                case CO2:
                    if (sensor.getValue() < 0 || sensor.getValue() > 5000) {
                        errors.rejectValue("value", "Уровень CO2 должен быть в диапазоне 0-5000 ppm");
                    }
                    break;
                case NOISE:
                    if (sensor.getValue() < 0 || sensor.getValue() > 150) {
                        errors.rejectValue("value", "Уровень шума должен быть в диапазоне 0-150 дБ");
                    }
                    break;
            }
        }
    }
}
