DROP DATABASE IF EXISTS sports_booking;
CREATE DATABASE sports_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sports_booking;

-- 1. Таблиця спортивних об'єктів
CREATE TABLE sports_facility (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 code VARCHAR(40) NOT NULL,
                                 name VARCHAR(160) NOT NULL,
                                 facility_type VARCHAR(100) NOT NULL,
                                 max_capacity INT NOT NULL
);

-- 2. Таблиця користувачів
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       full_name VARCHAR(160) NOT NULL,
                       email VARCHAR(120) NOT NULL,
                       phone VARCHAR(40) NOT NULL
);

-- 3. Таблиця часових слотів
CREATE TABLE time_slot (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           facility_id BIGINT NOT NULL,
                           slot_date VARCHAR(20) NOT NULL,
                           start_time VARCHAR(10) NOT NULL,
                           end_time VARCHAR(10) NOT NULL,
                           duration_minutes DOUBLE NOT NULL,
                           is_available BOOLEAN NOT NULL DEFAULT TRUE,
                           FOREIGN KEY (facility_id) REFERENCES sports_facility(id) ON DELETE CASCADE
);

-- 4. Таблиця бронювань (об'єкт життєвого циклу)
CREATE TABLE booking (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         slot_id BIGINT NOT NULL,
                         participants_count INT NOT NULL,
                         equipment_score DOUBLE NOT NULL,
                         state VARCHAR(30) NOT NULL,
                         f1 DOUBLE NOT NULL,
                         f2 DOUBLE NOT NULL,
                         f3 DOUBLE NOT NULL,
                         integral_index DOUBLE NOT NULL,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (slot_id) REFERENCES time_slot(id) ON DELETE CASCADE
);

-- Початкові демонстраційні дані: Спортивні об'єкти
INSERT INTO sports_facility (code, name, facility_type, max_capacity) VALUES
                                                                          ('SF-01', 'Головна баскетбольна арена', 'Ігровий зал', 20),
                                                                          ('SF-02', 'Тенісний корт №1 (критий)', 'Тенісний корт', 4),
                                                                          ('SF-03', 'Міні-футбольне поле Політехнік', 'Футбольне поле', 16);

-- Початкові демонстраційні дані: Користувачі
INSERT INTO users (full_name, email, phone) VALUES
                                                ('Коваленко Олександр Ігорович', 'kovalenko@example.com', '+380501112233'),
                                                ('Мельник Ірина Василівна', 'melnyk@example.com', '+380672223344');

-- Початкові демонстраційні дані: Часові слоти
INSERT INTO time_slot (facility_id, slot_date, start_time, end_time, duration_minutes, is_available) VALUES
                                                                                                         (1, '2026-10-25', '10:00', '11:30', 90.0, FALSE),
                                                                                                         (2, '2026-10-25', '12:00', '13:00', 60.0, TRUE),
                                                                                                         (3, '2026-10-26', '16:00', '18:00', 120.0, FALSE);

-- Початкові демонстраційні дані: Бронювання
-- Приклад 1: 15 з 20 учасників (F1 = 75.0), 90 з 120 хв (F2 = 75.0), обладнання F3 = 60.0 -> I = 0.40*75 + 0.35*75 + 0.25*60 = 71.25
INSERT INTO booking (user_id, slot_id, participants_count, equipment_score, state, f1, f2, f3, integral_index) VALUES
                                                                                                                   (1, 1, 15, 60.0, 'CREATED', 75.0, 75.0, 60.0, 71.25),
                                                                                                                   (2, 3, 16, 80.0, 'CONFIRMED', 100.0, 100.0, 80.0, 95.00);