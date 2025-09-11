package com.avelina_anton.bzhch.smart_house.demo.repositories;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevicesRepository extends JpaRepository<Device, Integer> {
}
