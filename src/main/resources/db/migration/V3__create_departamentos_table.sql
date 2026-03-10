CREATE TABLE IF NOT EXISTS departamentos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX IF NOT EXISTS idx_departamentos_nombre ON departamentos (nombre);
CREATE INDEX IF NOT EXISTS idx_departamentos_activo ON departamentos (activo);

ALTER TABLE empleados
    ADD COLUMN IF NOT EXISTS departamento_id BIGINT;

ALTER TABLE empleados
    DROP CONSTRAINT IF EXISTS fk_empleado_departamento;

ALTER TABLE empleados
    ADD CONSTRAINT fk_empleado_departamento
        FOREIGN KEY (departamento_id)
            REFERENCES departamentos (id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

CREATE INDEX IF NOT EXISTS idx_empleados_departamento_id ON empleados (departamento_id);
