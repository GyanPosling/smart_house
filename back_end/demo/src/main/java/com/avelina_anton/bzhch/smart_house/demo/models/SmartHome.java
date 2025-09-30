package com.avelina_anton.bzhch.smart_house.demo.models;

import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "smart_homes")
public class SmartHome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "smartHome", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Device> devices = new ArrayList<>();

    @OneToMany(mappedBy = "smartHome", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sensor> sensors = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SmartHome() {}

    public SmartHome(String name, User user) {
        this.name = name;
        this.user = user;
    }


    public void addDevice(Device device) {
        devices.add(device);
        device.setSmartHome(this);
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
        sensor.setSmartHome(this);
    }

    public int getActiveDevicesCount() {
        return (int) devices.stream()
                .filter(device -> device.getStatus() == DeviceStatus.ON)
                .count();
    }
}