package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.repositories.UsersRepository;
import com.avelina_anton.bzhch.smart_house.demo.utllis.SmartHomeException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository userRepository;

    public UsersService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByName(String name) {
        return userRepository.findByName(name);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        if (findUserByName(user.getName()).isPresent()) {
            throw new SmartHomeException("Имя пользователя уже занято");
        }
        if (findUserByEmail(user.getEmail()).isPresent()) {
            throw new SmartHomeException("Email уже используется");
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}