CREATE DATABASE IF NOT EXISTS GestionSeguros;
USE GestionSeguros;

DROP TABLE IF EXISTS Vehiculo;
DROP TABLE IF EXISTS SeguroVehicular;

CREATE TABLE SeguroVehicular (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN DEFAULT FALSE,
    aseguradora VARCHAR(80) NOT NULL,
    nroPoliza VARCHAR(50) NOT NULL UNIQUE,
    cobertura ENUM('RC', 'TERCEROS', 'TODO_RIESGO') NOT NULL,
    vencimiento DATE NOT NULL
);

CREATE TABLE Vehiculo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN DEFAULT FALSE,
    dominio VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT,
    nroChasis VARCHAR(50) NOT NULL UNIQUE,
    seguro_id BIGINT UNIQUE,
    FOREIGN KEY (seguro_id) REFERENCES SeguroVehicular(id)
);

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE Vehiculo;
TRUNCATE TABLE SeguroVehicular;
SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS temp_multiplicador;
CREATE TEMPORARY TABLE temp_multiplicador (id INT);
INSERT INTO temp_multiplicador
WITH RECURSIVE nums AS (
     SELECT 1 AS id
     UNION ALL
     SELECT id + 1 FROM nums WHERE id < 1000
)
SELECT id FROM nums;

DROP TEMPORARY TABLE IF EXISTS temp_aseguradoras;
CREATE TEMPORARY TABLE temp_aseguradoras (nombre VARCHAR(80));
INSERT INTO temp_aseguradoras VALUES
('La Caja'), ('Sancor Seguros'), ('Rivadavia'), ('Allianz'), ('Zurich'), ('Mercantil Andina');

DROP TEMPORARY TABLE IF EXISTS temp_coberturas;
CREATE TEMPORARY TABLE temp_coberturas (cobertura ENUM('RC', 'TERCEROS', 'TODO_RIESGO'));
INSERT INTO temp_coberturas VALUES
('RC'), ('TERCEROS'), ('TODO_RIESGO');

DROP TEMPORARY TABLE IF EXISTS temp_marcas;
CREATE TEMPORARY TABLE temp_marcas (marca VARCHAR(50));
INSERT INTO temp_marcas VALUES
('Ford'), ('Volkswagen'), ('Chevrolet'), ('Renault'), ('Peugeot'), ('Toyota'), ('Fiat');

DROP TEMPORARY TABLE IF EXISTS temp_modelos;
CREATE TEMPORARY TABLE temp_modelos (modelo VARCHAR(50));
INSERT INTO temp_modelos VALUES
('Focus'), ('Gol'), ('Onix'), ('Clio'), ('208'), ('Corolla'), ('Cronos'), ('Ranger'), ('Amarok');

DROP TABLE IF EXISTS temp_letras;
CREATE TEMPORARY TABLE temp_letras (letra CHAR(1));
INSERT INTO temp_letras VALUES
('A'),('B'),('C'),('D'),('E'),('F'),('G'),('H'),('I'),('J'),
('K'),('L'),('M'),('N'),('O'),('P'),('Q'),('R'),('S'),('T'),
('U'),('V'),('W'),('X'),('Y'),('Z');

DROP TABLE IF EXISTS temp_digitos;
CREATE TEMPORARY TABLE temp_digitos (digito CHAR(1));
INSERT INTO temp_digitos VALUES
('0'),('1'),('2'),('3'),('4'),('5'),('6'),('7'),('8'),('9');

DROP TABLE IF EXISTS temp_letras2, temp_letras3, temp_letras4;
CREATE TEMPORARY TABLE temp_letras2 AS SELECT * FROM temp_letras;
CREATE TEMPORARY TABLE temp_letras3 AS SELECT * FROM temp_letras;
CREATE TEMPORARY TABLE temp_letras4 AS SELECT * FROM temp_letras;

DROP TABLE IF EXISTS temp_digitos2, temp_digitos3;
CREATE TEMPORARY TABLE temp_digitos2 AS SELECT * FROM temp_digitos;
CREATE TEMPORARY TABLE temp_digitos3 AS SELECT * FROM temp_digitos;

INSERT INTO SeguroVehicular (aseguradora, nroPoliza, cobertura, vencimiento)
SELECT
    (SELECT nombre FROM temp_aseguradoras ORDER BY RAND() LIMIT 1) AS aseguradora,
    CONCAT('POL-', LPAD(t.id, 8, '0')) AS nroPoliza,
    (SELECT cobertura FROM temp_coberturas ORDER BY RAND() LIMIT 1) AS cobertura,
    DATE_ADD('2024-01-01',
        INTERVAL FLOOR(RAND() * (DATEDIFF('2030-12-31', '2024-01-01'))) DAY
    ) AS vencimiento
FROM
    temp_multiplicador t;

INSERT INTO Vehiculo (dominio, marca, modelo, anio, nroChasis, seguro_id)
SELECT
    CONCAT(
        l1.letra, l2.letra,
        d1.digito, d2.digito, d3.digito,
        l3.letra, l4.letra
    ) AS dominio,
    
    (SELECT marca FROM temp_marcas ORDER BY RAND() LIMIT 1) AS marca,
    (SELECT modelo FROM temp_modelos ORDER BY RAND() LIMIT 1) AS modelo,
    (FLOOR(RAND() * (2025 - 2005 + 1)) + 2005) AS anio,
    CONCAT('CHAS-', LPAD(@rownum := @rownum + 1, 8, '0')) AS nroChasis,
    
    NULL AS seguro_id
FROM
    temp_letras l1
    CROSS JOIN temp_letras2 l2
    CROSS JOIN temp_digitos d1
    CROSS JOIN temp_digitos2 d2
    CROSS JOIN temp_digitos3 d3
    CROSS JOIN temp_letras3 l3
    CROSS JOIN temp_letras4 l4
    CROSS JOIN (SELECT @rownum := 0) r
LIMIT 10000;

WITH 
SegurosDisponibles AS (
    SELECT id AS seguro_id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM SeguroVehicular
),
VehiculosParaAsignar AS (
    SELECT id AS vehiculo_id, ROW_NUMBER() OVER (ORDER BY RAND()) AS rn
    FROM Vehiculo
    WHERE seguro_id IS NULL
    LIMIT 1000 
)
UPDATE Vehiculo v
JOIN VehiculosParaAsignar vpa ON v.id = vpa.vehiculo_id
JOIN SegurosDisponibles sd ON vpa.rn = sd.rn
SET v.seguro_id = sd.seguro_id;

DROP TEMPORARY TABLE IF EXISTS temp_multiplicador;
DROP TEMPORARY TABLE IF EXISTS temp_aseguradoras;
DROP TEMPORARY TABLE IF EXISTS temp_coberturas;
DROP TEMPORARY TABLE IF EXISTS temp_marcas;
DROP TEMPORARY TABLE IF EXISTS temp_modelos;
DROP TEMPORARY TABLE IF EXISTS temp_letras;
DROP TEMPORARY TABLE IF EXISTS temp_letras2;
DROP TEMPORARY TABLE IF EXISTS temp_letras3;
DROP TEMPORARY TABLE IF EXISTS temp_letras4;
DROP TEMPORARY TABLE IF EXISTS temp_digitos;
DROP TEMPORARY TABLE IF EXISTS temp_digitos2;
DROP TEMPORARY TABLE IF EXISTS temp_digitos3;

