package com.avelina_anton.bzhch.smart_house.demo.servicesTest;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
import com.avelina_anton.bzhch.smart_house.demo.services.AutomationService;
import com.avelina_anton.bzhch.smart_house.demo.services.DevicesService;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import com.avelina_anton.bzhch.smart_house.demo.services.SmartHomeService;
import com.avelina_anton.bzhch.smart_house.demo.services.SimulationService;
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

    @Mock
    private SmartHomeService smartHomeService;

    @Mock
    private SimulationService simulationService;

    @InjectMocks
    private AutomationService automationService;

    @Test
    void runAutomationCycle_TemperatureHigh_ShouldTurnOnAirConditioner() {
        // Arrange
        User user = new User();
        user.setId(1L);

        SmartHome smartHome = new SmartHome();
        smartHome.setId(1L);
        smartHome.setName("Test Home");
        smartHome.setUser(user);

        Sensor tempSensor = new Sensor();
        tempSensor.setType(SensorType.TEMPERATURE);
        tempSensor.setValue(25.0); // Выше комфортной зоны (20-24°C)
        tempSensor.setSmartHome(smartHome);

        Device airConditioner = new Device();
        airConditioner.setId(1L);
        airConditioner.setType(DeviceType.AIR_CONDITIONER);
        airConditioner.setMode(DeviceMode.AUTO);
        airConditioner.setStatus(DeviceStatus.OFF);
        airConditioner.setPowerLevel(0);
        airConditioner.setSmartHome(smartHome);

        when(smartHomeService.findAll()).thenReturn(Arrays.asList(smartHome));
        when(devicesService.findBySmartHome(smartHome)).thenReturn(Arrays.asList(airConditioner));
        when(sensorsService.getSensorsBySmartHome(smartHome)).thenReturn(Arrays.asList(tempSensor));

        // Act
        automationService.runAutomationCycle();

        // Assert
        verify(devicesService, times(1)).save(
                argThat(device ->
                        device.getType() == DeviceType.AIR_CONDITIONER &&
                                device.getStatus() == DeviceStatus.ON &&
                                device.getPowerLevel() > 0
                )
        );
    }

    @Test
    void runAutomationCycle_TemperatureLow_ShouldTurnOnHeater() {
        // Arrange
        User user = new User();
        user.setId(1L);

        SmartHome smartHome = new SmartHome();
        smartHome.setId(1L);
        smartHome.setName("Test Home");
        smartHome.setUser(user);

        Sensor tempSensor = new Sensor();
        tempSensor.setType(SensorType.TEMPERATURE);
        tempSensor.setValue(18.0); // Ниже комфортной зоны (20-24°C)
        tempSensor.setSmartHome(smartHome);

        Device heater = new Device();
        heater.setId(1L);
        heater.setType(DeviceType.HEATER);
        heater.setMode(DeviceMode.AUTO);
        heater.setStatus(DeviceStatus.OFF);
        heater.setPowerLevel(0);
        heater.setSmartHome(smartHome);

        when(smartHomeService.findAll()).thenReturn(Arrays.asList(smartHome));
        when(devicesService.findBySmartHome(smartHome)).thenReturn(Arrays.asList(heater));
        when(sensorsService.getSensorsBySmartHome(smartHome)).thenReturn(Arrays.asList(tempSensor));

        // Act
        automationService.runAutomationCycle();

        // Assert
        verify(devicesService, times(1)).save(
                argThat(device ->
                        device.getType() == DeviceType.HEATER &&
                                device.getStatus() == DeviceStatus.ON
                )
        );
    }

    @Test
    void runAutomationCycle_TemperatureComfortable_ShouldTurnOffAllDevices() {
        // Arrange
        User user = new User();
        user.setId(1L);

        SmartHome smartHome = new SmartHome();
        smartHome.setId(1L);
        smartHome.setName("Test Home");
        smartHome.setUser(user);

        Sensor tempSensor = new Sensor();
        tempSensor.setType(SensorType.TEMPERATURE);
        tempSensor.setValue(22.0); // В зоне комфорта (20-24°C)
        tempSensor.setSmartHome(smartHome);

        Device heater = new Device();
        heater.setId(1L);
        heater.setType(DeviceType.HEATER);
        heater.setMode(DeviceMode.AUTO);
        heater.setStatus(DeviceStatus.ON);
        heater.setPowerLevel(80);
        heater.setSmartHome(smartHome);

        Device airConditioner = new Device();
        airConditioner.setId(2L);
        airConditioner.setType(DeviceType.AIR_CONDITIONER);
        airConditioner.setMode(DeviceMode.AUTO);
        airConditioner.setStatus(DeviceStatus.ON);
        airConditioner.setPowerLevel(60);
        airConditioner.setSmartHome(smartHome);

        when(smartHomeService.findAll()).thenReturn(Arrays.asList(smartHome));
        when(devicesService.findBySmartHome(smartHome)).thenReturn(Arrays.asList(heater, airConditioner));
        when(sensorsService.getSensorsBySmartHome(smartHome)).thenReturn(Arrays.asList(tempSensor));

        // Act
        automationService.runAutomationCycle();

        // Assert
        verify(devicesService, times(1)).save(
                argThat(device ->
                        device.getType() == DeviceType.HEATER &&
                                device.getStatus() == DeviceStatus.OFF &&
                                device.getPowerLevel() == 0
                )
        );
        verify(devicesService, times(1)).save(
                argThat(device ->
                        device.getType() == DeviceType.AIR_CONDITIONER &&
                                device.getStatus() == DeviceStatus.OFF &&
                                device.getPowerLevel() == 0
                )
        );
    }

    @Test
    void runAutomationCycle_HumidityLow_ShouldTurnOnHumidifier() {
        // Arrange
        User user = new User();
        user.setId(1L);

        SmartHome smartHome = new SmartHome();
        smartHome.setId(1L);
        smartHome.setName("Test Home");
        smartHome.setUser(user);

        Sensor humiditySensor = new Sensor();
        humiditySensor.setType(SensorType.HUMIDITY);
        humiditySensor.setValue(25.0); // Ниже комфортной зоны (30-45%)
        humiditySensor.setSmartHome(smartHome);

        Device humidifier = new Device();
        humidifier.setId(1L);
        humidifier.setType(DeviceType.HUMIDIFIER);
        humidifier.setMode(DeviceMode.AUTO);
        humidifier.setStatus(DeviceStatus.OFF);
        humidifier.setPowerLevel(0);
        humidifier.setSmartHome(smartHome);

        when(smartHomeService.findAll()).thenReturn(Arrays.asList(smartHome));
        when(devicesService.findBySmartHome(smartHome)).thenReturn(Arrays.asList(humidifier));
        when(sensorsService.getSensorsBySmartHome(smartHome)).thenReturn(Arrays.asList(humiditySensor));

        // Act
        automationService.runAutomationCycle();

        // Assert
        verify(devicesService, times(1)).save(
                argThat(device ->
                        device.getType() == DeviceType.HUMIDIFIER &&
                                device.getStatus() == DeviceStatus.ON
                )
        );
    }

    @Test
    void runAutomationCycle_CO2High_ShouldTurnOnVentilator() {
        // Arrange
        User user = new User();
        user.setId(1L);

        SmartHome smartHome = new SmartHome();
        smartHome.setId(1L);
        smartHome.setName("Test Home");
        smartHome.setUser(user);

        Sensor co2Sensor = new Sensor();
        co2Sensor.setType(SensorType.CO2);
        co2Sensor.setValue(1200.0); // Выше комфортной зоны (до 1000 ppm)
        co2Sensor.setSmartHome(smartHome);

        Device ventilator = new Device();
        ventilator.setId(1L);
        ventilator.setType(DeviceType.VENTILATOR);
        ventilator.setMode(DeviceMode.AUTO);
        ventilator.setStatus(DeviceStatus.OFF);
        ventilator.setPowerLevel(0);
        ventilator.setSmartHome(smartHome);

        when(smartHomeService.findAll()).thenReturn(Arrays.asList(smartHome));
        when(devicesService.findBySmartHome(smartHome)).thenReturn(Arrays.asList(ventilator));
        when(sensorsService.getSensorsBySmartHome(smartHome)).thenReturn(Arrays.asList(co2Sensor));

        // Act
        automationService.runAutomationCycle();

        // Assert
        verify(devicesService, times(1)).save(
                argThat(device ->
                        device.getType() == DeviceType.VENTILATOR &&
                                device.getStatus() == DeviceStatus.ON
                )
        );
    }

    @Test
    void runAutomationCycle_ManualModeDevice_ShouldNotControl() {
        // Arrange
        User user = new User();
        user.setId(1L);

        SmartHome smartHome = new SmartHome();
        smartHome.setId(1L);
        smartHome.setName("Test Home");
        smartHome.setUser(user);

        Sensor tempSensor = new Sensor();
        tempSensor.setType(SensorType.TEMPERATURE);
        tempSensor.setValue(25.0); // Выше комфортной зоны
        tempSensor.setSmartHome(smartHome);

        Device manualDevice = new Device();
        manualDevice.setId(1L);
        manualDevice.setType(DeviceType.AIR_CONDITIONER);
        manualDevice.setMode(DeviceMode.MANUAL); // Ручной режим!
        manualDevice.setStatus(DeviceStatus.OFF);
        manualDevice.setSmartHome(smartHome);

        when(smartHomeService.findAll()).thenReturn(Arrays.asList(smartHome));
        when(devicesService.findBySmartHome(smartHome)).thenReturn(Arrays.asList(manualDevice));
        when(sensorsService.getSensorsBySmartHome(smartHome)).thenReturn(Arrays.asList(tempSensor));

        // Act
        automationService.runAutomationCycle();

        // Assert - устройство в ручном режиме не должно управляться автоматически
        verify(devicesService, never()).save(any(Device.class));
    }

    @Test
    void runAutomationCycle_MultipleSmartHomes_ShouldProcessEach() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        SmartHome smartHome1 = new SmartHome();
        smartHome1.setId(1L);
        smartHome1.setName("Home 1");
        smartHome1.setUser(user1);

        SmartHome smartHome2 = new SmartHome();
        smartHome2.setId(2L);
        smartHome2.setName("Home 2");
        smartHome2.setUser(user2);

        Sensor tempSensor1 = new Sensor();
        tempSensor1.setType(SensorType.TEMPERATURE);
        tempSensor1.setValue(25.0);
        tempSensor1.setSmartHome(smartHome1);

        Sensor tempSensor2 = new Sensor();
        tempSensor2.setType(SensorType.TEMPERATURE);
        tempSensor2.setValue(25.0);
        tempSensor2.setSmartHome(smartHome2);

        Device device1 = new Device();
        device1.setId(1L);
        device1.setType(DeviceType.AIR_CONDITIONER);
        device1.setMode(DeviceMode.AUTO);
        device1.setStatus(DeviceStatus.OFF);
        device1.setSmartHome(smartHome1);

        Device device2 = new Device();
        device2.setId(2L);
        device2.setType(DeviceType.AIR_CONDITIONER);
        device2.setMode(DeviceMode.AUTO);
        device2.setStatus(DeviceStatus.OFF);
        device2.setSmartHome(smartHome2);

        when(smartHomeService.findAll()).thenReturn(Arrays.asList(smartHome1, smartHome2));
        when(devicesService.findBySmartHome(smartHome1)).thenReturn(Arrays.asList(device1));
        when(devicesService.findBySmartHome(smartHome2)).thenReturn(Arrays.asList(device2));
        when(sensorsService.getSensorsBySmartHome(smartHome1)).thenReturn(Arrays.asList(tempSensor1));
        when(sensorsService.getSensorsBySmartHome(smartHome2)).thenReturn(Arrays.asList(tempSensor2));

        // Act
        automationService.runAutomationCycle();

        // Assert - оба устройства должны быть включены
        verify(devicesService, times(2)).save(any(Device.class));
    }

    @Test
    void runAutomationCycle_NoAutoDevices_ShouldDoNothing() {
        // Arrange
        User user = new User();
        user.setId(1L);

        SmartHome smartHome = new SmartHome();
        smartHome.setId(1L);
        smartHome.setName("Test Home");
        smartHome.setUser(user);

        // Нет устройств в авторежиме
        when(smartHomeService.findAll()).thenReturn(Arrays.asList(smartHome));
        when(devicesService.findBySmartHome(smartHome)).thenReturn(Arrays.asList());

        // Act
        automationService.runAutomationCycle();

        // Assert - никаких действий с устройствами
        verify(devicesService, never()).save(any(Device.class));
    }
}