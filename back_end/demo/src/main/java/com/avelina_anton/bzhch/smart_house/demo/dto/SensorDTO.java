// SensorDTO.java
package com.avelina_anton.bzhch.smart_house.demo.dto;

import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SensorDTO {
    private Long id; // Необязательное поле, без @NotNull

    @NotNull(message = "Тип датчика обязателен")
    private SensorType type;

    @Min(value = 0, message = "Значение должно быть не менее 0")
    private double value;

    @NotEmpty(message = "Локация обязательна")
    @Size(min = 2, max = 100, message = "Локация должна быть от 2 до 100 символов")
    private String location;
}