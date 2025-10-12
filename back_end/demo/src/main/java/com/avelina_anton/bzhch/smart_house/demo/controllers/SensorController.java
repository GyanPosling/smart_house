package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.SensorDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import com.avelina_anton.bzhch.smart_house.demo.services.SmartHomeService;
import com.avelina_anton.bzhch.smart_house.demo.utllis.ErrorsUtil;
import com.avelina_anton.bzhch.smart_house.demo.utllis.SensorNotFoundException;
import com.avelina_anton.bzhch.smart_house.demo.utllis.SensorValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/smarthome/{smartHomeId}/sensors")
public class SensorController {
    private final SensorsService sensorsService;
    private final ModelMapper modelMapper;
    private final SmartHomeService smartHomeService;
    private final SensorValidator sensorValidator;

    public SensorController(SensorsService sensorsService, ModelMapper modelMapper,
                            SmartHomeService smartHomeService, SensorValidator sensorValidator) {
        this.sensorsService = sensorsService;
        this.modelMapper = modelMapper;
        this.smartHomeService = smartHomeService;
        this.sensorValidator = sensorValidator;
    }

    private SmartHome getSmartHome(Long smartHomeId) {
        return smartHomeService.findById(smartHomeId);
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

        Sensor sensor = sensorsService.getSensorById(id);

        if (!sensor.getSmartHome().getId().equals(smartHomeId)) {
            throw new SensorNotFoundException("Датчик не принадлежит умному дому с id " + smartHomeId);
        }

        return ResponseEntity.ok(modelMapper.map(sensor, SensorDTO.class));
    }

    @GetMapping("/type/{type}")
    public List<SensorDTO> getSensorsByType(@PathVariable Long smartHomeId, @PathVariable SensorType type) {
        SmartHome smartHome = getSmartHome(smartHomeId);
        return sensorsService.getSensorsBySmartHomeAndType(smartHome, type).stream()
                .map(sensor -> modelMapper.map(sensor, SensorDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@PathVariable Long smartHomeId,
                                                  @Valid @RequestBody SensorDTO sensorDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        SmartHome smartHome = getSmartHome(smartHomeId);
        Sensor sensor = modelMapper.map(sensorDTO, Sensor.class);
        sensor.setSmartHome(smartHome);

        sensorValidator.validate(sensor, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        Sensor savedSensor = sensorsService.saveSensor(sensor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedSensor, SensorDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable Long smartHomeId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody SensorDTO sensorDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        getSmartHome(smartHomeId);

        Sensor existingSensor = sensorsService.getSensorById(id);

        if (!existingSensor.getSmartHome().getId().equals(smartHomeId)) {
            throw new SensorNotFoundException("Датчик не принадлежит умному дому с id " + smartHomeId);
        }

        existingSensor.setType(sensorDTO.getType());
        existingSensor.setValue(sensorDTO.getValue());
        existingSensor.setLocation(sensorDTO.getLocation());

        sensorValidator.validate(existingSensor, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        Sensor updatedSensor = sensorsService.saveSensor(existingSensor);
        return ResponseEntity.ok(modelMapper.map(updatedSensor, SensorDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable Long smartHomeId, @PathVariable Long id) {
        getSmartHome(smartHomeId);

        Sensor sensor = sensorsService.getSensorById(id);

        if (!sensor.getSmartHome().getId().equals(smartHomeId)) {
            throw new SensorNotFoundException("Датчик не принадлежит умному дому с id " + smartHomeId);
        }

        sensorsService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}