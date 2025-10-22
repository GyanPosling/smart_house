package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.SensorDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import com.avelina_anton.bzhch.smart_house.demo.services.UsersService;
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
@RequestMapping("/users/{userId}/sensors")
public class SensorController {
    private final SensorsService sensorsService;
    private final ModelMapper modelMapper;
    private final UsersService usersService;
    private final SensorValidator sensorValidator;

    public SensorController(SensorsService sensorsService, ModelMapper modelMapper,
                            UsersService usersService, SensorValidator sensorValidator) {
        this.sensorsService = sensorsService;
        this.modelMapper = modelMapper;
        this.usersService = usersService;
        this.sensorValidator = sensorValidator;
    }

    private User getUser(Long userId) {
        return usersService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + userId + " не найден"));
    }

    @GetMapping
    public List<SensorDTO> getUserSensors(@PathVariable Long userId) {
        User user = getUser(userId);
        return sensorsService.getSensorsByUser(user).stream()
                .map(sensor -> modelMapper.map(sensor, SensorDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable Long userId, @PathVariable Long id) {
        getUser(userId);

        Sensor sensor = sensorsService.getSensorById(id);

        if (!sensor.getUser().getId().equals(userId)) {
            throw new SensorNotFoundException("Датчик не принадлежит пользователю с id " + userId);
        }

        return ResponseEntity.ok(modelMapper.map(sensor, SensorDTO.class));
    }

    @GetMapping("/type/{type}")
    public List<SensorDTO> getSensorsByType(@PathVariable Long userId, @PathVariable SensorType type) {
        User user = getUser(userId);
        return sensorsService.getSensorsByUserAndType(user, type).stream()
                .map(sensor -> modelMapper.map(sensor, SensorDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@PathVariable Long userId,
                                                  @Valid @RequestBody SensorDTO sensorDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        User user = getUser(userId);
        Sensor sensor = modelMapper.map(sensorDTO, Sensor.class);
        sensor.setUser(user);

        sensorValidator.validate(sensor, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        Sensor savedSensor = sensorsService.saveSensor(sensor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedSensor, SensorDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable Long userId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody SensorDTO sensorDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        getUser(userId);

        Sensor existingSensor = sensorsService.getSensorById(id);

        if (!existingSensor.getUser().getId().equals(userId)) {
            throw new SensorNotFoundException("Датчик не принадлежит пользователю с id " + userId);
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
    public ResponseEntity<Void> deleteSensor(@PathVariable Long userId, @PathVariable Long id) {
        getUser(userId);

        Sensor sensor = sensorsService.getSensorById(id);

        if (!sensor.getUser().getId().equals(userId)) {
            throw new SensorNotFoundException("Датчик не принадлежит пользователю с id " + userId);
        }

        sensorsService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}