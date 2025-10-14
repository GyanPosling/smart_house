//package com.avelina_anton.bzhch.smart_house.demo.controllers;
//
//import com.avelina_anton.bzhch.smart_house.demo.dto.DeviceDTO;
//import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
//import com.avelina_anton.bzhch.smart_house.demo.models.devices.Device;
//import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceMode;
//import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceStatus;
//import com.avelina_anton.bzhch.smart_house.demo.models.devices.DeviceType;
//import com.avelina_anton.bzhch.smart_house.demo.services.DevicesService;
//import com.avelina_anton.bzhch.smart_house.demo.services.SmartHomeService;
//import com.avelina_anton.bzhch.smart_house.demo.utils.DeviceValidator;
//import com.avelina_anton.bzhch.smart_house.demo.utils.ErrorsUtil;
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
//    private SmartHomeService smartHomeService;
//
//    @MockBean
//    private DeviceValidator deviceValidator;
//
//    @MockBean
//    private ErrorsUtil errorsUtil;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private SmartHome createTestSmartHome() {
//        SmartHome smartHome = new SmartHome();
//        smartHome.setId(1L);
//        smartHome.setName("Test Home");
//        return smartHome;
//    }
//
//    private Device createTestDevice() {
//        Device device = new Device();
//        device.setId(1L);
//        device.setType(DeviceType.HEATER);
//        device.setName("Test Device");
//        device.setSmartHome(createTestSmartHome());
//        return device;
//    }
//
//    private DeviceDTO createTestDeviceDTO() {
//        DeviceDTO dto = new DeviceDTO();
//        dto.setId(1L);
//        dto.setType(DeviceType.HEATER);
//        dto.setName("Test Device");
//        return dto;
//    }
//
//    @Test
//    void getSmartHomeDevices_ShouldReturnDevices() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//        Device device = createTestDevice();
//        DeviceDTO dto = createTestDeviceDTO();
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(devicesService.findBySmartHome(smartHome)).thenReturn(Arrays.asList(device));
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(get("/smarthome/1/devices"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1));
//    }
//
//    @Test
//    void addDeviceToSmartHome_Valid_ShouldAdd() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//        DeviceDTO dto = createTestDeviceDTO();
//        Device device = createTestDevice();
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(modelMapper.map(dto, Device.class)).thenReturn(device);
//        when(devicesService.save(device)).thenReturn(device);
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(post("/smarthome/1/devices")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.type").value("HEATER"));
//    }
//
//    @Test
//    void getDeviceById_Valid_ShouldReturnDevice() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//        Device device = createTestDevice();
//        DeviceDTO dto = createTestDeviceDTO();
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(devicesService.findById(1L)).thenReturn(device);
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(get("/smarthome/1/devices/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    void updateDevice_Valid_ShouldUpdate() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//        Device existingDevice = createTestDevice();
//        DeviceDTO dto = createTestDeviceDTO();
//        dto.setName("Updated Name");
//
//        Device updatedDevice = createTestDevice();
//        updatedDevice.setName("Updated Name");
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(devicesService.findById(1L)).thenReturn(existingDevice);
//        when(modelMapper.map(dto, Device.class)).thenReturn(updatedDevice);
//        when(devicesService.save(updatedDevice)).thenReturn(updatedDevice);
//        when(modelMapper.map(updatedDevice, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(put("/smarthome/1/devices/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Updated Name"));
//    }
//
//    @Test
//    void deleteDevice_Valid_ShouldDelete() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//        Device device = createTestDevice();
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(devicesService.findById(1L)).thenReturn(device);
//
//        mockMvc.perform(delete("/smarthome/1/devices/1"))
//                .andExpect(status().isNoContent());
//
//        verify(devicesService, times(1)).deleteDevice(1L);
//    }
//
//    @Test
//    void turnOnDevice_Valid_ShouldTurnOn() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//        Device device = createTestDevice();
//        device.setStatus(DeviceStatus.ON);
//
//        DeviceDTO dto = createTestDeviceDTO();
//        dto.setStatus(DeviceStatus.ON);
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(devicesService.turnOnDevice(1L)).thenReturn(device);
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(patch("/smarthome/1/devices/1/on"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("ON"));
//    }
//
//    @Test
//    void turnOffDevice_Valid_ShouldTurnOff() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//        Device device = createTestDevice();
//        device.setStatus(DeviceStatus.OFF);
//
//        DeviceDTO dto = createTestDeviceDTO();
//        dto.setStatus(DeviceStatus.OFF);
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(devicesService.turnOffDevice(1L)).thenReturn(device);
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(patch("/smarthome/1/devices/1/off"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("OFF"));
//    }
//
//    @Test
//    void setAutoMode_Valid_ShouldSetAuto() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//        Device device = createTestDevice();
//        device.setMode(DeviceMode.AUTO);
//
//        DeviceDTO dto = createTestDeviceDTO();
//        dto.setMode(DeviceMode.AUTO);
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(devicesService.setAutomationMode(1L)).thenReturn(device);
//        when(modelMapper.map(device, DeviceDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(patch("/smarthome/1/devices/1/auto"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.mode").value("AUTO"));
//    }
//
//    @Test
//    void getDeviceById_DeviceNotFound_ShouldReturnNotFound() throws Exception {
//        SmartHome smartHome = createTestSmartHome();
//
//        when(smartHomeService.findById(1L)).thenReturn(Optional.of(smartHome));
//        when(devicesService.findById(1L)).thenReturn(null);
//
//        mockMvc.perform(get("/smarthome/1/devices/1"))
//                .andExpect(status().isNotFound());
//    }
//}