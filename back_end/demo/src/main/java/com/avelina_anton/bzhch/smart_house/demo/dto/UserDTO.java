// UserDTO.java (обновленный)
package com.avelina_anton.bzhch.smart_house.demo.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
}