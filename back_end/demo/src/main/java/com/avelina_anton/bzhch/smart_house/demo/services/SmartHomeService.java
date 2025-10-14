package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SmartHomeRepository;
import com.avelina_anton.bzhch.smart_house.demo.utllis.SmartHomeNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SmartHomeService {

    private final SmartHomeRepository smartHomeRepository;

    public SmartHomeService(SmartHomeRepository smartHomeRepository) {
        this.smartHomeRepository = smartHomeRepository;
    }

    public SmartHome findByUserId(Long userId) {
        return smartHomeRepository.findByUserId(userId);
    }

    public SmartHome save(SmartHome smartHome) {
        return smartHomeRepository.save(smartHome);
    }

    public List<SmartHome> findAll() {
        return smartHomeRepository.findAll();
    }

    public Optional<SmartHome> findById(Long id) {
        return smartHomeRepository.findById(id);
    }

    public SmartHome update(Long id, SmartHome smartHomeDetails) {
        SmartHome smartHome = findById(id)
                .orElseThrow(() -> new SmartHomeNotFoundException("Умный дом с id " + id + " не найден"));
        smartHome.setName(smartHomeDetails.getName());
        return smartHomeRepository.save(smartHome);
    }

    public void delete(Long id) {
        SmartHome smartHome = findById(id)
                .orElseThrow(() -> new SmartHomeNotFoundException("Умный дом с id " + id + " не найден"));
        smartHomeRepository.delete(smartHome);
    }
}