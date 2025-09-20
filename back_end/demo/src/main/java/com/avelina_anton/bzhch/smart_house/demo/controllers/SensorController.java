// SensorController.java (обновленный с DTO)
package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.SensorDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/smart_house/sensors")
public class SensorController {
    private final SensorsService sensorsService;
    private final ModelMapper modelMapper;

    public SensorController(SensorsService sensorsService, ModelMapper modelMapper) {
        this.sensorsService = sensorsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<SensorDTO> getAllSensors() {
        return sensorsService.getAllSensors().stream()
                .map(sensor -> modelMapper.map(sensor, SensorDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable Long id) {
        return sensorsService.getSensorById(id)
                .map(sensor -> ResponseEntity.ok(modelMapper.map(sensor, SensorDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public List<SensorDTO> getSensorsByType(@PathVariable SensorType type) {
        return sensorsService.getSensorsByType(type).stream()
                .map(sensor -> modelMapper.map(sensor, SensorDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@Valid @RequestBody SensorDTO sensorDTO) {
        if (sensorDTO.getValue() < 0 || sensorDTO.getType() == null || sensorDTO.getLocation() == null) {
            return ResponseEntity.badRequest().build();
        }
        Sensor sensor = modelMapper.map(sensorDTO, Sensor.class);
        Sensor savedSensor = sensorsService.saveSensor(sensor);
        return ResponseEntity.status(201).body(modelMapper.map(savedSensor, SensorDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable Long id, @Valid @RequestBody SensorDTO sensorDTO) {
        if (sensorDTO.getValue() < 0 || sensorDTO.getType() == null || sensorDTO.getLocation() == null) {
            return ResponseEntity.badRequest().build();
        }
        return sensorsService.getSensorById(id)
                .map(existingSensor -> {
                    existingSensor.setType(sensorDTO.getType());
                    existingSensor.setValue(sensorDTO.getValue());
                    existingSensor.setLocation(sensorDTO.getLocation());
                    Sensor updatedSensor = sensorsService.saveSensor(existingSensor);
                    return ResponseEntity.ok(modelMapper.map(updatedSensor, SensorDTO.class));
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