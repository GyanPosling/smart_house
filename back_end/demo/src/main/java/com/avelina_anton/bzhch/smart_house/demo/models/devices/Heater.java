package com.avelina_anton.bzhch.smart_house.demo.models.devices;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("HEATER") // ← Значение для этого типа в БД
@EqualsAndHashCode(callSuper = true)
public class Heater extends Device {
    private int targetTemperature;
    private int currentTemperature;

    @Override
    public String getDeviceType() {
        return "HEATER";
    }

    @Override
    public String getStatus() {
        return "kkk";
    }

    @Override
    public String getStatusDescription() {
        return "Обогреватель " + getName() + ": " + getStatus() + ", мощность: " + getPowerLevel() + "%";
    }
}
