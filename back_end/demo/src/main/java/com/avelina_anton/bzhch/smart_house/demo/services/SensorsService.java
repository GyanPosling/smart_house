package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
import com.avelina_anton.bzhch.smart_house.demo.utllis.SensorNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorsService {

    private final SensorsRepository sensorRepository;

    public SensorsService(SensorsRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public List<Sensor> getSensorsBySmartHome(SmartHome smartHome) {
        return sensorRepository.findBySmartHome(smartHome);
    }

    public Sensor getSensorById(Long id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new SensorNotFoundException("Датчик с id " + id + " не найден"));
    }

    public List<Sensor> getSensorsBySmartHomeAndType(SmartHome smartHome, SensorType type) {
        return sensorRepository.findBySmartHomeAndType(smartHome, type);
    }

    public Sensor saveSensor(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public void deleteSensor(Long id) {
        Sensor sensor = getSensorById(id);
        sensorRepository.delete(sensor);
    }
}