package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorsService {

    private final SensorsRepository sensorsRepository;

    @Autowired
    public SensorsService(SensorsRepository sensorsRepository) {
        this.sensorsRepository = sensorsRepository;
    }

    public Optional<Sensor> getSensorByType(SensorType type) {
        return sensorsRepository.findByType(type).stream().findFirst();
    }

    public List<Sensor> getSensorsByType(SensorType type) {
        return sensorsRepository.findByType(type);
    }

    public List<Sensor> getAllSensors() {
        return sensorsRepository.findAll();
    }

    public Sensor saveSensor(Sensor sensor) {
        return sensorsRepository.save(sensor);
    }

    public Optional<Sensor> getSensorById(Long id) {
        return sensorsRepository.findById(id);
    }

    public void deleteSensor(Long id) {
        sensorsRepository.deleteById(id);
    }
}