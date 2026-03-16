CREATE SEQUENCE IF NOT EXISTS empleado_consecutivo_seq START WITH 1001 INCREMENT BY 1;

ALTER TABLE IF EXISTS empleado
	ADD COLUMN IF NOT EXISTS correo VARCHAR(150),
	ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255),
	ADD COLUMN IF NOT EXISTS activo BOOLEAN;

UPDATE empleado
SET correo = CONCAT('legacy+', consecutivo, '@invalid.local')
WHERE correo IS NULL;

UPDATE empleado
SET password_hash = '$2a$10$Qu8qVQjQ5bkA90xj4x4m4uA1b8upfKQ7QSh6h6vV8Eap8rjLjjmIu'
WHERE password_hash IS NULL;

UPDATE empleado
SET activo = TRUE
WHERE activo IS NULL;

ALTER TABLE IF EXISTS empleado
	ALTER COLUMN correo SET NOT NULL,
	ALTER COLUMN password_hash SET NOT NULL,
	ALTER COLUMN activo SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_empleado_correo_lower
	ON empleado (LOWER(correo));

CREATE TABLE IF NOT EXISTS evento_autenticacion (
	id BIGSERIAL PRIMARY KEY,
	correo_intentado VARCHAR(150),
	resultado VARCHAR(20) NOT NULL,
	motivo VARCHAR(80) NOT NULL,
	origen_solicitud VARCHAR(120),
	fecha_hora TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_evento_auth_fecha_hora
	ON evento_autenticacion (fecha_hora);

CREATE INDEX IF NOT EXISTS ix_evento_auth_correo_fecha
	ON evento_autenticacion (correo_intentado, fecha_hora);
