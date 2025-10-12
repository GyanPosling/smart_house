package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.repositories.DevicesRepository;
import com.avelina_anton.bzhch.smart_house.demo.utllis.DeviceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DevicesService {

    private final DevicesRepository deviceRepository;

    public DevicesService(DevicesRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> findBySmartHome(SmartHome smartHome) {
        return deviceRepository.findBySmartHome(smartHome);
    }

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public Device findById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Устройство с id " + id + " не найдено"));
    }

    public void deleteDevice(Long id) {
        Device device = findById(id);
        deviceRepository.delete(device);
    }

    public Device turnOnDevice(Long id) {
        Device device = findById(id);
        device.setStatus(DeviceStatus.ON);
        return deviceRepository.save(device);
    }

    public Device turnOffDevice(Long id) {
        Device device = findById(id);
        device.setStatus(DeviceStatus.OFF);
        return deviceRepository.save(device);
    }

    public Device setAutomationMode(Long id) {
        Device device = findById(id);
        device.setMode(DeviceMode.AUTO);
        return deviceRepository.save(device);
    }
}