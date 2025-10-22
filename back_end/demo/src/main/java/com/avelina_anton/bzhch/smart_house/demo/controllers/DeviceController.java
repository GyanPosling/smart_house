package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.DeviceDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.services.DevicesService;
import com.avelina_anton.bzhch.smart_house.demo.services.UsersService;
import com.avelina_anton.bzhch.smart_house.demo.utllis.DeviceNotFoundException;
import com.avelina_anton.bzhch.smart_house.demo.utllis.DeviceValidator;
import com.avelina_anton.bzhch.smart_house.demo.utllis.ErrorsUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/devices")
public class DeviceController {
    private final DevicesService devicesService;
    private final ModelMapper modelMapper;
    private final UsersService usersService;
    private final DeviceValidator deviceValidator;

    public DeviceController(DevicesService devicesService, ModelMapper modelMapper,
                            UsersService usersService, DeviceValidator deviceValidator) {
        this.devicesService = devicesService;
        this.modelMapper = modelMapper;
        this.usersService = usersService;
        this.deviceValidator = deviceValidator;
    }

    private User getUser(Long userId) {
        return usersService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + userId + " не найден"));
    }

    @GetMapping
    public List<DeviceDTO> getUserDevices(@PathVariable Long userId) {
        User user = getUser(userId);
        return devicesService.findByUser(user).stream()
                .map(device -> modelMapper.map(device, DeviceDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<DeviceDTO> addDeviceToUser(@PathVariable Long userId,
                                                     @Valid @RequestBody DeviceDTO deviceDTO,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        User user = getUser(userId);

        Device device = modelMapper.map(deviceDTO, Device.class);
        device.setUser(user);

        deviceValidator.validate(device, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        Device savedDevice = devicesService.save(device);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedDevice, DeviceDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable Long userId, @PathVariable Long id) {
        getUser(userId);

        Device device = devicesService.findById(id);

        if (!device.getUser().getId().equals(userId)) {
            throw new DeviceNotFoundException("Устройство не принадлежит пользователю с id " + userId);
        }

        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable Long userId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody DeviceDTO deviceDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        getUser(userId);

        Device existingDevice = devicesService.findById(id);

        if (!existingDevice.getUser().getId().equals(userId)) {
            throw new DeviceNotFoundException("Устройство не принадлежит пользователю с id " + userId);
        }

        Device device = modelMapper.map(deviceDTO, Device.class);
        device.setId(id);
        device.setUser(existingDevice.getUser());

        deviceValidator.validate(device, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnErrorsToClient(bindingResult);
        }

        Device updatedDevice = devicesService.save(device);
        return ResponseEntity.ok(modelMapper.map(updatedDevice, DeviceDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long userId, @PathVariable Long id) {
        getUser(userId);

        Device device = devicesService.findById(id);

        if (!device.getUser().getId().equals(userId)) {
            throw new DeviceNotFoundException("Устройство не принадлежит пользователю с id " + userId);
        }

        devicesService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/on")
    public ResponseEntity<DeviceDTO> turnOnDevice(@PathVariable Long userId, @PathVariable Long id) {
        getUser(userId);
        Device device = devicesService.turnOnDevice(id);
        if (!device.getUser().getId().equals(userId)) {
            throw new DeviceNotFoundException("Устройство не принадлежит пользователю с id " + userId);
        }
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }

    @PatchMapping("/{id}/off")
    public ResponseEntity<DeviceDTO> turnOffDevice(@PathVariable Long userId, @PathVariable Long id) {
        getUser(userId);
        Device device = devicesService.turnOffDevice(id);
        if (!device.getUser().getId().equals(userId)) {
            throw new DeviceNotFoundException("Устройство не принадлежит пользователю с id " + userId);
        }
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }

    @PatchMapping("/{id}/auto")
    public ResponseEntity<DeviceDTO> setAutoMode(@PathVariable Long userId, @PathVariable Long id) {
        getUser(userId);
        Device device = devicesService.setAutomationMode(id);
        if (!device.getUser().getId().equals(userId)) {
            throw new DeviceNotFoundException("Устройство не принадлежит пользователю с id " + userId);
        }
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }
}