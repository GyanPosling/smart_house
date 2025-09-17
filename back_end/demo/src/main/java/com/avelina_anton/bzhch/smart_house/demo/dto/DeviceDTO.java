package com.avelina_anton.bzhch.smart_house.demo.dto;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class DeviceDTO {
    private String name;
    private DeviceType type;
    private DeviceStatus status;
    private DeviceMode deviceMode;
    private int powerLevel;
    private boolean isConnected;
}
