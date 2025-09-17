package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.repositories.DevicesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DevicesService {

    private final DevicesRepository devicesRepository;
    private final AutomationService automationService;

    @Autowired
    public DevicesService(DevicesRepository devicesRepository, AutomationService automationService) {
        this.devicesRepository = devicesRepository;
        this.automationService = automationService;
    }

    public List<Device> findAll() {
        return devicesRepository.findAll();
    }

    public List<Device> findByUserId(Long userId) {
        return devicesRepository.findByUser_Id(userId);
    }

    public Device save(Device device) {
        return devicesRepository.save(device);
    }

    public Optional<Device> findById(Long id) {
        return devicesRepository.findById(id);
    }

    public Device turnOnDevice(Long deviceId) {
        Device device = devicesRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setStatus(DeviceStatus.ON);
        device.setDeviceMode(DeviceMode.MANUAL); // При ручном управлении переключаем в MANUAL
        return devicesRepository.save(device);
    }

    public Device turnOffDevice(Long deviceId) {
        Device device = devicesRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setStatus(DeviceStatus.OFF);
        device.setDeviceMode(DeviceMode.MANUAL); // При ручном управлении переключаем в MANUAL
        return devicesRepository.save(device);
    }

    public Device setAutomationMode(Long deviceId) {
        Device device = devicesRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setDeviceMode(DeviceMode.AUTO);
        return devicesRepository.save(device);
    }

    public void deleteDevice(Long deviceId) {
        devicesRepository.deleteById(deviceId);
    }
}