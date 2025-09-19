package com.avelina_anton.bzhch.smart_house.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    @NotEmpty
    @Size(min = 2, max = 50, message = "name should be greater than 2 and less than 50")
    private String name;

    @NotEmpty
    @Size(min = 5, max = 100, message = "Password should be greater than 5 and less than 100")
    @Column(name = "password")
    private String password;

    @Column(name = "email")
    @Email
    private String email;


}
