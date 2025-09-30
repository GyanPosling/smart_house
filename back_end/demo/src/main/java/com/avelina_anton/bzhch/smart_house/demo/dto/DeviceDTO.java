// DeviceDTO.java
package com.avelina_anton.bzhch.smart_house.demo.dto;

import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceDTO {
    private Long id; // Необязательное поле, без @NotNull, чтобы не требовать в PUT-запросах

    @NotNull(message = "Тип устройства обязателен")
    private DeviceType type;

    @NotEmpty(message = "Имя устройства обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String name;

    private String location;

    private DeviceStatus status;

    private DeviceMode mode;

    @Min(value = 0, message = "Уровень мощности должен быть не менее 0")
    private Integer powerLevel;

    private Boolean isConnected;

    private Integer targetTemperature;

    private Integer currentTemperature;

    private Integer targetHumidity;

    private Integer currentHumidity;

    private Long userId;

    private SmartHome smartHome;

}