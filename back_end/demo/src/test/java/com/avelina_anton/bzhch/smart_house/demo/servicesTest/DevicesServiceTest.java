package com.avelina_anton.bzhch.smart_house.demo.servicesTest;

import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import com.avelina_anton.bzhch.smart_house.demo.repositories.DevicesRepository;
import com.avelina_anton.bzhch.smart_house.demo.services.DevicesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DevicesServiceTest {

    @Mock
    private DevicesRepository devicesRepository;

    @InjectMocks
    private DevicesService devicesService;

    @Test
    void findAll_ShouldReturnAllDevices() {
        Device device1 = new Device();
        device1.setId(1L);
        device1.setType(DeviceType.HEATER);
        Device device2 = new Device();
        device2.setId(2L);
        device2.setType(DeviceType.VENTILATOR);

        when(devicesRepository.findAll()).thenReturn(Arrays.asList(device1, device2));

        List<Device> devices = devicesService.findAll();

        assertEquals(2, devices.size());
        verify(devicesRepository, times(1)).findAll();
    }

    @Test
    void findByUserId_ShouldReturnDevicesForUser() {
        Device device = new Device();
        device.setId(1L);
        device.setType(DeviceType.HEATER);

        when(devicesRepository.findByUser_Id(1L)).thenReturn(Arrays.asList(device));

        List<Device> devices = devicesService.findByUserId(1L);

        assertEquals(1, devices.size());
        assertEquals(DeviceType.HEATER, devices.get(0).getType());
        verify(devicesRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    void save_ValidDevice_ShouldSaveDevice() {
        Device device = new Device();
        device.setType(DeviceType.HEATER);
        device.setName("Test Heater");

        when(devicesRepository.save(any(Device.class))).thenReturn(device);

        Device savedDevice = devicesService.save(device);

        assertNotNull(savedDevice);
        assertEquals(DeviceType.HEATER, savedDevice.getType());
        verify(devicesRepository, times(1)).save(device);
    }

    @Test
    void save_NullDevice_ShouldThrowException() {
        assertThrows(ResponseStatusException.class, () -> devicesService.save(null));
    }

    @Test
    void turnOnDevice_DeviceExists_ShouldTurnOn() {
        Device device = new Device();
        device.setId(1L);
        device.setStatus(DeviceStatus.OFF);
        device.setMode(DeviceMode.AUTO);

        when(devicesRepository.findById(1L)).thenReturn(Optional.of(device));
        when(devicesRepository.save(any(Device.class))).thenReturn(device);

        Device updatedDevice = devicesService.turnOnDevice(1L);

        assertEquals(DeviceStatus.ON, updatedDevice.getStatus());
        assertEquals(DeviceMode.MANUAL, updatedDevice.getMode());
        verify(devicesRepository, times(1)).save(device);
    }

    @Test
    void turnOnDevice_DeviceNotFound_ShouldThrowException() {
        when(devicesRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> devicesService.turnOnDevice(1L));
    }

    @Test
    void deleteDevice_DeviceExists_ShouldDelete() {
        when(devicesRepository.existsById(1L)).thenReturn(true);

        devicesService.deleteDevice(1L);

        verify(devicesRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDevice_DeviceNotFound_ShouldThrowException() {
        when(devicesRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> devicesService.deleteDevice(1L));
    }
}