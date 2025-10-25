CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       email VARCHAR(255) UNIQUE,
                       created_at TIMESTAMP
);

CREATE TABLE sensors (
                         id SERIAL PRIMARY KEY,
                         type VARCHAR(50) NOT NULL,
                         value DOUBLE PRECISION NOT NULL,
                         location VARCHAR(100),
                         user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP
);

CREATE TABLE devices (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         location VARCHAR(100),
                         status VARCHAR(50) NOT NULL,  -- Увеличено до 50
                         type VARCHAR(50) NOT NULL,
                         mode VARCHAR(50) NOT NULL,    -- Увеличено до 50
                         power_level INTEGER DEFAULT 0,
                         is_connected BOOLEAN DEFAULT FALSE,
                         target_temperature INTEGER,
                         current_temperature INTEGER,
                         target_humidity INTEGER,
                         current_humidity INTEGER,
                         user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP
);