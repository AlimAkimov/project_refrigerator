-- liquibase formatted sql

--changeset akimov:1
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    instructions TEXT NOT NULL,
    cooking_time INT, -- в минутах
    difficulty_level VARCHAR(50)
);

--changeset akimov:2
CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(100) -- например: 'Молочные', 'Мясо', 'Овощи' и т.д.
);

--changeset akimov:3
CREATE TABLE measurement_unit (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- 'граммы', 'миллилитры', 'штуки' и т.д.
    abbreviation VARCHAR(10) NOT NULL UNIQUE -- 'г', 'мл', 'шт'
);

--changeset akimov:4
CREATE TABLE dish_ingredient (
    id SERIAL PRIMARY KEY,
    dish_id INT NOT NULL REFERENCES dish(id) ON DELETE CASCADE,
    ingredient_id INT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    amount NUMERIC(10, 2) NOT NULL, -- количество (например, 200.50)
    measurement_unit_id INT REFERENCES measurement_unit(id) ON DELETE SET NULL,
    notes VARCHAR(255), -- дополнительные заметки ('мелко нарезанные', 'охлаждённые' и т.д.)
    CONSTRAINT unique_dish_ingredient UNIQUE (dish_id, ingredient_id)
);



