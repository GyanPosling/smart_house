package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.UserDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.services.UsersService;
import com.avelina_anton.bzhch.smart_house.demo.utllis.SmartHomeException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/smart_house/users")
public class UserController {
    private final UsersService usersService;
    private final ModelMapper modelMapper;

    public UserController(UsersService usersService, ModelMapper modelMapper) {
        this.usersService = usersService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return usersService.getAllUsers().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = usersService.findUserById(id)
                .orElseThrow(() -> new SmartHomeException("Пользователь с id " + id + " не найден"));
        return ResponseEntity.ok(modelMapper.map(user, UserDTO.class));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = usersService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedUser, UserDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        User existingUser = usersService.findUserById(id)
                .orElseThrow(() -> new SmartHomeException("Пользователь с id " + id + " не найден"));

        User user = modelMapper.map(userDTO, User.class);
        user.setId(id);
        user.setCreatedAt(existingUser.getCreatedAt());

        User updatedUser = usersService.updateUser(user);
        return ResponseEntity.ok(modelMapper.map(updatedUser, UserDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = usersService.findUserById(id)
                .orElseThrow(() -> new SmartHomeException("Пользователь с id " + id + " не найден"));
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}