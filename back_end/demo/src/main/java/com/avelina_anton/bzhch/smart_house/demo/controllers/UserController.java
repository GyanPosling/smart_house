package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.dto.UserDTO;
import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.services.UsersService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


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
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID пользователя");
        }
        return usersService.findUserById(id)
                .map(user -> ResponseEntity.ok(modelMapper.map(user, UserDTO.class)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email и пароль обязательны");
        }
        User user = modelMapper.map(userDTO, User.class);
        User savedUser = usersService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedUser, UserDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO userDTO) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID пользователя");
        }
        return usersService.findUserById(id)
                .map(existingUser -> {
                    User user = modelMapper.map(userDTO, User.class);
                    user.setId(id);
                    User updatedUser = usersService.updateUser(user);
                    return ResponseEntity.ok(modelMapper.map(updatedUser, UserDTO.class));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимый ID пользователя");
        }
        if (usersService.findUserById(id).isPresent()) {
            usersService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
    }
}