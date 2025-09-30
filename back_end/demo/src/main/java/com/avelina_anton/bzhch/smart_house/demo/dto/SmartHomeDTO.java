package com.avelina_anton.bzhch.smart_house.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SmartHomeDTO {
    private Long id;
    private String name;
    private Long userId;
    private String userName;
    private int devicesCount;
    private int activeDevicesCount;
    private int sensorsCount;
    private List<DeviceDTO> devices;
    private List<SensorDTO> sensors;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SmartHomeDTO() {}


}