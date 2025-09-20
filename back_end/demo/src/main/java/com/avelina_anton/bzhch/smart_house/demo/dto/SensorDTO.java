
package com.avelina_anton.bzhch.smart_house.demo.dto;

import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SensorDTO {
    private Long id;
    private SensorType type;
    private double value;
    private String location;
}