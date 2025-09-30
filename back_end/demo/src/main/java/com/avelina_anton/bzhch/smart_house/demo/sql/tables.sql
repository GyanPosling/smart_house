-- Таблица пользователей
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       email VARCHAR(255) UNIQUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица умных домов
CREATE TABLE smart_homes (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(100) NOT NULL,
                             user_id INTEGER UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица датчиков (теперь связана с умным домом)
CREATE TABLE sensors (
                         id SERIAL PRIMARY KEY,
                         type VARCHAR(20) NOT NULL CHECK (type IN ('TEMPERATURE', 'HUMIDITY', 'CO2', 'NOISE')),
                         value DOUBLE PRECISION NOT NULL,
                         location VARCHAR(100),
                         smart_home_id INTEGER REFERENCES smart_homes(id) ON DELETE CASCADE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица устройств (теперь связана с умным домом вместо пользователя)
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
                         smart_home_id INTEGER REFERENCES smart_homes(id) ON DELETE CASCADE,
                         user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);