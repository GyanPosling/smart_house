package com.avelina_anton.bzhch.smart_house.demo.models.devices;

import com.avelina_anton.bzhch.smart_house.demo.models.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "device_type")
@Table(name = "devices")
public abstract class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
//    private String location;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    private DeviceMode deviceMode;

    private int powerLevel;
    private boolean isConnected;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public abstract String getDeviceType();
    public abstract String getStatus();
    public abstract String getStatusDescription();
    public abstract DeviceMode getMode();
}
