package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.repositories.DevicesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DevicesService {

    private final DevicesRepository devicesRepository;

    @Autowired
    public DevicesService(DevicesRepository devicesRepository) {
        this.devicesRepository = devicesRepository;
    }

    public Device turnOnDevice(Long deviceId) {
        Device device = devicesRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setStatus(DeviceStatus.ON);
        return devicesRepository.save(device);
    }

    public Device turnOffDevice(Long deviceId) {
        Device device = devicesRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setStatus(DeviceStatus.OFF);
        return devicesRepository.save(device);
    }

    // Здесь нужно будет добавить логику для ручного/автоматического режимов
}