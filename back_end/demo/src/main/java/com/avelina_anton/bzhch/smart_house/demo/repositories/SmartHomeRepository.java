package com.avelina_anton.bzhch.smart_house.demo.repositories;

import com.avelina_anton.bzhch.smart_house.demo.models.SmartHome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmartHomeRepository extends JpaRepository<SmartHome, Long> {

    @Query("SELECT sh FROM SmartHome sh WHERE sh.user.id = :userId")
    SmartHome findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(sh) > 0 FROM SmartHome sh WHERE sh.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);

    @Query("SELECT sh FROM SmartHome sh JOIN FETCH sh.devices WHERE sh.user.id = :userId")
    Optional<SmartHome> findByUserIdWithDevices(@Param("userId") Long userId);
}