// DeviceDTO.java (обновленный)
package com.avelina_anton.bzhch.smart_house.demo.dto;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class DeviceDTO {
    private Long id;
    private String name;
    private String location;
    private DeviceType type;
    private DeviceStatus status;
    private DeviceMode mode;
    private int powerLevel;
    private boolean isConnected;
    private Integer targetTemperature;
    private Integer currentTemperature;
    private Integer targetHumidity;
    private Integer currentHumidity;
    private Long userId;
}