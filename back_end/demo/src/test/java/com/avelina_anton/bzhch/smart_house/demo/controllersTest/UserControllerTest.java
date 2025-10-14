//package com.avelina_anton.bzhch.smart_house.demo.controllers;
//
//import com.avelina_anton.bzhch.smart_house.demo.dto.UserDTO;
//import com.avelina_anton.bzhch.smart_house.demo.models.User;
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
//@WebMvcTest(UserController.class)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UsersService usersService;
//
//    @MockBean
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void getAllUsers_ShouldReturnUsers() throws Exception {
//        User user = new User();
//        user.setId(1L);
//        user.setName("test");
//
//        UserDTO dto = new UserDTO();
//        dto.setId(1L);
//        dto.setName("test");
//
//        when(usersService.getAllUsers()).thenReturn(Arrays.asList(user));
//        when(modelMapper.map(user, UserDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(get("/smart_house/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1));
//    }
//
//    @Test
//    void getUserById_Valid_ShouldReturnUser() throws Exception {
//        User user = new User();
//        user.setId(1L);
//
//        UserDTO dto = new UserDTO();
//        dto.setId(1L);
//
//        when(usersService.findUserById(1L)).thenReturn(Optional.of(user));
//        when(modelMapper.map(user, UserDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(get("/smart_house/users/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    void createUser_Valid_ShouldCreate() throws Exception {
//        UserDTO dto = new UserDTO();
//        dto.setName("test");
//        dto.setPassword("password");
//        dto.setEmail("test@example.com");
//
//        User user = new User();
//        user.setId(1L);
//        user.setName("test");
//
//        when(modelMapper.map(dto, User.class)).thenReturn(user);
//        when(usersService.registerUser(user)).thenReturn(user);
//        when(modelMapper.map(user, UserDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(post("/smart_house/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("test"));
//    }
//
//    @Test
//    void updateUser_Valid_ShouldUpdate() throws Exception {
//        UserDTO dto = new UserDTO();
//        dto.setName("updated");
//        dto.setEmail("updated@example.com");
//
//        User existing = new User();
//        existing.setId(1L);
//
//        User updated = new User();
//        updated.setId(1L);
//        updated.setName("updated");
//
//        when(usersService.findUserById(1L)).thenReturn(Optional.of(existing));
//        when(modelMapper.map(dto, User.class)).thenReturn(updated);
//        when(usersService.updateUser(updated)).thenReturn(updated);
//        when(modelMapper.map(updated, UserDTO.class)).thenReturn(dto);
//
//        mockMvc.perform(put("/smart_house/users/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("updated"));
//    }
//
//    @Test
//    void deleteUser_Valid_ShouldDelete() throws Exception {
//        when(usersService.findUserById(1L)).thenReturn(Optional.of(new User()));
//
//        mockMvc.perform(delete("/smart_house/users/1"))
//                .andExpect(status().isNoContent());
//    }
//}