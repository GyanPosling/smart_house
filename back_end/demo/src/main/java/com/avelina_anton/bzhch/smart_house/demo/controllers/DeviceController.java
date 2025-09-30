package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.DeviceDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.services.DevicesService;
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
@RequestMapping("/smarthome/{smartHomeId}/devices")
public class DeviceController {
    private final DevicesService devicesService;
    private final ModelMapper modelMapper;
    private final SmartHomeService smartHomeService;

    public DeviceController(DevicesService devicesService, ModelMapper modelMapper, SmartHomeService smartHomeService) {
        this.devicesService = devicesService;
        this.modelMapper = modelMapper;
        this.smartHomeService = smartHomeService;
    }

    private SmartHome getSmartHome(Long smartHomeId) {
        return smartHomeService.findById(smartHomeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Умный дом не найден"));
    }

    @GetMapping
    public List<DeviceDTO> getSmartHomeDevices(@PathVariable Long smartHomeId) {
        SmartHome smartHome = getSmartHome(smartHomeId);
        return devicesService.findBySmartHome(smartHome).stream()
                .map(device -> modelMapper.map(device, DeviceDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<DeviceDTO> addDeviceToSmartHome(@PathVariable Long smartHomeId, @Valid @RequestBody DeviceDTO deviceDTO) {
        if (deviceDTO.getType() == null || deviceDTO.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тип и имя устройства обязательны");
        }

        SmartHome smartHome = getSmartHome(smartHomeId);

        Device device = modelMapper.map(deviceDTO, Device.class);

        // Установка связи с умным домом
        device.setSmartHome(smartHome);

        // Удаляем установку User, если она есть в DTO, так как устройство привязано к дому
        // device.setUser(smartHome.getUser());

        Device savedDevice = devicesService.save(device);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedDevice, DeviceDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable Long smartHomeId, @PathVariable Long id) {
        getSmartHome(smartHomeId); // Проверяем, существует ли дом

        return devicesService.findById(id)
                .map(device -> {
                    if (!device.getSmartHome().getId().equals(smartHomeId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Устройство не принадлежит этому умному дому");
                    }
                    return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство не найдено"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable Long smartHomeId, @PathVariable Long id, @Valid @RequestBody DeviceDTO deviceDTO) {
        getSmartHome(smartHomeId);

        return devicesService.findById(id)
                .map(existingDevice -> {
                    if (!existingDevice.getSmartHome().getId().equals(smartHomeId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Устройство не принадлежит этому умному дому");
                    }

                    Device device = modelMapper.map(deviceDTO, Device.class);
                    device.setId(id);
                    device.setSmartHome(existingDevice.getSmartHome()); // Сохраняем привязку

                    Device updatedDevice = devicesService.save(device);
                    return ResponseEntity.ok(modelMapper.map(updatedDevice, DeviceDTO.class));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство не найдено"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long smartHomeId, @PathVariable Long id) {
        getSmartHome(smartHomeId);

        Device device = devicesService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство не найдено"));

        if (!device.getSmartHome().getId().equals(smartHomeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Устройство не принадлежит этому умному дому");
        }

        devicesService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    // --- Управление статусом (для фронтенда) ---

    @PatchMapping("/{id}/on")
    public ResponseEntity<DeviceDTO> turnOnDevice(@PathVariable Long smartHomeId, @PathVariable Long id) {
        getSmartHome(smartHomeId);
        Device device = devicesService.turnOnDevice(id);
        if (!device.getSmartHome().getId().equals(smartHomeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Устройство не принадлежит этому умному дому");
        }
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }

    @PatchMapping("/{id}/off")
    public ResponseEntity<DeviceDTO> turnOffDevice(@PathVariable Long smartHomeId, @PathVariable Long id) {
        getSmartHome(smartHomeId);
        Device device = devicesService.turnOffDevice(id);
        if (!device.getSmartHome().getId().equals(smartHomeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Устройство не принадлежит этому умному дому");
        }
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }

    @PatchMapping("/{id}/auto")
    public ResponseEntity<DeviceDTO> setAutoMode(@PathVariable Long smartHomeId, @PathVariable Long id) {
        getSmartHome(smartHomeId);
        Device device = devicesService.setAutomationMode(id);
        if (!device.getSmartHome().getId().equals(smartHomeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Устройство не принадлежит этому умному дому");
        }
        return ResponseEntity.ok(modelMapper.map(device, DeviceDTO.class));
    }
}