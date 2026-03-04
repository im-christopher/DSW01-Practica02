CREATE TABLE IF NOT EXISTS empleados (
    clave VARCHAR(5) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    telefono VARCHAR(100) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_empleados_nombre ON empleados (nombre);
