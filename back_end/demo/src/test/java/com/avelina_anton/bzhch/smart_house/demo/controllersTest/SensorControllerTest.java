package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.SensorDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.services.SensorsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorController.class)
public class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorsService sensorsService;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllSensors_ShouldReturnSensors() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setId(1L);
        sensor.setType(SensorType.TEMPERATURE);

        SensorDTO dto = new SensorDTO();
        dto.setId(1L);
        dto.setType(SensorType.TEMPERATURE);

        when(sensorsService.getAllSensors()).thenReturn(Arrays.asList(sensor));
        when(modelMapper.map(sensor, SensorDTO.class)).thenReturn(dto);

        mockMvc.perform(get("/smart_house/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createSensor_Valid_ShouldCreate() throws Exception {
        SensorDTO dto = new SensorDTO();
        dto.setType(SensorType.TEMPERATURE);
        dto.setValue(22.0);
        dto.setLocation("Room");

        Sensor sensor = new Sensor();
        sensor.setId(1L);
        sensor.setType(SensorType.TEMPERATURE);
        sensor.setValue(22.0);
        sensor.setLocation("Room");

        when(modelMapper.map(dto, Sensor.class)).thenReturn(sensor);
        when(sensorsService.saveSensor(sensor)).thenReturn(sensor);
        when(modelMapper.map(sensor, SensorDTO.class)).thenReturn(dto);

        mockMvc.perform(post("/smart_house/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(22.0));
    }

    @Test
    void updateSensor_Valid_ShouldUpdate() throws Exception {
        SensorDTO dto = new SensorDTO();
        dto.setType(SensorType.TEMPERATURE);
        dto.setValue(23.0);
        dto.setLocation("Updated");

        Sensor existing = new Sensor();
        existing.setId(1L);

        when(sensorsService.getSensorById(1L)).thenReturn(Optional.of(existing));
        when(sensorsService.saveSensor(any(Sensor.class))).thenReturn(existing);
        when(modelMapper.map(existing, SensorDTO.class)).thenReturn(dto);

        mockMvc.perform(put("/smart_house/sensors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(23.0));
    }

    @Test
    void deleteSensor_Valid_ShouldDelete() throws Exception {
        when(sensorsService.getSensorById(1L)).thenReturn(Optional.of(new Sensor()));

        mockMvc.perform(delete("/smart_house/sensors/1"))
                .andExpect(status().isNoContent());
    }
}