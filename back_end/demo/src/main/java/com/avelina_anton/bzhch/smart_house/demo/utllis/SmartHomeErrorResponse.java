package com.avelina_anton.bzhch.smart_house.demo.utllis;

import java.time.LocalDateTime;

public class SmartHomeErrorResponse {
    private String message;
    private LocalDateTime timestamp;

    public SmartHomeErrorResponse(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}