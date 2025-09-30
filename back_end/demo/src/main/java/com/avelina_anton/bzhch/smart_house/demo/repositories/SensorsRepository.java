package com.avelina_anton.bzhch.smart_house.demo.repositories;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorsRepository extends JpaRepository<Sensor, Long> {

    // Новые методы для работы с SmartHome (требуются контроллеру)

    /**
     * Возвращает все датчики, принадлежащие указанному умному дому.
     */
    List<Sensor> findBySmartHome(SmartHome smartHome);

    /**
     * Возвращает все датчики указанного типа, принадлежащие указанному умному дому.
     */
    List<Sensor> findBySmartHomeAndType(SmartHome smartHome, SensorType type);

    // Существующие методы, которые вы, вероятно, используете

    List<Sensor> findByType(SensorType type);

    // Методы findById, findAll, save и delete унаследованы от JpaRepository
}