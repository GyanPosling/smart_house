package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.services.SmartHomeService;
import com.avelina_anton.bzhch.smart_house.demo.utllis.SmartHomeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/smarthome")
public class SmartHomeController {

    private final SmartHomeService smartHomeService;

    public SmartHomeController(SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<SmartHome> getSmartHomeByUserId(@PathVariable Long userId) {
        SmartHome smartHome = smartHomeService.findByUserId(userId);
        if (smartHome != null) {
            return ResponseEntity.ok(smartHome);
        } else {
            throw new SmartHomeNotFoundException("Умный дом для пользователя с id " + userId + " не найден");
        }
    }

    @PostMapping
    public ResponseEntity<SmartHome> createSmartHome(@RequestBody SmartHome smartHome) {
        SmartHome createdSmartHome = smartHomeService.save(smartHome);
        return new ResponseEntity<>(createdSmartHome, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SmartHome>> getAllSmartHomes() {
        List<SmartHome> smartHomes = smartHomeService.findAll();
        return ResponseEntity.ok(smartHomes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SmartHome> getSmartHomeById(@PathVariable Long id) {
        SmartHome smartHome = smartHomeService.findById(id)
                .orElseThrow(() -> new SmartHomeNotFoundException("Умный дом с id " + id + " не найден"));
        return ResponseEntity.ok(smartHome);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SmartHome> updateSmartHome(@PathVariable Long id, @RequestBody SmartHome smartHomeDetails) {
        SmartHome updatedSmartHome = smartHomeService.update(id, smartHomeDetails);
        return ResponseEntity.ok(updatedSmartHome);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSmartHome(@PathVariable Long id) {
        smartHomeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}