package com.avelina_anton.bzhch.smart_house.demo.repositories;

import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevicesRepository extends JpaRepository<Device, Long> {
    List<Device> findByType(DeviceType type);
    List<Device> findByTypeAndStatus(DeviceType type, DeviceStatus status);
    List<Device> findByUser_Id(Long userId);
    Optional<Device> findFirstByType(DeviceType type);
}