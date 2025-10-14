//package com.avelina_anton.bzhch.smart_house.demo.controllers;
//
//import com.avelina_anton.bzhch.smart_house.demo.models.User;
//import com.avelina_anton.bzhch.smart_house.demo.repositories.UsersRepository;
//import com.avelina_anton.bzhch.smart_house.demo.security.JwtUtils;
//import com.avelina_anton.bzhch.smart_house.demo.services.CustomUserDetailsService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AuthController.class)
//public class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    @MockBean
//    private UsersRepository usersRepository;
//
//    @MockBean
//    private CustomUserDetailsService customUserDetailsService;
//
//    @MockBean
//    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
//
//    @MockBean
//    private JwtUtils jwtUtils;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void register_UserNotExists_ShouldRegister() throws Exception {
//        User user = new User();
//        user.setName("test");
//        user.setPassword("password");
//        user.setEmail("test@example.com");
//
//        when(usersRepository.existsByName("test")).thenReturn(false);
//        when(usersRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.empty());
//        when(passwordEncoder.encode("password")).thenReturn("encoded");
//
//        mockMvc.perform(post("/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User registered successfully"));
//
//        verify(usersRepository, times(1)).save(any(User.class));
//    }
//
//    @Test
//    void register_UserExists_ShouldReturnBadRequest() throws Exception {
//        User user = new User();
//        user.setName("test");
//        user.setPassword("password");
//        user.setEmail("test@example.com");
//
//        when(usersRepository.existsByName("test")).thenReturn(true);
//
//        mockMvc.perform(post("/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Username already taken"));
//    }
//
//    @Test
//    void login_ValidCredentials_ShouldReturnToken() throws Exception {
//        User user = new User();
//        user.setName("test");
//        user.setPassword("password");
//
//        Authentication auth = mock(Authentication.class);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
//        when(jwtUtils.generateJwtToken("test")).thenReturn("token");
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("token"));
//    }
//
//    @Test
//    void login_InvalidCredentials_ShouldReturnUnauthorized() throws Exception {
//        User user = new User();
//        user.setName("test");
//        user.setPassword("wrong");
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(org.springframework.security.authentication.BadCredentialsException.class);
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(content().string("Invalid credentials"));
//    }
//}