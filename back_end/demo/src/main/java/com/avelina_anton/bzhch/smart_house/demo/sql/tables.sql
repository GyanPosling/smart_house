CREATE TABLE Users (
    id int primary key generated always as identity,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255)
);

CREATE TABLE Devices (
    id int primary key generated always as identity,
    name VARCHAR(255) NOT NULL,
    device_type VARCHAR(50) NOT NULL CHECK (device_type IN ('HEATER', 'AIR_CONDITIONER', 'HUMIDIFIER', 'DEHUMIDIFIER', 'VENTILATOR')),
    status VARCHAR(10) NOT NULL CHECK (status IN ('ON', 'OFF')),
    device_mode VARCHAR(10) NOT NULL CHECK (device_mode IN ('MANUAL', 'AUTO')),
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE Sensors (
    id int primary key generated always as identity,
    type VARCHAR(50) NOT NULL CHECK (type IN ('TEMPERATURE', 'HUMIDITY', 'CO2', 'NOISE')),
    value DOUBLE PRECISION NOT NULL CHECK (value >= 0),
    location VARCHAR(255) NOT NULL
);
