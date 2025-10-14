//package com.avelina_anton.bzhch.smart_house.demo.servicesTest;
//
//import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
//import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
//import com.avelina_anton.bzhch.smart_house.demo.repositories.SensorsRepository;
//import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class SensorsServiceTest {
//
//    @Mock
//    private SensorsRepository sensorsRepository;
//
//    @InjectMocks
//    private SensorsService sensorsService;
//
//    @Test
//    void getAllSensors_ShouldReturnAllSensors() {
//        Sensor sensor1 = new Sensor();
//        sensor1.setId(1L);
//        sensor1.setType(SensorType.TEMPERATURE);
//        Sensor sensor2 = new Sensor();
//        sensor2.setId(2L);
//        sensor2.setType(SensorType.HUMIDITY);
//
//        when(sensorsRepository.findAll()).thenReturn(Arrays.asList(sensor1, sensor2));
//
//        List<Sensor> sensors = sensorsService.getAllSensors();
//
//        assertEquals(2, sensors.size());
//        verify(sensorsRepository, times(1)).findAll();
//    }
//
//    @Test
//    void getSensorsByType_ShouldReturnSensorsByType() {
//        Sensor sensor = new Sensor();
//        sensor.setId(1L);
//        sensor.setType(SensorType.TEMPERATURE);
//
//        when(sensorsRepository.findByType(SensorType.TEMPERATURE)).thenReturn(Arrays.asList(sensor));
//
//        List<Sensor> sensors = sensorsService.getSensorsByType(SensorType.TEMPERATURE);
//
//        assertEquals(1, sensors.size());
//        assertEquals(SensorType.TEMPERATURE, sensors.get(0).getType());
//        verify(sensorsRepository, times(1)).findByType(SensorType.TEMPERATURE);
//    }
//
//    @Test
//    void saveSensor_ValidSensor_ShouldSave() {
//        Sensor sensor = new Sensor();
//        sensor.setType(SensorType.CO2);
//        sensor.setValue(500.0);
//
//        when(sensorsRepository.save(any(Sensor.class))).thenReturn(sensor);
//
//        Sensor savedSensor = sensorsService.saveSensor(sensor);
//
//        assertNotNull(savedSensor);
//        assertEquals(SensorType.CO2, savedSensor.getType());
//        verify(sensorsRepository, times(1)).save(sensor);
//    }
//
//    @Test
//    void deleteSensor_SensorExists_ShouldDelete() {
//        when(sensorsRepository.findById(1L)).thenReturn(Optional.of(new Sensor()));
//
//        sensorsService.deleteSensor(1L);
//
//        verify(sensorsRepository, times(1)).deleteById(1L);
//    }
//}