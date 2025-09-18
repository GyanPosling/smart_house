package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.repositories.DevicesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DevicesService {

    private final DevicesRepository devicesRepository;

    @Autowired
    public DevicesService(DevicesRepository devicesRepository) {
        this.devicesRepository = devicesRepository;
    }

    public List<Device> findAll() {
        return devicesRepository.findAll();
    }

    public List<Device> findByUserId(Long userId) {
        return devicesRepository.findByUser_Id(userId);
    }

    public Device save(Device device) {
        if (device == null || device.getDeviceType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid device data");
        }
        return devicesRepository.save(device);
    }

    public Optional<Device> findById(Long id) {
        return devicesRepository.findById(id);
    }

    public Device turnOnDevice(Long deviceId) {
        Optional<Device> deviceOpt = devicesRepository.findById(deviceId);
        if (deviceOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found");
        }
        Device device = deviceOpt.get();
        if (device.equals(DeviceStatus.ON)) {
            return device; // Уже включено, ничего не делаем
        }
        device.setStatus(DeviceStatus.ON);
        device.setDeviceMode(DeviceMode.MANUAL);
        return devicesRepository.save(device);
    }
    public Device turnOffDevice(Long deviceId) {
        Optional<Device> deviceOpt = devicesRepository.findById(deviceId);
        if (deviceOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found");
        }
        Device device = deviceOpt.get();
        if (device.equals(DeviceStatus.OFF)) {
            return device;
        }

        device.setStatus(DeviceStatus.OFF);
        device.setDeviceMode(DeviceMode.MANUAL);
        return devicesRepository.save(device);
    }

    public Device setAutomationMode(Long deviceId) {
        Optional<Device> deviceOpt = devicesRepository.findById(deviceId);
        if (deviceOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found");
        }
        Device device = deviceOpt.get();
        device.setDeviceMode(DeviceMode.AUTO);
        return devicesRepository.save(device);
    }

    public void deleteDevice(Long deviceId) {
        if (!devicesRepository.existsById(deviceId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found");
        }
        devicesRepository.deleteById(deviceId);
    }
}