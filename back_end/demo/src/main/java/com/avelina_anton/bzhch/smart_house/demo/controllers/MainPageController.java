package com.avelina_anton.bzhch.smart_house.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
@RequestMapping()
public class MainPageController {
    @GetMapping()
    public String mainPage(){
        return " ";
    }


}

