ALTER TABLE empleados
ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE empleados
SET activo = TRUE
WHERE activo IS NULL;

CREATE INDEX IF NOT EXISTS idx_empleados_activo ON empleados (activo);
