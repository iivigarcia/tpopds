-- Disable foreign key checks to safely drop all tables
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables in correct order (due to foreign key constraints)
DROP TABLE IF EXISTS estadisticas;
DROP TABLE IF EXISTS comentarios;
DROP TABLE IF EXISTS usuario_deporte;
DROP TABLE IF EXISTS equipo_jugadores;
DROP TABLE IF EXISTS partido_equipos;
DROP TABLE IF EXISTS partidos_jugadores;
DROP TABLE IF EXISTS partidos;
DROP TABLE IF EXISTS equipos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS deportes;
DROP TABLE IF EXISTS geolocalizations;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Create tables in correct order
CREATE TABLE geolocalizations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    lat VARCHAR(255) NOT NULL,
    lng VARCHAR(255) NOT NULL
);

CREATE TABLE deportes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    geolocalization_id INT NOT NULL
);

CREATE TABLE equipos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

CREATE TABLE partidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deporte_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora VARCHAR(255) NOT NULL,
    geolocalization_id INT NOT NULL,
    organizador_id BIGINT NOT NULL,
    cantidad_jugadores INT,
    nivel_minimo VARCHAR(255),
    nivel_maximo VARCHAR(255),
    estado VARCHAR(255),
    estrategia_emparejamiento VARCHAR(255),
    equipo_ganador_id BIGINT,
    FOREIGN KEY (deporte_id) REFERENCES deportes(id),
    FOREIGN KEY (organizador_id) REFERENCES usuarios(id),
    FOREIGN KEY (equipo_ganador_id) REFERENCES equipos(id)
);

CREATE TABLE partido_equipos (
    partido_id BIGINT NOT NULL,
    equipo_id BIGINT NOT NULL,
    PRIMARY KEY (partido_id, equipo_id),
    FOREIGN KEY (partido_id) REFERENCES partidos(id),
    FOREIGN KEY (equipo_id) REFERENCES equipos(id)
);

CREATE TABLE equipo_jugadores (
    equipo_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (equipo_id, usuario_id),
    FOREIGN KEY (equipo_id) REFERENCES equipos(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE usuario_deporte (
    usuario_id BIGINT NOT NULL,
    deporte_id BIGINT NOT NULL,
    nivel_juego VARCHAR(255) NOT NULL,
    deporte_favorito BOOLEAN NOT NULL,
    PRIMARY KEY (usuario_id, deporte_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (deporte_id) REFERENCES deportes(id)
);

CREATE TABLE comentarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mensaje TEXT NOT NULL,
    fecha DATE NOT NULL,
    partido_id BIGINT NOT NULL,
    jugador_id BIGINT NOT NULL,
    FOREIGN KEY (partido_id) REFERENCES partidos(id),
    FOREIGN KEY (jugador_id) REFERENCES usuarios(id)
);

CREATE TABLE estadisticas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    partido_id BIGINT NOT NULL,
    jugador_id BIGINT NOT NULL,
    anotaciones INT,
    asistencias INT,
    amonestaciones INT,
    mejor_jugador BOOLEAN,
    FOREIGN KEY (partido_id) REFERENCES partidos(id),
    FOREIGN KEY (jugador_id) REFERENCES usuarios(id)
);