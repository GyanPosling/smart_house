-- Таблица пользователей
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE
);

-- Таблица датчиков
CREATE TABLE sensors (
    id SERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL CHECK (type IN ('TEMPERATURE', 'HUMIDITY', 'CO2', 'NOISE')),
    value DOUBLE PRECISION NOT NULL,
    location VARCHAR(100)
);

-- Таблица устройств
CREATE TABLE devices (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ON', 'OFF', 'STAND_BY')),
    type VARCHAR(50) NOT NULL CHECK (type IN ('HEATER', 'AIR_CONDITIONER', 'HUMIDIFIER', 'DEHUMIDIFIER', 'VENTILATOR')),
    mode VARCHAR(20) NOT NULL CHECK (mode IN ('AUTO', 'MANUAL')),
    power_level INTEGER DEFAULT 0 CHECK (power_level BETWEEN 0 AND 100),
    is_connected BOOLEAN DEFAULT false,
    target_temperature INTEGER,
    current_temperature INTEGER,
    target_humidity INTEGER,
    current_humidity INTEGER,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE
);