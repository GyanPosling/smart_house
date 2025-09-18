package com.avelina_anton.bzhch.smart_house.demo.repositories;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevicesRepository extends JpaRepository<Device, Long> { // Изменено на Long
    Optional<Device> findById(Long deviceId);

    List<Device> findByDeviceType(DeviceType type); // Изменено название метода


    List<Device> findByDeviceTypeAndStatus(DeviceType type, DeviceStatus status); // Изменено название метода

    List<Device> findByUser_Id(Long userId);

    List<Device> findByType(DeviceType type);
    Optional <Device> findFirstByType(DeviceType type);
}