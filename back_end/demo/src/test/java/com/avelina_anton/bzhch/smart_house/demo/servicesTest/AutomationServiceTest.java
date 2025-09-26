package com.avelina_anton.bzhch.smart_house.demo.servicesTest;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import com.avelina_anton.bzhch.smart_house.demo.services.AutomationService;
import com.avelina_anton.bzhch.smart_house.demo.services.DevicesService;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutomationServiceTest {

    @Mock
    private SensorsService sensorsService;

    @Mock
    private DevicesService devicesService;

    @InjectMocks
    private AutomationService automationService;

    @Test
    void automateSmartHome_TemperatureHigh_ShouldTurnOnAirConditioner() {
        Sensor sensor = new Sensor();
        sensor.setType(SensorType.TEMPERATURE);
        sensor.setValue(25.0);

        Device airConditioner = new Device();
        airConditioner.setId(1L);
        airConditioner.setType(DeviceType.AIR_CONDITIONER);
        airConditioner.setMode(DeviceMode.AUTO);
        airConditioner.setStatus(DeviceStatus.OFF);

        when(sensorsService.getSensorsByType(SensorType.TEMPERATURE)).thenReturn(Arrays.asList(sensor));
        when(devicesService.findAll()).thenReturn(Arrays.asList(airConditioner));
        when(devicesService.save(any(Device.class))).thenReturn(airConditioner);

        automationService.automateSmartHome();

        verify(devicesService, times(1)).save(argThat(device ->
                device.getType() == DeviceType.AIR_CONDITIONER && device.getStatus() == DeviceStatus.ON));
    }

    @Test
    void automateSmartHome_HumidityLow_ShouldTurnOnHumidifier() {
        Sensor sensor = new Sensor();
        sensor.setType(SensorType.HUMIDITY);
        sensor.setValue(25.0);

        Device humidifier = new Device();
        humidifier.setId(1L);
        humidifier.setType(DeviceType.HUMIDIFIER);
        humidifier.setMode(DeviceMode.AUTO);
        humidifier.setStatus(DeviceStatus.OFF);

        when(sensorsService.getSensorsByType(SensorType.HUMIDITY)).thenReturn(Arrays.asList(sensor));
        when(devicesService.findAll()).thenReturn(Arrays.asList(humidifier));
        when(devicesService.save(any(Device.class))).thenReturn(humidifier);

        automationService.automateSmartHome();

        verify(devicesService, times(1)).save(argThat(device ->
                device.getType() == DeviceType.HUMIDIFIER && device.getStatus() == DeviceStatus.ON));
    }

    @Test
    void automateSmartHome_CO2High_ShouldTurnOnVentilator() {
        Sensor sensor = new Sensor();
        sensor.setType(SensorType.CO2);
        sensor.setValue(1200.0);

        Device ventilator = new Device();
        ventilator.setId(1L);
        ventilator.setType(DeviceType.VENTILATOR);
        ventilator.setMode(DeviceMode.AUTO);
        ventilator.setStatus(DeviceStatus.OFF);

        when(sensorsService.getSensorsByType(SensorType.CO2)).thenReturn(Arrays.asList(sensor));
        when(devicesService.findAll()).thenReturn(Arrays.asList(ventilator));
        when(devicesService.save(any(Device.class))).thenReturn(ventilator);

        automationService.automateSmartHome();

        verify(devicesService, times(1)).save(argThat(device ->
                device.getType() == DeviceType.VENTILATOR && device.getStatus() == DeviceStatus.ON));
    }
}