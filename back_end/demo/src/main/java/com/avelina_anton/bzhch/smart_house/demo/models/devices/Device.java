package com.avelina_anton.bzhch.smart_house.demo.models.devices;

import com.avelina_anton.bzhch.smart_house.demo.models.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @Enumerated(EnumType.STRING)
    private DeviceType type;

    @Enumerated(EnumType.STRING)
    private DeviceMode mode;

    private int powerLevel;
    private boolean isConnected;

    private Integer targetTemperature;
    private Integer currentTemperature;
    private Integer targetHumidity;
    private Integer currentHumidity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getStatusDescription() {
        switch (type) {
            case HEATER:
                return "Обогреватель " + name + ": " + status + ", темп.: " + currentTemperature + "°C";
            case AIR_CONDITIONER:
                return "Кондиционер " + name + ": " + status + ", темп.: " + currentTemperature + "°C";
            case HUMIDIFIER:
                return "Увлажнитель " + name + ": " + status + ", влажность: " + currentHumidity + "%";
            default:
                return type + " " + name + ": " + status;
        }
    }

    public boolean supportsTemperatureControl() {
        return type == DeviceType.HEATER || type == DeviceType.AIR_CONDITIONER;
    }

    public boolean supportsHumidityControl() {
        return type == DeviceType.HUMIDIFIER || type == DeviceType.DEHUMIDIFIER;
    }
}