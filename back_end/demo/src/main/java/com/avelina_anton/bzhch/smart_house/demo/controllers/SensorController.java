package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.SensorDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import com.avelina_anton.bzhch.smart_house.demo.services.SmartHomeService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/smarthome/{smartHomeId}/sensors")
public class SensorController {
    private final SensorsService sensorsService;
    private final ModelMapper modelMapper;
    private final SmartHomeService smartHomeService;

    public SensorController(SensorsService sensorsService, ModelMapper modelMapper, SmartHomeService smartHomeService) {
        this.sensorsService = sensorsService;
        this.modelMapper = modelMapper;
        this.smartHomeService = smartHomeService;
    }

    private SmartHome getSmartHome(Long smartHomeId) {
        return smartHomeService.findById(smartHomeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Умный дом не найден"));
    }

    @GetMapping
    public List<SensorDTO> getSmartHomeSensors(@PathVariable Long smartHomeId) {
        SmartHome smartHome = getSmartHome(smartHomeId);
        return sensorsService.getSensorsBySmartHome(smartHome).stream()
                .map(sensor -> modelMapper.map(sensor, SensorDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable Long smartHomeId, @PathVariable Long id) {
        getSmartHome(smartHomeId);

        return sensorsService.getSensorById(id)
                .map(sensor -> {
                    if (!sensor.getSmartHome().getId().equals(smartHomeId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Датчик не принадлежит этому умному дому");
                    }
                    return ResponseEntity.ok(modelMapper.map(sensor, SensorDTO.class));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public List<SensorDTO> getSensorsByType(@PathVariable Long smartHomeId, @PathVariable SensorType type) {
        SmartHome smartHome = getSmartHome(smartHomeId);
        // Предполагается, что в SensorsService есть метод findBySmartHomeAndType
        return sensorsService.getSensorsBySmartHomeAndType(smartHome, type).stream()
                .map(sensor -> modelMapper.map(sensor, SensorDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@PathVariable Long smartHomeId, @Valid @RequestBody SensorDTO sensorDTO) {
        if (sensorDTO.getValue() < 0 || sensorDTO.getType() == null || sensorDTO.getLocation() == null) {
            return ResponseEntity.badRequest().build();
        }

        SmartHome smartHome = getSmartHome(smartHomeId);
        Sensor sensor = modelMapper.map(sensorDTO, Sensor.class);
        sensor.setSmartHome(smartHome); // Установка связи

        Sensor savedSensor = sensorsService.saveSensor(sensor);
        return ResponseEntity.status(201).body(modelMapper.map(savedSensor, SensorDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable Long smartHomeId, @PathVariable Long id, @Valid @RequestBody SensorDTO sensorDTO) {
        getSmartHome(smartHomeId);

        if (sensorDTO.getValue() < 0 || sensorDTO.getType() == null || sensorDTO.getLocation() == null) {
            return ResponseEntity.badRequest().build();
        }

        return sensorsService.getSensorById(id)
                .map(existingSensor -> {
                    if (!existingSensor.getSmartHome().getId().equals(smartHomeId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Датчик не принадлежит этому умному дому");
                    }

                    existingSensor.setType(sensorDTO.getType());
                    existingSensor.setValue(sensorDTO.getValue());
                    existingSensor.setLocation(sensorDTO.getLocation());

                    Sensor updatedSensor = sensorsService.saveSensor(existingSensor);
                    return ResponseEntity.ok(modelMapper.map(updatedSensor, SensorDTO.class));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSensor(@PathVariable Long smartHomeId, @PathVariable Long id) {
        getSmartHome(smartHomeId);

        return sensorsService.getSensorById(id)
                .map(sensor -> {
                    if (!sensor.getSmartHome().getId().equals(smartHomeId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Датчик не принадлежит этому умному дому");
                    }
                    sensorsService.deleteSensor(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}