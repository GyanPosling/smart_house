package com.avelina_anton.bzhch.smart_house.demo.services;

import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import com.avelina_anton.bzhch.smart_house.demo.repositories.SmartHomeRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SmartHomeService {

    private final SmartHomeRepository smartHomeRepository;

    public SmartHomeService(SmartHomeRepository smartHomeRepository) {
        this.smartHomeRepository = smartHomeRepository;
    }

    public List<SmartHome> findAll() {
        return smartHomeRepository.findAll();
    }

    public Optional<SmartHome> findById(Long id) {
        return smartHomeRepository.findById(id);
    }

    public SmartHome findByUserId(Long userId) {
        return smartHomeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SmartHome not found for User ID: " + userId));
    }

    public SmartHome save(SmartHome smartHome) {
        if (smartHome == null || smartHome.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid SmartHome data");
        }
        return smartHomeRepository.save(smartHome);
    }

    public SmartHome update(Long id, SmartHome smartHomeDetails) {
        SmartHome smartHome = smartHomeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SmartHome not found"));

        if (smartHomeDetails.getName() != null) {
            smartHome.setName(smartHomeDetails.getName());
        }

        // ВАЖНО: Связь с User не должна меняться через этот метод обновления

        return smartHomeRepository.save(smartHome);
    }

    public void delete(Long id) {
        if (!smartHomeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SmartHome not found");
        }
        smartHomeRepository.deleteById(id);
    }
}