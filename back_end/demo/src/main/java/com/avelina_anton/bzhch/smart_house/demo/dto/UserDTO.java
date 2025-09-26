// UserDTO.java
package com.avelina_anton.bzhch.smart_house.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    private Long id; // Необязательное поле, без @NotNull

    @NotEmpty(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String name;

    @NotEmpty(message = "Пароль обязателен")
    @Size(min = 5, max = 100, message = "Пароль должен быть от 5 до 100 символов")
    private String password;

    @Email(message = "Некорректный email")
    @NotEmpty(message = "Email обязателен")
    private String email;
}