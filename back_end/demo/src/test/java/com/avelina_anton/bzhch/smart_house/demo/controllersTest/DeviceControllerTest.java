//package com.avelina_anton.bzhch.smart_house.demo.controllers;
//
//import com.avelina_anton.bzhch.smart_house.demo.dto.DeviceDTO;
//import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
//import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
//import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
//import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
//import com.avelina_anton.bzhch.smart_house.demo.services.DevicesService;
//import com.avelina_anton.bzhch.smart_house.demo.services.UsersService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(DeviceController.class)
//public class DeviceControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private DevicesService devicesService;
//
//    @MockBean
//    private ModelMapper modelMapper;
//
//    @MockBean
//    private UsersService usersService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void getAllDevices_ShouldReturnDevices() throws Exception {
//        Device device = new Device();
//        device.setId(1L);
//        device.setType(DeviceType.HEATER);
//
//        DeviceDTO dto = new DeviceDTO();
//        dto.setId(1L);
//        dto.setType(DeviceType.HEATER);
//
//        when(devicesService.findAll()).thenReturn(Arrays.asList(device));
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(get("/smart_house/devices"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1));
//    }
//
//    @Test
//    void addDevice_Valid_ShouldAdd() throws Exception {
//        DeviceDTO dto = new DeviceDTO();
//        dto.setType(DeviceType.HEATER);
//        dto.setName("Test");
//
//        Device device = new Device();
//        device.setId(1L);
//        device.setType(DeviceType.HEATER);
//        device.setName("Test");
//
//        when(modelMapper.map(dto, Device.class)).thenReturn(device);
//        when(devicesService.save(device)).thenReturn(device);
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(post("/smart_house/devices")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.type").value("HEATER"));
//    }
//
//    @Test
//    void updateDevice_Valid_ShouldUpdate() throws Exception {
//        DeviceDTO dto = new DeviceDTO();
//        dto.setType(DeviceType.HEATER);
//        dto.setName("Updated");
//
//        Device existing = new Device();
//        existing.setId(1L);
//        existing.setType(DeviceType.HEATER);
//
//        Device updated = new Device();
//        updated.setId(1L);
//        updated.setType(DeviceType.HEATER);
//        updated.setName("Updated");
//
//        when(devicesService.findById(1L)).thenReturn(Optional.of(existing));
//        when(modelMapper.map(dto, Device.class)).thenReturn(updated);
//        when(devicesService.save(updated)).thenReturn(updated);
//        when(modelMapper.map(updated, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(put("/smart_house/devices/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Updated"));
//    }
//
//    @Test
//    void deleteDevice_Valid_ShouldDelete() throws Exception {
//        when(devicesService.findById(1L)).thenReturn(Optional.of(new Device()));
//
//        mockMvc.perform(delete("/smart_house/devices/1"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void turnOnDevice_Valid_ShouldTurnOn() throws Exception {
//        Device device = new Device();
//        device.setId(1L);
//        device.setStatus(DeviceStatus.ON);
//
//        DeviceDTO dto = new DeviceDTO();
//        dto.setId(1L);
//        dto.setStatus(DeviceStatus.ON);
//
//        when(devicesService.turnOnDevice(1L)).thenReturn(device);
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(patch("/smart_house/devices/1/on"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("ON"));
//    }
//}