// DeviceController.java
package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.DeviceDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.services.DevicesService;
import com.avelina_anton.bzhch.smart_house.demo.services.UsersService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/smart_house/devices")
public class DeviceController {
    private final DevicesService devicesService;
    private final ModelMapper modelMapper;
    private final UsersService usersService;

    public DeviceController(DevicesService devicesService, ModelMapper modelMapper, UsersService usersService) {
        this.devicesService = devicesService;
        this.modelMapper = modelMapper;
        this.usersService = usersService;
    }

    @GetMapping
    public List<DeviceDTO> getAllDevices() {
        return devicesService.findAll().stream()
                .map(device -> modelMapper.map(device, DeviceDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}")
    public List<DeviceDTO> getUserDevices(@PathVariable Long userId) {
        if (userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID пользователя");
        }
        return devicesService.findByUserId(userId).stream()
                .map(device -> modelMapper.map(device, DeviceDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<DeviceDTO> addDevice(@Valid @RequestBody DeviceDTO deviceDTO) {
        if (deviceDTO.getType() == null || deviceDTO.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тип и имя устройства обязательны");
        }
        Device device = modelMapper.map(deviceDTO, Device.class);
        Device savedDevice = devicesService.save(device);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedDevice, DeviceDTO.class));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<DeviceDTO> addUserDevice(@PathVariable Long userId, @Valid @RequestBody DeviceDTO deviceDTO) {
        if (deviceDTO.getType() == null || deviceDTO.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тип и имя устройства обязательны");
        }

        User user = usersService.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        Device device = modelMapper.map(deviceDTO, Device.class);
        device.setUser(user);

        Device savedDevice = devicesService.save(device);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedDevice, DeviceDTO.class));
    }


    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID устройства");
        }
        return devicesService.findById(id)
                .map(device -> ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство не найдено"));
    }

    @PatchMapping("/{id}/on")
    public ResponseEntity<DeviceDTO> turnOnDevice(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID устройства");
        }
        Device device = devicesService.turnOnDevice(id);
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }

    @PatchMapping("/{id}/off")
    public ResponseEntity<DeviceDTO> turnOffDevice(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID устройства");
        }
        Device device = devicesService.turnOffDevice(id);
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }

    @PatchMapping("/{id}/auto")
    public ResponseEntity<DeviceDTO> setAutoMode(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID устройства");
        }
        Device device = devicesService.setAutomationMode(id);
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceDTO deviceDTO) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID устройства");
        }
        if (deviceDTO.getType() == null || deviceDTO.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тип и имя устройства обязательны");
        }
        return devicesService.findById(id)
                .map(existingDevice -> {
                    Device device = modelMapper.map(deviceDTO, Device.class);
                    device.setId(id);
                    Device updatedDevice = devicesService.save(device);
                    return ResponseEntity.ok(modelMapper.map(updatedDevice, DeviceDTO.class));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство не найдено"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID устройства");
        }
        devicesService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}