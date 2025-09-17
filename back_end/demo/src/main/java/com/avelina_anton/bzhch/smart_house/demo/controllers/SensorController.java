package com.avelina_anton.bzhch.smart_house.demo.controllers;


import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/smart_house/sensors")
public class SensorController {

    private final SensorsService sensorsService;

    public SensorController(SensorsService sensorsService) {
        this.sensorsService = sensorsService;
    }

    @GetMapping
    public List<Sensor> getAllSensors() {
        return sensorsService.getAllSensors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sensor> getSensorById(@PathVariable Long id) {
        Optional<Sensor> sensor = sensorsService.getSensorById(id);
        return sensor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Sensor> getSensorByType(@PathVariable SensorType type) {
        Optional<Sensor> sensor = sensorsService.getSensorByType(type);
        return sensor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Sensor createSensor(@RequestBody Sensor sensor) {
        return sensorsService.saveSensor(sensor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sensor> updateSensor(@PathVariable Long id, @RequestBody Sensor sensorDetails) {
        return sensorsService.getSensorById(id)
                .map(existingSensor -> {
                    existingSensor.setType(sensorDetails.getType());
                    existingSensor.setValue(sensorDetails.getValue());
                    existingSensor.setLocation(sensorDetails.getLocation());
                    return ResponseEntity.ok(sensorsService.saveSensor(existingSensor));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        if (sensorsService.getSensorById(id).isPresent()) {
            sensorsService.getSensorById(id); // В реальности нужно добавить метод delete
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
