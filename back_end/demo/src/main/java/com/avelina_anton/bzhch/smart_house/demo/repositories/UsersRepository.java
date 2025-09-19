package com.avelina_anton.bzhch.smart_house.demo.repositories;

import com.avelina_anton.bzhch.smart_house.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    boolean existsByName(String name);
}
