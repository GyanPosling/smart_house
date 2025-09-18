package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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
        return sensorsService.getSensorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/type/{type}")
    public List<Sensor> getSensorsByType(@PathVariable SensorType type) {
        return sensorsService.getSensorsByType(type);
    }

    @PostMapping
    public ResponseEntity<Sensor> createSensor(@Valid @RequestBody Sensor sensor) {
        if (sensor.getValue() < 0 || sensor.getType() == null || sensor.getLocation() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).body(sensorsService.saveSensor(sensor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sensor> updateSensor(@PathVariable Long id, @Valid @RequestBody Sensor sensorDetails) {
        if (sensorDetails.getValue() < 0 || sensorDetails.getType() == null || sensorDetails.getLocation() == null) {
            return ResponseEntity.badRequest().build();
        }
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
            sensorsService.deleteSensor(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}