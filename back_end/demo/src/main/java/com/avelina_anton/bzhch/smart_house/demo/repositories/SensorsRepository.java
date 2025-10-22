package com.avelina_anton.bzhch.smart_house.demo.repositories;

import com.avelina_anton.bzhch.smart_house.demo.models.Sensor;
import com.avelina_anton.bzhch.smart_house.demo.models.SensorType;
import com.avelina_anton.bzhch.smart_house.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorsRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByUser(User user);
    List<Sensor> findByUserAndType(User user, SensorType type);
    List<Sensor> findByType(SensorType type);
    @Query("SELECT s FROM Sensor s WHERE s.user.id = :userId AND s.type = :type")
    Optional<Sensor> findByUserIdAndType(@Param("userId") Long userId, @Param("type") SensorType type);
}